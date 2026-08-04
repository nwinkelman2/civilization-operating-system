package io.github.opencivilizationplatform.modules.social;

import io.github.opencivilizationplatform.modules.social.application.SocialGraphService;
import io.github.opencivilizationplatform.modules.social.domain.AgentRelationship;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class SocialGraphServiceTest {

    private SocialGraphService service;

    @BeforeEach
    void setUp() {
        service = new SocialGraphService();
    }

    @Test
    void testSetAndGetRelationship() {
        service.setRelationship("agent-1", "agent-2", 80.0, 10.0);

        Optional<AgentRelationship> rel = service.getRelationship("agent-1", "agent-2");
        assertTrue(rel.isPresent());
        assertEquals(80.0, rel.get().trustScore());
        assertEquals(10.0, rel.get().rivalryScore());
    }

    @Test
    void testUpdateRelationship() {
        service.setRelationship("agent-1", "agent-2", 50.0, 20.0);
        AgentRelationship updated = service.updateRelationship("agent-1", "agent-2", 15.0, -5.0);

        assertNotNull(updated);
        assertEquals(65.0, updated.trustScore());
        assertEquals(15.0, updated.rivalryScore());
    }

    @Test
    void testUpdateRelationshipNew() {
        AgentRelationship updated = service.updateRelationship("agent-1", "agent-2", 10.0, 5.0);

        assertNotNull(updated);
        assertEquals(60.0, updated.trustScore()); // default 50 + 10
        assertEquals(5.0, updated.rivalryScore()); // default 0 + 5
    }

    @Test
    void testGetRelationshipsForAgent() {
        service.setRelationship("agent-1", "agent-2", 70.0, 0.0);
        service.setRelationship("agent-3", "agent-1", 40.0, 10.0);
        service.setRelationship("agent-2", "agent-3", 90.0, 0.0);

        List<AgentRelationship> agent1Rels = service.getRelationshipsForAgent("agent-1");
        assertEquals(2, agent1Rels.size());
    }

    @Test
    void testComputeNetworkCohesion() {
        assertEquals(0.0, service.computeNetworkCohesion());

        service.setRelationship("agent-1", "agent-2", 80.0, 10.0);
        service.setRelationship("agent-2", "agent-3", 60.0, 20.0);

        // avgTrust = (80 + 60) / 2 = 70.0
        // avgRivalry = (10 + 20) / 2 = 15.0
        // cohesion = 70.0 - (15.0 * 0.5) = 62.5
        assertEquals(62.5, service.computeNetworkCohesion(), 0.001);
    }

    @Test
    void testProcessSocialGraphTick() {
        service.setRelationship("agent-1", "agent-2", 80.0, 10.0);
        assertDoesNotThrow(() -> service.processSocialGraphTick());
    }
}
