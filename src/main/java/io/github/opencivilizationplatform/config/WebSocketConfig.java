package io.github.opencivilizationplatform.config;

import io.github.opencivilizationplatform.web.handler.VoxtexWebSocketHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final VoxtexWebSocketHandler voxtexHandler;
    private final WebSocketAuthInterceptor authInterceptor;

    public WebSocketConfig(VoxtexWebSocketHandler voxtexHandler, WebSocketAuthInterceptor authInterceptor) {
        this.voxtexHandler = voxtexHandler;
        this.authInterceptor = authInterceptor;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(voxtexHandler, "/ws/voxtex")
            .setAllowedOrigins("*")
            .addInterceptors(authInterceptor);
    }
}
