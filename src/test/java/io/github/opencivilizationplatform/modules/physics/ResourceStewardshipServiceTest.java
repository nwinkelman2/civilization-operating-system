package io.github.opencivilizationplatform.modules.physics;

import io.github.opencivilizationplatform.modules.physics.application.ResourceStewardshipService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ResourceStewardshipServiceTest {

    private ResourceStewardshipService stewardshipService;

    @BeforeEach
    void setUp() {
        stewardshipService = new ResourceStewardshipService();
    }

    @Test
    void testCalculateEcologicalFootprint() {
        double footprint = stewardshipService.calculateEcologicalFootprint(50.0, 20.0, 100);
        // (50 * 0.6 + 20 * 0.4) * (100 / 100) = (30 + 8) * 1 = 38.0
        assertEquals(38.0, footprint, 0.001);
    }

    @Test
    void testCalculateSustainabilityIndex() {
        double indexHigh = stewardshipService.calculateSustainabilityIndex(100.0, 50.0);
        assertEquals(2.0, indexHigh, 0.001);

        double indexZeroFootprint = stewardshipService.calculateSustainabilityIndex(10.0, 0.0);
        assertEquals(2.0, indexZeroFootprint, 0.001);
    }

    @Test
    void testEvaluateStewardshipStatuses() {
        // High regen vs footprint -> SUSTAINABLE
        ResourceStewardshipService.StewardshipReport report1 = stewardshipService.evaluateStewardship(10.0, 5.0, 100.0, 100);
        assertEquals("SUSTAINABLE", report1.status());
        assertTrue(report1.sustainabilityIndex() >= 1.0);

        // Moderate ratio -> MODERATE_IMPACT
        ResourceStewardshipService.StewardshipReport report2 = stewardshipService.evaluateStewardship(100.0, 50.0, 50.0, 100);
        assertEquals("MODERATE_IMPACT", report2.status());
        assertTrue(report2.sustainabilityIndex() >= 0.5 && report2.sustainabilityIndex() < 1.0);

        // Low regen vs high footprint -> ECOLOGICAL_CRITICAL
        ResourceStewardshipService.StewardshipReport report3 = stewardshipService.evaluateStewardship(200.0, 150.0, 10.0, 100);
        assertEquals("ECOLOGICAL_CRITICAL", report3.status());
        assertTrue(report3.sustainabilityIndex() < 0.5);
    }

    @Test
    void testProcessStewardshipTick() {
        ResourceStewardshipService.StewardshipReport report = stewardshipService.processStewardshipTick(50.0, 20.0, 50.0, 100);
        assertNotNull(report);
        assertEquals(38.0, report.ecologicalFootprint(), 0.001);
        assertEquals("SUSTAINABLE", report.status());
    }
}
