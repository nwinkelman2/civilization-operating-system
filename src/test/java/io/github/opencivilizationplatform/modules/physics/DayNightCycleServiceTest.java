package io.github.opencivilizationplatform.modules.physics;

import io.github.opencivilizationplatform.modules.physics.application.DayNightCycleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DayNightCycleServiceTest {

    private DayNightCycleService service;

    @BeforeEach
    void setUp() {
        service = new DayNightCycleService();
    }

    @Test
    void testInitialState() {
        assertEquals(12.0, service.getCurrentHour());
        assertTrue(service.isDaytime());
        assertEquals("DAY_ACTIVE", service.getDayNightActiveStatus());
        assertEquals(1.0, service.calculateSolarIntensity(), 0.001);
    }

    @Test
    void testAdvanceCycle() {
        service.setCurrentHour(12.0);
        service.advanceCycle(6.0);
        assertEquals(18.0, service.getCurrentHour());
        assertFalse(service.isDaytime());
        assertEquals("NIGHT_REST", service.getDayNightActiveStatus());
        assertEquals(0.0, service.calculateSolarIntensity(), 0.001);
    }

    @Test
    void testCycleWrapAround() {
        service.setCurrentHour(23.0);
        service.advanceCycle(3.0);
        assertEquals(2.0, service.getCurrentHour());
        assertFalse(service.isDaytime());
        assertEquals("NIGHT_REST", service.getDayNightActiveStatus());
    }

    @Test
    void testSolarIntensityAtPeakAndNight() {
        service.setCurrentHour(12.0);
        assertEquals(1.0, service.calculateSolarIntensity(), 0.001);

        service.setCurrentHour(0.0);
        assertEquals(0.0, service.calculateSolarIntensity(), 0.001);

        service.setCurrentHour(6.0);
        assertEquals(0.0, service.calculateSolarIntensity(), 0.001);

        service.setCurrentHour(9.0);
        assertTrue(service.calculateSolarIntensity() > 0.6 && service.calculateSolarIntensity() < 0.8);
    }

    @Test
    void testProcessDayNightTick() {
        service.setCurrentHour(10.0);
        assertDoesNotThrow(() -> service.processDayNightTick());
        assertEquals(11.0, service.getCurrentHour());
    }
}
