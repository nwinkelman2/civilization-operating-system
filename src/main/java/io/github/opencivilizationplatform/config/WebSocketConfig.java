package io.github.opencivilizationplatform.config;

import io.github.opencivilizationplatform.web.handler.NexusWebSocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final NexusWebSocketHandler nexusHandler;
    private final WebSocketAuthInterceptor authInterceptor;

    public WebSocketConfig(NexusWebSocketHandler nexusHandler, WebSocketAuthInterceptor authInterceptor) {
        this.nexusHandler = nexusHandler;
        this.authInterceptor = authInterceptor;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(nexusHandler, "/ws/nexus")
            .setAllowedOrigins("*")
            .addInterceptors(authInterceptor);
    }
}
