package io.github.opencivilizationplatform.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.net.URI;
import java.util.Map;

@Component
public class WebSocketAuthInterceptor implements HandshakeInterceptor {

    private static final Logger log = LoggerFactory.getLogger(WebSocketAuthInterceptor.class);
    private final JwtService jwtService;

    public WebSocketAuthInterceptor(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) {
        URI uri = request.getURI();
        String query = uri.getQuery();

        if (query == null || query.isBlank()) {
            log.warn("WebSocket handshake rejected: no token query parameter");
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return false;
        }

        String token = extractQueryParam(query, "token");
        if (token == null || !jwtService.isTokenValid(token)) {
            log.warn("WebSocket handshake rejected: invalid or missing JWT token");
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return false;
        }

        String clientId = jwtService.extractClientId(token);
        attributes.put("X-Client-Id", clientId);
        log.debug("WebSocket authenticated: clientId={}", clientId);
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
    }

    private String extractQueryParam(String query, String param) {
        for (String pair : query.split("&")) {
            String[] kv = pair.split("=", 2);
            if (kv.length == 2 && kv[0].equals(param)) {
                return kv[1];
            }
        }
        return null;
    }
}
