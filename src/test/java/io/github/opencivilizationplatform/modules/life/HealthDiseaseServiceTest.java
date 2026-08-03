package io.github.opencivilizationplatform.modules.life;

import io.github.opencivilizationplatform.modules.life.application.HealthDiseaseService;
import io.github.opencivilizationplatform.modules.life.domain.EpidemicStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HealthDiseaseServiceTest {

    private HealthDiseaseService healthDiseaseService;

    @BeforeEach
    void setUp() {
        healthDiseaseService = new HealthDiseaseService();
    }

    @Test
    void testCalculateContagionRiskBaseline() {
        double risk = healthDiseaseService.calculateContagionRisk(40.0, 0.0);
        assertEquals(0.05, risk, 1e-6);
    }

    @Test
    void testCalculateContagionRiskHighAqiAndDensity() {
        double risk = healthDiseaseService.calculateContagionRisk(150.0, 2000.0);
        // AQI risk: (150 - 50) * 0.002 = 0.2
        // Density risk: 2000 * 0.0001 = 0.2
        // Baseline: 0.05 -> Total = 0.45
        assertEquals(0.45, risk, 1e-6);
    }

    @Test
    void testCalculateCureRateZeroFacilities() {
        double cureRate = healthDiseaseService.calculateCureRate(0, 2.0);
        assertEquals(0.0, cureRate, 1e-6);
    }

    @Test
    void testCalculateCureRateWithTech() {
        double cureRate = healthDiseaseService.calculateCureRate(3, 1.0);
        // Base: 3 * 0.1 = 0.3, Tech multiplier: 1 + 1.0 = 2.0 -> Total = 0.6
        assertEquals(0.6, cureRate, 1e-6);
    }

    @Test
    void testEvaluateEpidemicStatusZeroPopulation() {
        EpidemicStatus status = healthDiseaseService.evaluateEpidemicStatus(0, 100.0, 1000.0, 2, 1.0);
        assertEquals(0, status.infectedCount());
        assertEquals(0.0, status.severityScore());
        assertFalse(status.activeOutbreak());
    }

    @Test
    void testEvaluateEpidemicStatusOutbreak() {
        EpidemicStatus status = healthDiseaseService.evaluateEpidemicStatus(1000, 200.0, 5000.0, 0, 0.0);
        assertTrue(status.infectedCount() > 0);
        assertTrue(status.severityScore() > 0.0);
        assertTrue(status.activeOutbreak());
    }

    @Test
    void testProcessHealthTick() {
        EpidemicStatus status = healthDiseaseService.processHealthTick(500, 80.0, 1000.0, 2);
        assertNotNull(status);
    }
}
