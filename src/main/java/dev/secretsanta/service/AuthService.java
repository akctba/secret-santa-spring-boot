package dev.secretsanta.service;

import dev.secretsanta.config.SecretSantaProperties;
import dev.secretsanta.exception.ApiException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Date;
import java.util.HexFormat;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final SecretSantaProperties properties;
    private final byte[] signingKey;

    public AuthService(SecretSantaProperties properties) {
        this.properties = properties;
        this.signingKey = sha256(properties.getAuth().getSecret());
    }

    public String hashPassword(String password) {
        return HexFormat.of().formatHex(sha256(password));
    }

    public boolean matches(String rawPassword, String storedHash) {
        return MessageDigest.isEqual(hashPassword(rawPassword).getBytes(StandardCharsets.UTF_8),
                storedHash.getBytes(StandardCharsets.UTF_8));
    }

    public String createAccessToken(long userId) {
        Instant now = Instant.now();
        return Jwts.builder()
                .issuer(properties.getAuth().getIssuer())
                .subject(Long.toString(userId))
                .id(UUID.randomUUID().toString())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(properties.getAuth().getAccessTokenMinutes() * 60)))
                .signWith(Keys.hmacShaKeyFor(signingKey))
                .compact();
    }

    public String createRefreshToken(long userId) {
        Instant now = Instant.now();
        return Jwts.builder()
                .issuer(properties.getAuth().getIssuer())
                .subject(Long.toString(userId))
                .claim("type", "refresh")
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(properties.getAuth().getRefreshTokenDays() * 86_400)))
                .signWith(Keys.hmacShaKeyFor(signingKey))
                .compact();
    }

    public long parseAccessToken(String token) {
        var claims = Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(signingKey))
                .requireIssuer(properties.getAuth().getIssuer())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        if ("refresh".equals(claims.get("type", String.class))) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Invalid token");
        }
        return Long.parseLong(claims.getSubject());
    }

    public long parseRefreshToken(String token) {
        var claims = Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(signingKey))
                .requireIssuer(properties.getAuth().getIssuer())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        if (!"refresh".equals(claims.get("type", String.class))) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Invalid token");
        }
        return Long.parseLong(claims.getSubject());
    }

    private static byte[] sha256(String value) {
        try {
            return MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8));
        } catch (Exception exception) {
            throw new IllegalStateException("SHA-256 is unavailable", exception);
        }
    }
}
