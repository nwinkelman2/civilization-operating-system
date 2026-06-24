package io.github.opencivilizationplatform.web.handler;

import tools.jackson.databind.ObjectMapper;
import io.github.opencivilizationplatform.modules.voxtex.application.VoxtexMeshService;
import io.github.opencivilizationplatform.modules.voxtex.domain.VoxtexMessage;
import io.github.opencivilizationplatform.modules.voxtex.domain.VoxtexMessageType;
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
public class VoxtexWebSocketHandler extends TextWebSocketHandler {

    private static final Logger log = LoggerFactory.getLogger(VoxtexWebSocketHandler.class);
    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final VoxtexMeshService meshService;
    private final ObjectMapper objectMapper;

    public VoxtexWebSocketHandler(VoxtexMeshService meshService, ObjectMapper objectMapper) {
        this.meshService = meshService;
        this.objectMapper = objectMapper;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.put(session.getId(), session);
        log.info("WebSocket connected: {}", session.getId());
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
                VoxtexMessageType msgType = VoxtexMessageType.valueOf(typeStr);
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

    public void broadcastMessageLocally(VoxtexMessage msg) {
        try {
            String json = objectMapper.writeValueAsString(Map.of(
                "type", "voxtex-message",
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
