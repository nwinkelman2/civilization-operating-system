package io.github.opencivilizationplatform.modules.life;

import io.github.opencivilizationplatform.modules.life.application.AgentPersonalityService;
import io.github.opencivilizationplatform.modules.life.application.AgentPersonalityService.DecisionWeights;
import io.github.opencivilizationplatform.modules.life.domain.AgentPersonality;
import io.github.opencivilizationplatform.modules.life.domain.EpisodicMemoryEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AgentPersonalityServiceTest {

    private AgentPersonalityService personalityService;

    @BeforeEach
    void setUp() {
        personalityService = new AgentPersonalityService();
    }

    @Test
    void testAgentPersonalityClamping() {
        AgentPersonality personality = new AgentPersonality(1.5, -0.2, 0.8);
        assertEquals(1.0, personality.cooperationIndex());
        assertEquals(0.0, personality.riskTolerance());
        assertEquals(0.8, personality.innovationFocus());
    }

    @Test
    void testEpisodicMemoryEventCreation() {
        Instant now = Instant.now();
        EpisodicMemoryEvent event = new EpisodicMemoryEvent("evt-1", now, "TRADE_SUCCESS", "Completed trade", 0.5);

        assertEquals("evt-1", event.eventId());
        assertEquals(now, event.timestamp());
        assertEquals("TRADE_SUCCESS", event.eventType());
        assertEquals("Completed trade", event.description());
        assertEquals(0.5, event.impactScore());
    }

    @Test
    void testAddAndGetAgentMemories() {
        String agentId = "agent-42";
        assertTrue(personalityService.getAgentMemories(agentId).isEmpty());

        EpisodicMemoryEvent event = personalityService.addMemoryEvent(agentId, "ALLIANCE", "Formed alliance", 0.8);
        assertNotNull(event);
        assertNotNull(event.eventId());

        List<EpisodicMemoryEvent> memories = personalityService.getAgentMemories(agentId);
        assertEquals(1, memories.size());
        assertEquals("ALLIANCE", memories.get(0).eventType());
    }

    @Test
    void testEvaluateDecisionWeightsWithoutMemories() {
        AgentPersonality personality = new AgentPersonality(0.6, 0.4, 0.7);
        DecisionWeights weights = personalityService.evaluateDecisionWeights(personality);

        assertEquals(0.6, weights.cooperationWeight());
        assertEquals(0.4, weights.riskWeight());
        assertEquals(0.7, weights.innovationWeight());
    }

    @Test
    void testEvaluateDecisionWeightsWithMemories() {
        String agentId = "agent-100";
        AgentPersonality personality = new AgentPersonality(0.5, 0.5, 0.5);

        personalityService.addMemoryEvent(agentId, "SUCCESSFUL_TRADE", "Profitable trade", 2.0);
        personalityService.addMemoryEvent(agentId, "RESOURCES_FOUND", "Discovered iron ore", 1.0);

        DecisionWeights weights = personalityService.evaluateAgentDecisionWeights(agentId, personality);

        // Total impact = 3.0 -> impactModifier = 0.15
        assertTrue(weights.cooperationWeight() > 0.5);
        assertTrue(weights.riskWeight() > 0.5);
        assertTrue(weights.innovationWeight() > 0.5);
    }
}
