package com.example.intern.filter;

import com.example.intern.dto.LoginRequestDto;
import com.example.intern.entity.User;
import com.example.intern.jwt.JwtUtil;
import com.example.intern.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j(topic = "JwtAuthenticationFilter")
@Component
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final UserRepository userRepository;

    public JwtAuthenticationFilter(UserRepository userRepository) {
        this.userRepository = userRepository;

        setFilterProcessesUrl("/api/auth/login");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            LoginRequestDto requestDto = new ObjectMapper().readValue(request.getInputStream(), LoginRequestDto.class);

            return getAuthenticationManager().authenticate(
                    new UsernamePasswordAuthenticationToken(requestDto.getUsername(), requestDto.getPassword(), null)
            );
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }


    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException {
        String username = ((UserDetails) authResult.getPrincipal()).getUsername();

        String token = JwtUtil.createToken(username, JwtUtil.ACCESS_TOKEN_EXPIRATION);
        String refresh = JwtUtil.createToken(username, JwtUtil.REFRESH_TOKEN_EXPIRATION);

        saveRefreshToken(username, refresh);

        response.addHeader(JwtUtil.AUTHORIZATION_HEADER, token);

        responseSetting(response, 200, "로그인에 성공하였습니다.");
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException {
        responseSetting(response, 401, "로그인에 실패하였습니다.");
    }


    private void saveRefreshToken(String username, String refresh) {
        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new IllegalArgumentException("not found user")
        );
        user.updateRefresh(refresh);
        userRepository.save(user);
    }

    private void responseSetting(HttpServletResponse response, int statusCode, String message) throws IOException {
        response.setStatus(statusCode);
        response.setContentType("text/plain;charset=UTF-8");
        response.getWriter().write(message);

    }
}