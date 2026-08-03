package io.github.opencivilizationplatform.modules.life.application;

import io.github.opencivilizationplatform.modules.life.domain.AgentPersonality;
import io.github.opencivilizationplatform.modules.life.domain.EpisodicMemoryEvent;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class AgentPersonalityService {

    private final Map<String, List<EpisodicMemoryEvent>> agentMemories = new ConcurrentHashMap<>();

    public record DecisionWeights(
        double cooperationWeight,
        double riskWeight,
        double innovationWeight
    ) {}

    public EpisodicMemoryEvent addMemoryEvent(String agentId, String eventType, String description, double impactScore) {
        String eventId = UUID.randomUUID().toString();
        EpisodicMemoryEvent event = new EpisodicMemoryEvent(eventId, Instant.now(), eventType, description, impactScore);
        addMemoryEvent(agentId, event);
        return event;
    }

    public void addMemoryEvent(String agentId, EpisodicMemoryEvent event) {
        if (agentId == null || agentId.isBlank() || event == null) {
            return;
        }
        agentMemories.computeIfAbsent(agentId, k -> new CopyOnWriteArrayList<>()).add(event);
    }

    public List<EpisodicMemoryEvent> getAgentMemories(String agentId) {
        if (agentId == null || !agentMemories.containsKey(agentId)) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(agentMemories.get(agentId));
    }

    public DecisionWeights evaluateDecisionWeights(AgentPersonality personality) {
        return evaluateDecisionWeights(personality, Collections.emptyList());
    }

    public DecisionWeights evaluateDecisionWeights(AgentPersonality personality, List<EpisodicMemoryEvent> events) {
        if (personality == null) {
            personality = new AgentPersonality(0.5, 0.5, 0.5);
        }

        if (events == null || events.isEmpty()) {
            return new DecisionWeights(
                personality.cooperationIndex(),
                personality.riskTolerance(),
                personality.innovationFocus()
            );
        }

        double totalImpact = events.stream()
            .mapToDouble(EpisodicMemoryEvent::impactScore)
            .sum();

        double impactModifier = totalImpact * 0.05;

        double cooperation = Math.max(0.0, Math.min(1.0, personality.cooperationIndex() + impactModifier));
        double risk = Math.max(0.0, Math.min(1.0, personality.riskTolerance() + (impactModifier * 0.5)));
        double innovation = Math.max(0.0, Math.min(1.0, personality.innovationFocus() + (impactModifier * 0.2)));

        return new DecisionWeights(cooperation, risk, innovation);
    }

    public DecisionWeights evaluateAgentDecisionWeights(String agentId, AgentPersonality personality) {
        List<EpisodicMemoryEvent> memories = getAgentMemories(agentId);
        return evaluateDecisionWeights(personality, memories);
    }
}
