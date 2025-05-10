package io.github.mdraihan27.login_system.utilities;

import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class JwtUtil {

    private final String JWT_SECRET_KEY, REFRESH_TOKEN_SECRET_KEY;

    public JwtUtil(@Value("${app.jwtKey}") String jwtSecretKey, @Value("${app.refreshTokenKey}") String refreshTokenSecretKey) {
        this.JWT_SECRET_KEY = jwtSecretKey;
        this.REFRESH_TOKEN_SECRET_KEY = refreshTokenSecretKey;
    }

    private SecretKey getSigningKey(Boolean isRefresh) {
        return isRefresh ? Keys.hmacShaKeyFor(REFRESH_TOKEN_SECRET_KEY.getBytes()) : Keys.hmacShaKeyFor(JWT_SECRET_KEY.getBytes());
    }

    public String extractUsername(String token, Boolean isRefresh) {
        Claims claims = extractAllClaims(token, isRefresh);
        return claims.getSubject();
    }

    public Date extractExpiration(String token, Boolean isRefresh) {
        return extractAllClaims(token, isRefresh).getExpiration();
    }

    private Claims extractAllClaims(String token, Boolean isRefresh) {
        return Jwts.parser()
                .verifyWith(getSigningKey(isRefresh))
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private Boolean isTokenExpired(String token, Boolean isRefresh) {
        return extractExpiration(token, isRefresh).before(new Date());
    }

    public String generateToken(String username, Boolean isRefresh) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, username, isRefresh);
    }

    private String createToken(Map<String, Object> claims, String subject, Boolean isRefresh) {
        long tokenLifetimeInMillis ;
        if(isRefresh){
            tokenLifetimeInMillis = 1000L * 60 * 60 * 24 * 90;
        }else{
            tokenLifetimeInMillis = 1000L * 60 * 15;
        }

        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .header().empty().add("typ", "JWT")
                .and()
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + tokenLifetimeInMillis))
                .signWith(getSigningKey(isRefresh))
                .compact();
    }


    public Boolean validateToken(String token, Boolean isRefresh) {
        try {
            extractAllClaims(token, isRefresh); //this will throw error is jwt in signed with the correct signing key
            return !isTokenExpired(token, isRefresh);
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
    }


}
