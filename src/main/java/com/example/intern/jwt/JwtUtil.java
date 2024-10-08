package com.example.intern.jwt;

import com.example.intern.entity.TokenError;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.util.Base64;
import java.util.Date;


@Slf4j(topic = "JwtUtil")
@RequiredArgsConstructor
public class JwtUtil {
    public static final Long ACCESS_TOKEN_EXPIRATION = 60 * 60 * 1000L;
    public static final Long REFRESH_TOKEN_EXPIRATION = 24 * 60 * 60 * 1000L;
    public static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private static final Key key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(System.getenv("JWT_SECRET_KEY")));
    private static final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

    public static String createToken(String username, long expiration) {
        Date date = new Date();

        return BEARER_PREFIX +
                Jwts.builder()
                        .setSubject(username)
                        .setExpiration(new Date(date.getTime() + expiration))
                        .setIssuedAt(date)
                        .signWith(key, signatureAlgorithm)
                        .compact();
    }

    public static String getJwtTokenFromHeader(HttpServletRequest request) {

        String bearerToken = request.getHeader(JwtUtil.AUTHORIZATION_HEADER);

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public static TokenError validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return TokenError.VALID;
        } catch (SecurityException | MalformedJwtException | SignatureException e) {
            log.error("Invalid JWT signature, 유효하지 않는 JWT 서명 입니다.");
            return TokenError.INVALID_SIGN;
        } catch (ExpiredJwtException e) {
            log.error("Expired JWT token, 만료된 JWT token 입니다.");
            return TokenError.EXPRIED;
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다.");
            return TokenError.UNSUPPORTED;
        } catch (IllegalArgumentException e) {
            log.error("JWT claims is empty, 잘못된 JWT 토큰 입니다.");
            return TokenError.EMPTY_CLAIMS;
        }
    }

    public static Claims getUserInfoFromToken(String token) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }
}
