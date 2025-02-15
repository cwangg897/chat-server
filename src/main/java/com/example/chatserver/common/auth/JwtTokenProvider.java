package com.example.chatserver.common.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.security.Key;
import java.util.Date;
import javax.crypto.spec.SecretKeySpec;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {

    private final String secretKey;
    private final int expiration;
    private final Key SECRET_KEY;

    public JwtTokenProvider(@Value("${jwt.secretKey}") String secretKey, @Value("${jwt.expiration}") int expiration) {
        this.secretKey = secretKey;
        this.expiration = expiration;
        this.SECRET_KEY = new SecretKeySpec(java.util.Base64.getDecoder().decode(secretKey),
            SignatureAlgorithm.HS512.getJcaName()); // 디코딩시키고 동시에 암호화를 HS512로 암호화를 시킴
    }

    // 토큰 생성 JWT를 자바에서 만들 수 있도록 이렇게 코드를 짠거임
    public String createToken(String email, String role){
        Claims claims = Jwts.claims().setSubject(email); // payload라고 생각
        claims.put("role", role);
        Date now = new Date();
        String token = Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(now)
            .setExpiration(new Date(now.getTime()+expiration*60*1000L))
            .signWith(SECRET_KEY)
            .compact();
        return token;
    }
}
