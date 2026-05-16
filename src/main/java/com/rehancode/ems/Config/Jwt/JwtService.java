package com.rehancode.ems.Config.Jwt;



import com.rehancode.ems.Config.DetailsService.UserPrinicple;
import com.rehancode.ems.Model.UsersModel;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.xml.crypto.Data;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

@Service
public class JwtService {
    @Value("${jwt.secret}")
    private String key;


    public SecretKey getKey(){
        return Keys.hmacShaKeyFor(key.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(UsersModel usersModel){
        Map<String,Object> claims=new HashMap<>();

        claims.put("UserID",usersModel.getId());
        claims.put("Roles",usersModel.getRole());
        Date now = new Date(System.currentTimeMillis());


        long expirationTime = 30 * 60 * 1000; // 30 minutes

        Date exp = new Date(System.currentTimeMillis() + expirationTime);

        return Jwts.builder()
                .claims(claims)
                .subject(usersModel.getUsername())
                .issuedAt(now)
                .expiration(exp)
                .id(UUID.randomUUID().toString())
                .signWith(getKey())
                .compact();

    }
    public String extractUsername(String token) {
        return extractClaims(token,Claims::getSubject);
    }
    public Date extractExpiration(String token){
        return extractClaims(token,Claims::getExpiration);
    }
    public boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date());
    }


    public boolean validateToken(String token, UserDetails userDetails) {
        String username=extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
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
