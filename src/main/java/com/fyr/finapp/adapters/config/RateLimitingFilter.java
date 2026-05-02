package com.fyr.finapp.adapters.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Order(-200)
public class RateLimitingFilter extends OncePerRequestFilter {
    private static final Logger log = LoggerFactory.getLogger(RateLimitingFilter.class);

    // Auth endpoints: 20 requests / 10 minutes per IP (brute-force protection)
    private final Map<String, Bucket> authBuckets = new ConcurrentHashMap<>();

    // General API endpoints: 200 requests / minute per IP
    private final Map<String, Bucket> apiBuckets = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain) throws ServletException, IOException {

        String path = request.getRequestURI();

        // Skip health checks — Railway polls this frequently
        if (path.startsWith("/actuator/")) {
            chain.doFilter(request, response);
            return;
        }

        String ip = resolveClientIp(request);
        Bucket bucket = isAuthEndpoint(path)
                ? authBuckets.computeIfAbsent(ip, k -> newAuthBucket())
                : apiBuckets.computeIfAbsent(ip, k -> newApiBucket());

        if (bucket.tryConsume(1)) {
            chain.doFilter(request, response);
        } else {
            log.warn("Rate limit exceeded for IP={} path={} method={}", ip, path, request.getMethod());
            writeTooManyRequests(response, isAuthEndpoint(path));
        }
    }

    private boolean isAuthEndpoint(String path) {
        return path.contains("/auth/");
    }

    private String resolveClientIp(HttpServletRequest request) {
        // Railway sits behind a proxy — X-Forwarded-For contains the real client IP
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private Bucket newAuthBucket() {
        // 20 tokens refilled all-at-once every 10 minutes
        Bandwidth limit = Bandwidth.classic(20, Refill.intervally(20, Duration.ofMinutes(10)));
        return Bucket.builder().addLimit(limit).build();
    }

    private Bucket newApiBucket() {
        // 200 tokens refilled all-at-once every minute
        Bandwidth limit = Bandwidth.classic(200, Refill.intervally(200, Duration.ofMinutes(1)));
        return Bucket.builder().addLimit(limit).build();
    }

    private void writeTooManyRequests(HttpServletResponse response, boolean isAuth) throws IOException {
        String message = isAuth
                ? "Too many login attempts, please wait 10 minutes"
                : "Too many requests, please slow down";

        response.setStatus(429);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(
                String.format("{\"status\":\"TOO_MANY_REQUESTS\",\"code\":429,\"message\":\"%s\",\"errors\":{}}", message)
        );
    }
}
