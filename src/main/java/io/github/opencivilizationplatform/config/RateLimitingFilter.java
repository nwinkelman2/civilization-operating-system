package io.github.opencivilizationplatform.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;

@Component
@Order(1)
public class RateLimitingFilter implements Filter {

    private final StringRedisTemplate redisTemplate;
    private static final int MAX_REQUESTS_PER_MINUTE = 100;

    public RateLimitingFilter(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String path = httpRequest.getRequestURI();

        if (path.startsWith("/api/v1/")) {
            String clientIp = httpRequest.getHeader("X-Forwarded-For");
            if (clientIp == null || clientIp.isEmpty() || "unknown".equalsIgnoreCase(clientIp)) {
                clientIp = httpRequest.getRemoteAddr();
            } else {
                clientIp = clientIp.split(",")[0].trim();
            }
            String redisKey = "rate:limit:" + clientIp;

            Long currentCount = redisTemplate.opsForValue().increment(redisKey);
            if (currentCount != null) {
                if (currentCount == 1L) {
                    redisTemplate.expire(redisKey, Duration.ofMinutes(1));
                } else {
                    Long ttl = redisTemplate.getExpire(redisKey);
                    if (ttl != null && ttl == -1L) {
                        redisTemplate.expire(redisKey, Duration.ofMinutes(1));
                    }
                }
            }

            if (currentCount != null && currentCount > MAX_REQUESTS_PER_MINUTE) {
                HttpServletResponse httpResponse = (HttpServletResponse) response;
                httpResponse.setStatus(429);
                httpResponse.setContentType("application/json");
                httpResponse.getWriter().write("""
                        {"error":"Too many requests","message":"Rate limit exceeded. Try again later."}
                        """);
                return;
            }
        }
        chain.doFilter(request, response);
    }
}
