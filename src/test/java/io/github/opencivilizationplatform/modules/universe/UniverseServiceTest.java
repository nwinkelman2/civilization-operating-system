package io.github.opencivilizationplatform.modules.universe;

import io.github.opencivilizationplatform.modules.universe.application.UniverseService;
import io.github.opencivilizationplatform.modules.universe.domain.UniverseConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UniverseServiceTest {

    private UniverseService universeService;

    @BeforeEach
    void setUp() {
        universeService = new UniverseService();
    }

    @Test
    void testDefaultConfig() {
        UniverseConfig config = universeService.getGlobalConfig();
        assertNotNull(config);
        assertEquals(15000L, config.cycleDurationMs());
        assertEquals(1_000_000.0, config.maxWorldEnergyCap());
        assertEquals(0.001, config.entropyDecayRate());

        assertEquals(15000L, config.getCycleDurationMs());
        assertEquals(1_000_000.0, config.getMaxWorldEnergyCap());
        assertEquals(0.001, config.getEntropyDecayRate());
    }

    @Test
    void testValidateEntropyBounds() {
        assertTrue(universeService.validateEntropyBounds(500_000.0));
        assertTrue(universeService.validateEntropyBounds(0.0));
        assertTrue(universeService.validateEntropyBounds(1_000_000.0));
        assertFalse(universeService.validateEntropyBounds(-1.0));
        assertFalse(universeService.validateEntropyBounds(1_000_001.0));
    }

    @Test
    void testInvalidConfigConstructor() {
        assertThrows(IllegalArgumentException.class, () -> new UniverseConfig(0, 100, 0.1));
        assertThrows(IllegalArgumentException.class, () -> new UniverseConfig(100, -1, 0.1));
        assertThrows(IllegalArgumentException.class, () -> new UniverseConfig(100, 100, -0.1));
        assertThrows(IllegalArgumentException.class, () -> new UniverseConfig(100, 100, 1.1));
    }
}
