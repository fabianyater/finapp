package com.fyr.finapp.adapters.driven.security.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtProvider jwtProvider;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        String token = resolveBearerToken(authHeader);

        if (token == null) {
            filterChain.doFilter(request, response);
            return;
        }

        // Token present — validate and authenticate, or return 401 directly
        try {
            String email = jwtProvider.extractEmail(token);
            if (email == null || email.isBlank()) {
                writeUnauthorized(response, "INVALID_TOKEN", "Token is missing required claims");
                return;
            }

            UserDetails userDetails = userDetailsService.loadUserByUsername(email);

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (ExpiredJwtException e) {
            log.warn("Expired JWT on {} {}", request.getMethod(), request.getRequestURI());
            writeUnauthorized(response, "TOKEN_EXPIRED", "Session expired, please log in again");
            return;
        } catch (UnsupportedJwtException | MalformedJwtException | SignatureException e) {
            log.warn("Malformed JWT on {} {}: {}", request.getMethod(), request.getRequestURI(), e.getMessage());
            writeUnauthorized(response, "INVALID_TOKEN", "Invalid token");
            return;
        } catch (UsernameNotFoundException e) {
            log.warn("Token references unknown user on {} {}: {}", request.getMethod(), request.getRequestURI(), e.getMessage());
            writeUnauthorized(response, "USER_NOT_FOUND", "User account no longer exists");
            return;
        } catch (Exception e) {
            log.error("Unexpected error processing JWT on {} {}", request.getMethod(), request.getRequestURI(), e);
            SecurityContextHolder.clearContext();
            writeUnauthorized(response, "AUTH_ERROR", "Authentication failed");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private String resolveBearerToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) return null;
        String token = authHeader.substring(7).trim();
        return token.isEmpty() ? null : token;
    }

    private void writeUnauthorized(HttpServletResponse response, String status, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(
                String.format("{\"status\":\"%s\",\"code\":401,\"message\":\"%s\",\"errors\":{}}", status, message)
        );
    }
}
