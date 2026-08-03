package io.github.opencivilizationplatform.modules.physics;

import io.github.opencivilizationplatform.modules.physics.application.ClimateDisasterService;
import io.github.opencivilizationplatform.modules.physics.domain.Season;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ClimateDisasterServiceTest {

    private ClimateDisasterService climateDisasterService;

    @BeforeEach
    void setUp() {
        climateDisasterService = new ClimateDisasterService();
    }

    @Test
    void testSeasonAdvancementCycle() {
        assertEquals(Season.SPRING, climateDisasterService.getCurrentSeason());

        assertEquals(Season.SUMMER, climateDisasterService.advanceSeason());
        assertEquals(Season.AUTUMN, climateDisasterService.advanceSeason());
        assertEquals(Season.WINTER, climateDisasterService.advanceSeason());
        assertEquals(Season.SPRING, climateDisasterService.advanceSeason());
    }

    @Test
    void testResourceYieldMultipliers() {
        assertEquals(1.2, climateDisasterService.calculateResourceYieldMultiplier(Season.SPRING), 1e-6);
        assertEquals(1.5, climateDisasterService.calculateResourceYieldMultiplier(Season.SUMMER), 1e-6);
        assertEquals(1.0, climateDisasterService.calculateResourceYieldMultiplier(Season.AUTUMN), 1e-6);
        assertEquals(0.5, climateDisasterService.calculateResourceYieldMultiplier(Season.WINTER), 1e-6);
        assertEquals(1.0, climateDisasterService.calculateResourceYieldMultiplier(null), 1e-6);
    }

    @Test
    void testTriggerDisasterEventHeatwave() {
        ClimateDisasterService.DisasterEvent event = climateDisasterService.triggerDisasterEvent(Season.SUMMER, 38.0);
        assertTrue(event.occurred());
        assertEquals("HEATWAVE", event.disasterType());
        assertTrue(event.severity() > 0.0);
    }

    @Test
    void testTriggerDisasterEventBlizzard() {
        ClimateDisasterService.DisasterEvent event = climateDisasterService.triggerDisasterEvent(Season.WINTER, -15.0);
        assertTrue(event.occurred());
        assertEquals("BLIZZARD", event.disasterType());
        assertTrue(event.severity() > 0.0);
    }

    @Test
    void testTriggerDisasterEventNone() {
        ClimateDisasterService.DisasterEvent event = climateDisasterService.triggerDisasterEvent(Season.SPRING, 15.0);
        assertFalse(event.occurred());
        assertEquals("NONE", event.disasterType());
    }

    @Test
    void testProcessClimateCycle() {
        ClimateDisasterService.ClimateCycleResult result = climateDisasterService.processClimateCycle(40.0);
        assertNotNull(result);
        assertEquals(Season.SUMMER, result.season());
        assertEquals(1.5, result.yieldMultiplier(), 1e-6);
        assertTrue(result.disasterEvent().occurred());
        assertEquals("HEATWAVE", result.disasterEvent().disasterType());
    }
}
