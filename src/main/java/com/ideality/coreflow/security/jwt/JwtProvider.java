package com.ideality.coreflow.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
public class JwtProvider {

    private final Key key;
    private final long accessExpirationTimeInMillis;
    private final long refreshExpirationTimeInMillis;

    public JwtProvider(
            @Value("${token.secret}") String secretKey,
            @Value("${token.access_expiration_time}") long accessExpirationTimeInMillis,
            @Value("${token.refresh_expiration_time}") long refreshExpirationTimeInMillis
    ) {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
        this.accessExpirationTimeInMillis = accessExpirationTimeInMillis;
        this.refreshExpirationTimeInMillis = refreshExpirationTimeInMillis;
    }

    // access token 생성
    public String generateAccessToken(Long userId, String employeeNum, String companySchema, List<String> roles) {
        return Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setSubject(userId.toString())
                .claim("employeeNum", employeeNum)
                .claim("companySchema", companySchema)
                .claim("roles", roles)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + accessExpirationTimeInMillis))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    // UUID 기반 refresh token 생성
    public String generateRefreshToken() {
        return UUID.randomUUID().toString();
    }

    // 토큰에서 Claims 꺼내기
    public Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // 특정 클레임 값 추출
    public <T> T getClaimFromToken(String token, String claimKey, Class<T> clazz) {
        Claims claims = getClaims(token);
        return claims.get(claimKey, clazz);
    }

    // id 추출(subject)
    public String getUserId(String token) {
        return getClaims(token).getSubject();
    }
    // 사번 추출
    public String getEmployeeNum(String token) {
        return getClaimFromToken(token, "employeeNum", String.class);
    }

    // 회사 스키마 추출
    public String getCompanySchema(String token) {
        return getClaimFromToken(token, "companySchema", String.class);
    }

    // 역할 추출
    public List<String> getRoles(String token) {
        return getClaimFromToken(token, "roles", List.class);
    }
}
