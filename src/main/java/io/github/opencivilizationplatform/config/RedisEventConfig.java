package io.github.opencivilizationplatform.config;

import tools.jackson.databind.ObjectMapper;
import io.github.opencivilizationplatform.modules.voxtex.domain.VoxtexMessage;
import io.github.opencivilizationplatform.modules.voxtex.application.VoxtexMeshService;
import io.github.opencivilizationplatform.web.handler.VoxtexWebSocketHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

@Configuration
public class RedisEventConfig {

    private static final Logger log = LoggerFactory.getLogger(RedisEventConfig.class);
    public static final String CHANNEL_NAME = "voxtex-mesh-events";

    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory connectionFactory) {
        return new StringRedisTemplate(connectionFactory);
    }

    @Bean
    public RedisMessageListenerContainer container(RedisConnectionFactory connectionFactory,
                                                   MessageListenerAdapter listenerAdapter) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(listenerAdapter, new ChannelTopic(CHANNEL_NAME));
        return container;
    }

    @Bean
    public MessageListenerAdapter listenerAdapter(RedisEventSubscriber subscriber) {
        return new MessageListenerAdapter(subscriber, "receiveMessage");
    }

    @org.springframework.stereotype.Component
    public static class RedisEventSubscriber {
        private final VoxtexMeshService meshService;
        private final VoxtexWebSocketHandler webSocketHandler;
        private final ObjectMapper objectMapper;

        public RedisEventSubscriber(VoxtexMeshService meshService,
                                    VoxtexWebSocketHandler webSocketHandler,
                                    ObjectMapper objectMapper) {
            this.meshService = meshService;
            this.webSocketHandler = webSocketHandler;
            this.objectMapper = objectMapper;
        }

        public void receiveMessage(String message) {
            try {
                VoxtexMessage msg = objectMapper.readValue(message, VoxtexMessage.class);
                log.info("Received event from Redis Pub/Sub: {} -> {}", msg.getSourceNode().getName(), msg.getTargetNode().getName());
                
                // 1. Trigger local SSE
                meshService.notifyListenersLocally(msg);
                
                // 2. Trigger local WebSockets
                webSocketHandler.broadcastMessageLocally(msg);
            } catch (Exception e) {
                log.error("Failed to process synchronized Redis event", e);
            }
        }
    }
}
