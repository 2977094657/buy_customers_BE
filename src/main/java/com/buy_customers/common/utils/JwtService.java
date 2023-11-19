package com.buy_customers.common.utils;

import com.buy_customers.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author 46
 */
@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long expirationTime;

    public String generateToken(User user, Integer expirationTimeOption) {
        Claims claims = Jwts.claims().setSubject(user.getPhone());
        claims.put("userId", user.getUserId());
        claims.put("phone", user.getPhone());
        claims.put("pwd", user.getPwd());

        Date now = new Date();
        long expiryTime;
        if (expirationTimeOption != null && expirationTimeOption == 1) {
            expiryTime = 604800000L;  // 7 days
        } else {
            expiryTime = 86400000L;  // 1 day
        }
        Date expiration = new Date(now.getTime() + expiryTime);

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
            Claims claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
            return claims.getExpiration().after(new Date());
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
