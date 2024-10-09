package com.example.intern.filter;

import com.example.intern.entity.TokenError;
import com.example.intern.entity.User;
import com.example.intern.jwt.JwtUtil;
import com.example.intern.repository.UserRepository;
import com.example.intern.service.UserDetailsImpl;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Objects;

@Slf4j(topic = "JwtAuthorizationFilter")
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {
    private final UserDetailsService userDetailsService;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String token = JwtUtil.getJwtTokenFromHeader(request);

        if (token != null) {
            try {
                TokenError tokenError = JwtUtil.validateToken(token);

                if (tokenError == TokenError.VALID) {
                    Claims claims = JwtUtil.getUserInfoFromToken(token);
                    String username = claims.getSubject();

                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } else if (tokenError == TokenError.EXPRIED) {
                    // 만료된 토큰 처리 (새로운 액세스 토큰 생성)
                    updateAccessToken(response, token);
                    return; // 새로운 토큰이 생성되었으므로 필터 체인을 종료
                } else {
                    handleTokenError(response, token);
                    return;
                }
            } catch (Exception e) {
                log.error("JWT validation failed: {}", e.getMessage());
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("text/plain;charset=UTF-8");
                response.getWriter().write("유효하지 않은 토큰입니다.");
                return;
            }
        }

        chain.doFilter(request, response);
    }

    public void setAuthentication(String username) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Authentication authentication = createAuthentication(username);
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
    }

    private Authentication createAuthentication(String username) {
        User user = userRepository.findByUsername(username).orElseThrow();
        UserDetailsImpl userDetailsImpl = new UserDetailsImpl(user);
        return new UsernamePasswordAuthenticationToken(userDetailsImpl, null, user.getAuthorities());
    }

    private void handleTokenError(HttpServletResponse response, String token) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("text/plain;charset=UTF-8");
        response.getWriter().write("유효하지 않은 토큰입니다.");
    }

    private void updateAccessToken(HttpServletResponse response, String token) throws IOException {
        String username = JwtUtil.getUserInfoFromToken(token).getSubject();
        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다.")
        );

        String refresh = user.getRefreshToken().substring(7); // Bearer 제거
        if (TokenError.VALID == JwtUtil.validateToken(refresh)) {
            // 새 액세스 토큰 생성
            String newToken = JwtUtil.createToken(user.getUsername(), JwtUtil.ACCESS_TOKEN_EXPIRATION);
            // 헤더에 새 토큰 추가 (Bearer 포함)
            response.addHeader(JwtUtil.AUTHORIZATION_HEADER, JwtUtil.BEARER_PREFIX + newToken);
        } else if (TokenError.EXPRIED == JwtUtil.validateToken(refresh)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("text/plain;charset=UTF-8");
            response.getWriter().write("refresh 토큰이 만료되었습니다.");
        } else {
            throw new RuntimeException("refresh token이 유효하지 않습니다.");
        }
    }
}