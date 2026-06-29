package io.github.opencivilizationplatform.config;

import io.github.opencivilizationplatform.web.handler.NexusWebSocketHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final NexusWebSocketHandler NexusHandler;

    public WebSocketConfig(NexusWebSocketHandler NexusHandler) {
        this.NexusHandler = NexusHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(NexusHandler, "/ws/nexus")
            .setAllowedOrigins("*");
    }
}

