package io.github.opencivilizationplatform.web.handler;

import tools.jackson.databind.ObjectMapper;
import io.github.opencivilizationplatform.modules.nexus.application.NexusMeshService;
import io.github.opencivilizationplatform.modules.nexus.domain.NexusMessage;
import io.github.opencivilizationplatform.modules.nexus.domain.NexusMessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class NexusWebSocketHandler extends TextWebSocketHandler {

    private static final Logger log = LoggerFactory.getLogger(NexusWebSocketHandler.class);
    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final NexusMeshService meshService;
    private final ObjectMapper objectMapper;

    public NexusWebSocketHandler(NexusMeshService meshService, ObjectMapper objectMapper) {
        this.meshService = meshService;
        this.objectMapper = objectMapper;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        String clientId = (String) session.getAttributes().get("X-Client-Id");
        sessions.put(session.getId(), session);
        log.info("WebSocket connected: sessionId={}, clientId={}", session.getId(), clientId);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        var payload = objectMapper.readValue(message.getPayload(), Map.class);
        String action = (String) payload.getOrDefault("action", "");
        switch (action) {
            case "send_message" -> {
                Long sourceId = Long.valueOf(payload.get("sourceNodeId").toString());
                Long targetId = Long.valueOf(payload.get("targetNodeId").toString());
                String content = (String) payload.get("content");
                String typeStr = (String) payload.get("messageType");
                NexusMessageType msgType = NexusMessageType.valueOf(typeStr);
                meshService.sendMessage(sourceId, targetId, msgType, content);
            }
            case "ping" -> {
                session.sendMessage(new TextMessage("{\"type\":\"pong\"}"));
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(session.getId());
        log.info("WebSocket disconnected: {}", session.getId());
    }

    public int getActiveSessionCount() {
        return (int) sessions.values().stream().filter(WebSocketSession::isOpen).count();
    }

    public void broadcastMessageLocally(NexusMessage msg) {
        try {
            String json = objectMapper.writeValueAsString(Map.of(
                "type", "Nexus-message",
                "sourceNodeId", msg.getSourceNode().getId(),
                "targetNodeId", msg.getTargetNode().getId(),
                "messageType", msg.getMessageType().name(),
                "content", msg.getContent(),
                "hopCount", msg.getHopCount()
            ));
            TextMessage textMsg = new TextMessage(json);
            for (WebSocketSession s : sessions.values()) {
                if (s.isOpen()) {
                    s.sendMessage(textMsg);
                }
            }
        } catch (Exception e) {
            log.error("Failed to broadcast message locally", e);
        }
    }
}
