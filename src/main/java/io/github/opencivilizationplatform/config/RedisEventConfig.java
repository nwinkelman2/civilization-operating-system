package io.github.opencivilizationplatform.config;

import tools.jackson.databind.ObjectMapper;
import io.github.opencivilizationplatform.modules.nexus.domain.NexusMessage;
import io.github.opencivilizationplatform.modules.nexus.domain.NexusNode;
import io.github.opencivilizationplatform.modules.nexus.dto.NexusMessageSyncDTO;
import io.github.opencivilizationplatform.modules.nexus.application.NexusMeshService;
import io.github.opencivilizationplatform.web.handler.NexusWebSocketHandler;
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
    public static final String CHANNEL_NAME = "Nexus-mesh-events";

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
        private final NexusMeshService meshService;
        private final NexusWebSocketHandler webSocketHandler;
        private final ObjectMapper objectMapper;

        public RedisEventSubscriber(NexusMeshService meshService,
                                    NexusWebSocketHandler webSocketHandler,
                                    ObjectMapper objectMapper) {
            this.meshService = meshService;
            this.webSocketHandler = webSocketHandler;
            this.objectMapper = objectMapper;
        }

        public void receiveMessage(String message) {
            try {
                NexusMessageSyncDTO dto = objectMapper.readValue(message, NexusMessageSyncDTO.class);
                
                // Map back to lightweight shell NexusMessage
                NexusMessage msg = new NexusMessage();
                msg.setId(dto.getId());
                msg.setMessageType(dto.getMessageType());
                msg.setContent(dto.getContent());
                msg.setHopCount(dto.getHopCount());
                
                if (dto.getSourceNodeId() != null) {
                    NexusNode source = new NexusNode();
                    source.setId(dto.getSourceNodeId());
                    source.setName(dto.getSourceNodeName());
                    msg.setSourceNode(source);
                }
                
                if (dto.getTargetNodeId() != null) {
                    NexusNode target = new NexusNode();
                    target.setId(dto.getTargetNodeId());
                    target.setName(dto.getTargetNodeName());
                    msg.setTargetNode(target);
                }
                
                log.info("Received event from Redis Pub/Sub: {} -> {}", 
                    msg.getSourceNode() != null ? msg.getSourceNode().getName() : "null", 
                    msg.getTargetNode() != null ? msg.getTargetNode().getName() : "null");
                
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

