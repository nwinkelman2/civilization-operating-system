package io.github.opencivilizationplatform.modules.region;

import io.github.opencivilizationplatform.modules.region.application.ExplorationColonyService;
import io.github.opencivilizationplatform.modules.region.domain.ExplorationExpedition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ExplorationColonyServiceTest {

    private ExplorationColonyService colonyService;

    @BeforeEach
    void setUp() {
        colonyService = new ExplorationColonyService();
    }

    @Test
    void testLaunchExpedition() {
        ExplorationExpedition exp = colonyService.launchExpedition("EXP-1", 101L, "ALPHA_CENTAURI");
        assertNotNull(exp);
        assertEquals("EXP-1", exp.expeditionId());
        assertEquals(101L, exp.civilizationId());
        assertEquals("ALPHA_CENTAURI", exp.targetRegion());
        assertEquals(0.0, exp.progress());
    }

    @Test
    void testLaunchExpeditionValidation() {
        assertThrows(IllegalArgumentException.class, () -> colonyService.launchExpedition(null, 101L, "ALPHA"));
        assertThrows(IllegalArgumentException.class, () -> colonyService.launchExpedition("EXP-1", null, "ALPHA"));
        assertThrows(IllegalArgumentException.class, () -> colonyService.launchExpedition("EXP-1", 101L, null));
    }

    @Test
    void testAdvanceExplorationAndColonyEstablishment() {
        colonyService.launchExpedition("EXP-1", 101L, "ALPHA_CENTAURI");

        ExplorationColonyService.ColonyEstablishmentResult r1 = colonyService.advanceExploration("EXP-1", 45.0);
        assertFalse(r1.colonyEstablished());
        assertEquals(45.0, r1.progress());

        ExplorationColonyService.ColonyEstablishmentResult r2 = colonyService.advanceExploration("EXP-1", 60.0);
        assertTrue(r2.colonyEstablished());
        assertEquals(100.0, r2.progress());
    }

    @Test
    void testProcessExplorationTick() {
        colonyService.launchExpedition("EXP-1", 101L, "REGION_A");
        colonyService.launchExpedition("EXP-2", 102L, "REGION_B");

        List<ExplorationColonyService.ColonyEstablishmentResult> results = colonyService.processExplorationTick(25.0);
        assertEquals(2, results.size());
        assertTrue(results.stream().allMatch(r -> r.progress() == 25.0));

        List<ExplorationExpedition> active = colonyService.getActiveExpeditions();
        assertEquals(2, active.size());
    }

    @Test
    void testAdvanceNonExistentExpedition() {
        ExplorationColonyService.ColonyEstablishmentResult result = colonyService.advanceExploration("UNKNOWN", 10.0);
        assertFalse(result.colonyEstablished());
        assertNull(result.civilizationId());
    }
}
