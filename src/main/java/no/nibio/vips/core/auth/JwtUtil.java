package no.nibio.vips.core.auth;

import java.util.Date;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;

public class JwtUtil {

    private JwtUtil() {}

    public static String generateToken(String clientId) {
        AuthConfig config = AuthConfig.getInstance();
        long nowMillis = System.currentTimeMillis();
        long expirationMillis = nowMillis + config.getExpirationMinutes() * 60 * 1000L;

        return Jwts.builder()
                .subject(clientId)
                .issuer(config.getIssuer())
                .issuedAt(new Date(nowMillis))
                .expiration(new Date(expirationMillis))
                .signWith(config.getSigningKey())
                .compact();
    }
    public static Claims validateToken(String token) {
        AuthConfig config = AuthConfig.getInstance();
        return Jwts.parser()
                .verifyWith(config.getSigningKey())
                .requireIssuer(config.getIssuer())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
