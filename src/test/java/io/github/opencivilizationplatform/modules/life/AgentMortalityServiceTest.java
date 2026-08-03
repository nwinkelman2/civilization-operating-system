package io.github.opencivilizationplatform.modules.life;

import io.github.opencivilizationplatform.modules.life.application.AgentMortalityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AgentMortalityServiceTest {

    private AgentMortalityService agentMortalityService;

    @BeforeEach
    void setUp() {
        agentMortalityService = new AgentMortalityService();
    }

    @Test
    void testEvaluateAgingAndHealthNormal() {
        AgentMortalityService.LifecycleStatus status = agentMortalityService.evaluateAgingAndHealth(25, 100.0, 0.1);
        assertEquals(26, status.age());
        assertEquals(99.5, status.health());
        assertFalse(status.isDead());
        assertNull(status.causeOfDeath());
    }

    @Test
    void testEvaluateAgingAndHealthHighStressAndAge() {
        AgentMortalityService.LifecycleStatus status = agentMortalityService.evaluateAgingAndHealth(70, 10.0, 0.9);
        assertEquals(71, status.age());
        assertTrue(status.health() < 10.0);
    }

    @Test
    void testEvaluateMortalityEventZeroHealth() {
        assertTrue(agentMortalityService.evaluateMortalityEvent(30, 0.0));
    }

    @Test
    void testCalculateBirthAdditions() {
        int births = agentMortalityService.calculateBirthAdditions(100, 0.05, 100.0);
        assertEquals(5, births);
    }

    @Test
    void testCalculateMortalityDeaths() {
        int deaths = agentMortalityService.calculateMortalityDeaths(100, 0.02, 40.0);
        assertTrue(deaths >= 2);
    }

    @Test
    void testProcessLifecycleTick() {
        AgentMortalityService.MortalityCycleResult result = agentMortalityService.processLifecycleTick(1000, 0.02, 0.01);
        assertNotNull(result);
        assertEquals(1000, result.startingPopulation());
        assertTrue(result.births() > 0);
        assertTrue(result.deaths() > 0);
        assertTrue(result.finalPopulation() > 0);
    }

    @Test
    void testProcessLifecycleTickZeroPopulation() {
        AgentMortalityService.MortalityCycleResult result = agentMortalityService.processLifecycleTick(0, 0.02, 0.01);
        assertEquals(0, result.startingPopulation());
        assertEquals(0, result.finalPopulation());
    }
}
