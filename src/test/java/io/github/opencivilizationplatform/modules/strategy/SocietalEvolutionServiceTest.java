package io.github.opencivilizationplatform.modules.strategy;

import io.github.opencivilizationplatform.modules.strategy.application.SocietalEvolutionService;
import io.github.opencivilizationplatform.modules.strategy.domain.SocietalEra;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SocietalEvolutionServiceTest {

    private SocietalEvolutionService evolutionService;

    @BeforeEach
    void setUp() {
        evolutionService = new SocietalEvolutionService();
    }

    @Test
    void testInitialEraIsAgrarian() {
        assertEquals(SocietalEra.AGRARIAN, evolutionService.getCurrentEra());
    }

    @Test
    void testCanAdvanceEraRequirements() {
        assertFalse(evolutionService.canAdvanceEra(SocietalEra.AGRARIAN, 4, 100.0));
        assertFalse(evolutionService.canAdvanceEra(SocietalEra.AGRARIAN, 5, 99.9));
        assertTrue(evolutionService.canAdvanceEra(SocietalEra.AGRARIAN, 5, 100.0));

        assertFalse(evolutionService.canAdvanceEra(SocietalEra.INDUSTRIAL, 14, 500.0));
        assertFalse(evolutionService.canAdvanceEra(SocietalEra.INDUSTRIAL, 15, 499.0));
        assertTrue(evolutionService.canAdvanceEra(SocietalEra.INDUSTRIAL, 15, 500.0));

        assertFalse(evolutionService.canAdvanceEra(SocietalEra.INFORMATION, 29, 1000.0));
        assertFalse(evolutionService.canAdvanceEra(SocietalEra.INFORMATION, 30, 999.0));
        assertTrue(evolutionService.canAdvanceEra(SocietalEra.INFORMATION, 30, 1000.0));

        assertFalse(evolutionService.canAdvanceEra(SocietalEra.BIOSPHERE_HARMONY, 100, 10000.0));
    }

    @Test
    void testFullEraProgressionFlow() {
        // AGRARIAN -> INDUSTRIAL
        SocietalEvolutionService.EvolutionResult r1 = evolutionService.processEvolutionCycle(5, 150.0);
        assertTrue(r1.eraAdvanced());
        assertEquals(SocietalEra.AGRARIAN, r1.currentEra());
        assertEquals(SocietalEra.INDUSTRIAL, r1.targetEra());
        assertEquals(SocietalEra.INDUSTRIAL, evolutionService.getCurrentEra());

        // INDUSTRIAL -> INFORMATION
        SocietalEvolutionService.EvolutionResult r2 = evolutionService.processEvolutionCycle(20, 600.0);
        assertTrue(r2.eraAdvanced());
        assertEquals(SocietalEra.INDUSTRIAL, r2.currentEra());
        assertEquals(SocietalEra.INFORMATION, r2.targetEra());
        assertEquals(SocietalEra.INFORMATION, evolutionService.getCurrentEra());

        // INFORMATION -> BIOSPHERE_HARMONY
        SocietalEvolutionService.EvolutionResult r3 = evolutionService.processEvolutionCycle(35, 1200.0);
        assertTrue(r3.eraAdvanced());
        assertEquals(SocietalEra.INFORMATION, r3.currentEra());
        assertEquals(SocietalEra.BIOSPHERE_HARMONY, r3.targetEra());
        assertEquals(SocietalEra.BIOSPHERE_HARMONY, evolutionService.getCurrentEra());

        // BIOSPHERE_HARMONY -> stays BIOSPHERE_HARMONY
        SocietalEvolutionService.EvolutionResult r4 = evolutionService.processEvolutionCycle(50, 5000.0);
        assertFalse(r4.eraAdvanced());
        assertEquals(SocietalEra.BIOSPHERE_HARMONY, r4.currentEra());
        assertEquals(SocietalEra.BIOSPHERE_HARMONY, r4.targetEra());
    }

    @Test
    void testSetCurrentEra() {
        evolutionService.setCurrentEra(SocietalEra.INFORMATION);
        assertEquals(SocietalEra.INFORMATION, evolutionService.getCurrentEra());
    }
}
