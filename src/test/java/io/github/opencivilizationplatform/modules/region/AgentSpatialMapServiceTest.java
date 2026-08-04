package io.github.opencivilizationplatform.modules.region;

import io.github.opencivilizationplatform.modules.region.application.AgentSpatialMapService;
import io.github.opencivilizationplatform.modules.region.domain.AgentPosition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class AgentSpatialMapServiceTest {

    private AgentSpatialMapService service;

    @BeforeEach
    void setUp() {
        service = new AgentSpatialMapService();
    }

    @Test
    void testUpdateAndGetPosition() {
        AgentPosition pos = new AgentPosition("agent-1", 10.0, 20.0, 5.0);
        service.updatePosition(pos);

        Optional<AgentPosition> retrieved = service.getPosition("agent-1");
        assertTrue(retrieved.isPresent());
        assertEquals(10.0, retrieved.get().x());
        assertEquals(20.0, retrieved.get().y());
        assertEquals(5.0, retrieved.get().speed());
    }

    @Test
    void testUpdatePositionOverload() {
        service.updatePosition("agent-2", 15.0, 25.0, 3.0);

        Optional<AgentPosition> retrieved = service.getPosition("agent-2");
        assertTrue(retrieved.isPresent());
        assertEquals(15.0, retrieved.get().x());
        assertEquals(25.0, retrieved.get().y());
    }

    @Test
    void testMoveTowardsPartial() {
        service.updatePosition("agent-1", 0.0, 0.0, 10.0);

        AgentPosition updated = service.moveTowards("agent-1", 100.0, 0.0, 2.0); // maxStep = 20.0
        assertNotNull(updated);
        assertEquals(20.0, updated.x(), 0.001);
        assertEquals(0.0, updated.y(), 0.001);
    }

    @Test
    void testMoveTowardsFull() {
        service.updatePosition("agent-1", 0.0, 0.0, 10.0);

        AgentPosition updated = service.moveTowards("agent-1", 5.0, 0.0, 1.0); // maxStep = 10.0 > 5.0
        assertNotNull(updated);
        assertEquals(5.0, updated.x(), 0.001);
        assertEquals(0.0, updated.y(), 0.001);
    }

    @Test
    void testProximityCheck() {
        service.updatePosition("agent-1", 0.0, 0.0, 1.0);
        service.updatePosition("agent-2", 3.0, 4.0, 1.0); // distance = 5.0
        service.updatePosition("agent-3", 10.0, 10.0, 1.0);

        List<AgentPosition> inRange = service.findAgentsInProximity(0.0, 0.0, 6.0);
        assertEquals(2, inRange.size());

        List<AgentPosition> nearAgent1 = service.findAgentsInProximity("agent-1", 6.0);
        assertEquals(1, nearAgent1.size());
        assertEquals("agent-2", nearAgent1.get(0).agentId());
    }

    @Test
    void testCalculateDistance() {
        AgentPosition p1 = new AgentPosition("a1", 0.0, 0.0, 1.0);
        AgentPosition p2 = new AgentPosition("a2", 3.0, 4.0, 1.0);

        double dist = service.calculateDistance(p1, p2);
        assertEquals(5.0, dist, 0.001);
    }

    @Test
    void testProcessSpatialTick() {
        service.updatePosition("agent-1", 1.0, 1.0, 1.0);
        assertDoesNotThrow(() -> service.processSpatialTick());
    }
}
