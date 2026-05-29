package com.rehancode.ems.Config.Jwt;

import com.rehancode.ems.Model.UsersModel;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

@Slf4j
@Service
public class JwtService {
    @Value("${jwt.secret}")
    private String key;


    public SecretKey getKey(){
        return Keys.hmacShaKeyFor(key.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(UsersModel usersModel){
        log.debug("Generating JWT token for username='{}' role='{}'",
                usersModel.getUsername(), usersModel.getRole());
        Map<String,Object> claims=new HashMap<>();
        claims.put("UserID",usersModel.getId());
        claims.put("Roles",usersModel.getRole());
        Date now = new Date(System.currentTimeMillis());
        long expirationTime = 30 * 60 * 1000; // 30 minutes
        Date exp = new Date(System.currentTimeMillis() + expirationTime);

        String token = Jwts.builder()
                .claims(claims)
                .subject(usersModel.getUsername())
                .issuedAt(now)
                .expiration(exp)
                .id(UUID.randomUUID().toString())
                .signWith(getKey())
                .compact();

        log.debug("JWT token generated for username='{}' expiresAt='{}'",
                usersModel.getUsername(), exp);
        return token;
    }

    public String extractUsername(String token) {
        return extractClaims(token,Claims::getSubject);
    }

    public Date extractExpiration(String token){
        return extractClaims(token,Claims::getExpiration);
    }

    public boolean isTokenExpired(String token){
        boolean expired = extractExpiration(token).before(new Date());
        if (expired) {
            log.debug("JWT token is expired");
        }
        return expired;
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        String username=extractUsername(token);
        boolean valid = username.equals(userDetails.getUsername()) && !isTokenExpired(token);
        if (!valid) {
            log.warn("JWT token validation failed for username='{}'", userDetails.getUsername());
        }
        return valid;
    }


    public <T> T extractClaims(String token, Function<Claims,T>claimsResolver ){
        final Claims claims=extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    public Claims extractAllClaims(String token){
        return Jwts
                .parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

    }
}
