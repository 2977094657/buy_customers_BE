package com.example.explor_gastro.utils;

import com.example.explor_gastro.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import java.util.Date;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long expirationTime;

    public  String generateToken(User user) {
        Claims claims = Jwts.claims().setSubject(user.getPhone());
        claims.put("userId", user.getUserId());
        claims.put("phone", user.getPhone());
        claims.put("pwd", user.getPwd());

        Date now = new Date();
        Date expiration = new Date(now.getTime() + expirationTime);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    /**
     * 解密token
     * @param token 前段传入的token
     * @return 解析后的明文
     */
    public User parseToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody();

        Integer userId = (Integer) claims.get("userId");
        String phone = (String) claims.get("phone");
        String pwd = (String) claims.get("pwd");
        Date iat = claims.getIssuedAt();
        Date exp = claims.getExpiration();

        return new User(userId, phone, pwd, iat, exp);
    }
    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
        User user = new User();
        user.setUserId((Integer) claims.get("userId"));
        user.setPhone((String) claims.get("phone"));

        return new UsernamePasswordAuthenticationToken(user, null, null);
    }
}
