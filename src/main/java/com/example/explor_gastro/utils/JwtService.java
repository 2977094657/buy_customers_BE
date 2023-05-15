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
        claims.put("name", user.getName());
        claims.put("phone", user.getPhone());
        claims.put("signupTime", user.getSignupTime());
        claims.put("description", user.getDescription());
        claims.put("address", user.getAddress());

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
        String name = (String) claims.get("name");
        String phone = (String) claims.get("phone");
        Date signupTime = new Date((Long) claims.get("signupTime"));
        String description = (String) claims.get("description");
        String address = (String) claims.get("address");
        Date iat = claims.getIssuedAt();
        Date exp = claims.getExpiration();

        return new User(userId, name, phone, signupTime, description, address, iat, exp);
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
        user.setName((String) claims.get("name"));
        user.setPhone((String) claims.get("phone"));
        user.setSignupTime(new Date((Long) claims.get("signupTime")));
        user.setDescription((String) claims.get("description"));
        user.setAddress((String) claims.get("address"));

        return new UsernamePasswordAuthenticationToken(user, null, null);
    }
}
