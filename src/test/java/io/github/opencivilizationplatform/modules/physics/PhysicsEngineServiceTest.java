package io.github.opencivilizationplatform.modules.physics;

import io.github.opencivilizationplatform.modules.physics.application.PhysicsEngineService;
import io.github.opencivilizationplatform.modules.physics.domain.ConservationLaw;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PhysicsEngineServiceTest {

    private PhysicsEngineService physicsEngineService;

    @BeforeEach
    void setUp() {
        physicsEngineService = new PhysicsEngineService();
    }

    @Test
    void testApplyPhysicalDecay() {
        double result = physicsEngineService.applyPhysicalDecay(100.0, 0.05);
        assertEquals(95.0, result, 1e-6);

        assertThrows(IllegalArgumentException.class, () -> physicsEngineService.applyPhysicalDecay(100.0, -0.1));
        assertThrows(IllegalArgumentException.class, () -> physicsEngineService.applyPhysicalDecay(100.0, 1.5));
        assertThrows(IllegalArgumentException.class, () -> physicsEngineService.applyPhysicalDecay(-10.0, 0.1));
    }

    @Test
    void testApplyIndustrialDrift() {
        double result = physicsEngineService.applyIndustrialDrift(1.0, 0.02);
        assertEquals(0.98, result, 1e-6);

        double floorResult = physicsEngineService.applyIndustrialDrift(0.01, 0.05);
        assertEquals(0.0, floorResult, 1e-6);

        assertThrows(IllegalArgumentException.class, () -> physicsEngineService.applyIndustrialDrift(1.0, -0.01));
    }

    @Test
    void testConservationLaw() {
        assertTrue(ConservationLaw.verifyConservation(100.0, 100.0000001));
        assertFalse(ConservationLaw.verifyConservation(100.0, 105.0));

        assertTrue(ConservationLaw.verifyEnergyMassConservation(50.0, 50.0, 40.0, 60.0));
        assertFalse(ConservationLaw.verifyEnergyMassConservation(50.0, 50.0, 40.0, 61.0));

        assertDoesNotThrow(() -> ConservationLaw.enforceConservation(100.0, 100.0));
        assertThrows(IllegalStateException.class, () -> ConservationLaw.enforceConservation(100.0, 105.0));

        assertTrue(physicsEngineService.verifyConservation(100.0, 100.0));
        assertDoesNotThrow(() -> physicsEngineService.enforceConservation(100.0, 100.0));
    }
}
