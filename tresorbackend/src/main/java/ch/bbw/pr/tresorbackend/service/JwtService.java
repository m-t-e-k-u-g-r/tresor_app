package ch.bbw.pr.tresorbackend.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JwtService
 *
 * @author Matteo Krüger
 */
@Service
public class JwtService {
    private final SecretKey SECRET_KEY;

    public JwtService(@Value("${jwt.signing-key}") String encodedKey) {
        byte[] bytes = Base64.getDecoder().decode(encodedKey);
        if (bytes.length != 32) {
            throw new IllegalArgumentException("Invalid JWT signing key");
        }
        this.SECRET_KEY = Keys.hmacShaKeyFor(bytes);
    }

    public String generateToken(String email, String encryptedKEK) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("encryptedKey", encryptedKEK);

        return Jwts.builder()
                .claims(claims)
                .subject(email)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
                .signWith(this.SECRET_KEY)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(this.SECRET_KEY)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String extractEmail(String token) {
        return Jwts.parser()
                .verifyWith(this.SECRET_KEY)
                .build().parseSignedClaims(token).getPayload()
                .getSubject();
    }

    public String extractEncryptedKEK(String token) {
        return Jwts.parser()
                .verifyWith(this.SECRET_KEY)
                .build().parseSignedClaims(token).getPayload()
                .get("encryptedKey", String.class);
    }
}
