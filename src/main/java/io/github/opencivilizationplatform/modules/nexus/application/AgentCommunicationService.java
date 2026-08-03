package io.github.opencivilizationplatform.modules.nexus.application;

import io.github.opencivilizationplatform.core.eventbus.EventBus;
import io.github.opencivilizationplatform.core.eventbus.events.NexusMessageSentEvent;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class AgentCommunicationService {

    public enum DialogueIntent {
        TRADE_NEGOTIATION,
        ALLIANCE_PROPOSAL,
        KNOWLEDGE_SHARE
    }

    public record AgentDialog(
        String dialogId,
        String senderAgentId,
        String receiverAgentId,
        DialogueIntent intent,
        String content,
        Instant timestamp
    ) {}

    private final EventBus eventBus;
    private final Map<String, List<AgentDialog>> dialogHistory = new ConcurrentHashMap<>();

    public AgentCommunicationService(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    public DialogueIntent classifyIntent(String content) {
        if (content == null) {
            return DialogueIntent.KNOWLEDGE_SHARE;
        }
        String lower = content.toLowerCase(Locale.ROOT);
        if (lower.contains("trade") || lower.contains("buy") || lower.contains("sell") || lower.contains("exchange") || lower.contains("resource")) {
            return DialogueIntent.TRADE_NEGOTIATION;
        } else if (lower.contains("alliance") || lower.contains("pact") || lower.contains("treaty") || lower.contains("join") || lower.contains("union")) {
            return DialogueIntent.ALLIANCE_PROPOSAL;
        } else {
            return DialogueIntent.KNOWLEDGE_SHARE;
        }
    }

    public AgentDialog processDialog(String senderAgentId, String receiverAgentId, String content) {
        DialogueIntent intent = classifyIntent(content);
        return processDialog(senderAgentId, receiverAgentId, intent, content);
    }

    public AgentDialog processDialog(String senderAgentId, String receiverAgentId, DialogueIntent intent, String content) {
        String dialogId = UUID.randomUUID().toString();
        AgentDialog dialog = new AgentDialog(dialogId, senderAgentId, receiverAgentId, intent, content, Instant.now());

        if (senderAgentId != null && !senderAgentId.isBlank()) {
            dialogHistory.computeIfAbsent(senderAgentId, k -> new CopyOnWriteArrayList<>()).add(dialog);
        }
        if (receiverAgentId != null && !receiverAgentId.isBlank() && !Objects.equals(senderAgentId, receiverAgentId)) {
            dialogHistory.computeIfAbsent(receiverAgentId, k -> new CopyOnWriteArrayList<>()).add(dialog);
        }

        Long messageId = Math.abs((long) dialogId.hashCode());
        Long sourceId = senderAgentId != null ? Math.abs((long) senderAgentId.hashCode()) : 0L;
        Long targetId = receiverAgentId != null ? Math.abs((long) receiverAgentId.hashCode()) : 0L;

        eventBus.publish(new NexusMessageSentEvent(
            "AgentCommunicationService",
            messageId,
            sourceId,
            targetId,
            intent.name(),
            content
        ));

        return dialog;
    }

    public List<AgentDialog> getDialogHistory(String agentId) {
        if (agentId == null || !dialogHistory.containsKey(agentId)) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(dialogHistory.get(agentId));
    }
}
