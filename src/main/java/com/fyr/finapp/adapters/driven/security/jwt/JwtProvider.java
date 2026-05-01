package com.fyr.finapp.adapters.driven.security.jwt;

import com.fyr.finapp.adapters.driven.security.user.SecurityUser;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
@Slf4j
public class JwtProvider {

    private static final String ROLES_CLAIM = "roles";
    private static final String EMAIL_CLAIM = "email";
    private static final String FULLNAME_CLAIM = "fullName";
    private static final String USERNAME_CLAIM = "username";
    private static final long MILLIS_PER_SECOND = 1_000L;

    private final JwtProperties jwtProperties;
    private final SecretKey signingKey;

    public JwtProvider(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        this.signingKey = buildSigningKey(jwtProperties.getSecret());
    }

    public String generateToken(SecurityUser user) {
        var roles = user.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        long nowMillis = System.currentTimeMillis();
        Date issuedAt = new Date(nowMillis);
        Date expiresAt = new Date(nowMillis + jwtProperties.getExpirationInSeconds() * MILLIS_PER_SECOND);

        return Jwts.builder()
                .subject(user.getId().toString())
                .claim(ROLES_CLAIM, roles)
                .claim(EMAIL_CLAIM, user.getEmail())
                .claim(FULLNAME_CLAIM, user.getFullName())
                .claim(USERNAME_CLAIM, user.getUsername())
                .issuedAt(issuedAt)
                .expiration(expiresAt)
                .issuer(jwtProperties.getIssuer())
                .signWith(signingKey, Jwts.SIG.HS256)
                .compact();
    }

    public Claims getClaims(String token) {
        return parser()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String extractEmail(String token) {
        return getClaims(token).get(EMAIL_CLAIM, String.class);
    }

    public boolean isTokenValid(String token) {
        try {
            parser().parseSignedClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.debug("JWT expired");
        } catch (UnsupportedJwtException e) {
            log.warn("Unsupported JWT: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.warn("Malformed JWT: {}", e.getMessage());
        } catch (SignatureException e) {
            log.warn("Invalid JWT signature: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.warn("Invalid JWT argument: {}", e.getMessage());
        }
        return false;
    }

    private JwtParser parser() {
        return Jwts.parser()
                .verifyWith(signingKey)
                .requireIssuer(jwtProperties.getIssuer())
                .build();
    }

    private SecretKey buildSigningKey(String secret) {
        byte[] secretBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(secretBytes);
    }
}
