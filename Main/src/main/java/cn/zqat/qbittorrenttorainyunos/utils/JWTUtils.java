package cn.zqat.qbittorrenttorainyunos.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.MacAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JWTUtils {

    @Value("${jwt.secret}")
    private String secret;
    private final MacAlgorithm algo = Jwts.SIG.HS256;
    @Value("${jwt.expiration}")
    private Long expiration;
    private volatile SecretKey key;

    public SecretKey getSignKey() {
        if(key != null){
            return key;
        }
        key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secret));
        return key;
    }

    public String getToken(Integer id,String username){
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", id);
        claims.put("username",username);
        Date d = new Date();
        return Jwts.builder()
                .claims(claims)
                .issuedAt(d)
                .expiration(new Date(d.getTime() + expiration))
                .signWith(getSignKey(),algo)
                .compact();
    }

    public Claims extractAllClaims(String token){
        return Jwts.parser()
                .verifyWith(getSignKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean isValid(String token){
        try {
            Claims claims = extractAllClaims(token);
            Date exp = claims.getExpiration();
            return exp != null && exp.after(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

}
