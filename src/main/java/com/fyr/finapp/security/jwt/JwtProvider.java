package com.fyr.finapp.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
@Slf4j
public class JwtProvider {

    private static final String ROLES_CLAIM = "roles";
    private static final long MILLIS_PER_SECOND = 1_000L;

    private final JwtProperties jwtProperties;
    private final SecretKey signingKey;

    public JwtProvider(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        this.signingKey = buildSigningKey(jwtProperties.getSecret());
    }

    public String generateToken(UserDetails userDetails) {
        var roles = userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        long nowMillis = System.currentTimeMillis();
        Date issuedAt = new Date(nowMillis);
        Date expiresAt = new Date(nowMillis + jwtProperties.getExpirationInSeconds() * MILLIS_PER_SECOND);

        return Jwts.builder()
                .subject(userDetails.getUsername())
                .claim(ROLES_CLAIM, roles)
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

    public String getSubject(String token) {
        return getClaims(token).getSubject();
    }

    public boolean validate(String token) {
        try {
            parser().parseSignedClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.error("token expired");
        } catch (UnsupportedJwtException e) {
            log.error("token unsupported");
        } catch (MalformedJwtException e) {
            log.error("token malformed");
        } catch (SignatureException e) {
            log.error("bad signature");
        } catch (IllegalArgumentException e) {
            log.error("illegal args");
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
