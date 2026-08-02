package io.github.opencivilizationplatform.authorization;

import io.github.opencivilizationplatform.config.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Order(1)
public class RoleExtractorFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final Map<String, String> userRoles = new ConcurrentHashMap<>();

    public RoleExtractorFilter(JwtService jwtService) {
        this.jwtService = jwtService;
        userRoles.put("admin", Role.ADMIN.name());
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String clientId = (String) request.getAttribute("X-Client-Id");
        if (clientId == null) {
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                try {
                    clientId = jwtService.extractClientId(authHeader.substring(7));
                } catch (Exception ignored) {}
            }
        }
        if (clientId != null) {
            String role = userRoles.getOrDefault(clientId, Role.CITIZEN.name());
            request.setAttribute("X-User-Role", role);
        } else {
            request.setAttribute("X-User-Role", Role.READ_ONLY.name());
        }
        filterChain.doFilter(request, response);
    }
}
