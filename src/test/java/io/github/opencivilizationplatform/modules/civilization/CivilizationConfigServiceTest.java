package io.github.opencivilizationplatform.modules.civilization;

import io.github.opencivilizationplatform.modules.civilization.application.CivilizationConfigService;
import io.github.opencivilizationplatform.modules.civilization.domain.CivilizationConfigMatrix;
import io.github.opencivilizationplatform.modules.civilization.domain.CivilizationConfigMatrix.ResourceAllocationPriority;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CivilizationConfigServiceTest {

    private CivilizationConfigService configService;

    @BeforeEach
    void setUp() {
        configService = new CivilizationConfigService();
    }

    @Test
    void testGetDefaultConfig() {
        CivilizationConfigMatrix config = configService.getDefaultConfig();
        assertEquals(ResourceAllocationPriority.BALANCED, config.resourceAllocationPriority());
        assertEquals(0.10, config.taxRate(), 0.001);
        assertEquals(0.50, config.autonomyLevel(), 0.001);
    }

    @Test
    void testCreateCustomConfigAndClamping() {
        CivilizationConfigMatrix config = configService.createCustomConfig(
            ResourceAllocationPriority.GROWTH,
            0.80, // Should be clamped to 0.50 max tax rate
            -0.10 // Should be clamped to 0.0 min autonomy level
        );

        assertEquals(ResourceAllocationPriority.GROWTH, config.resourceAllocationPriority());
        assertEquals(0.50, config.taxRate(), 0.001);
        assertEquals(0.0, config.autonomyLevel(), 0.001);
    }

    @Test
    void testSaveAndGetConfig() {
        Long civId = 42L;

        // Unsaved civId returns default config
        assertEquals(ResourceAllocationPriority.BALANCED, configService.getConfig(civId).resourceAllocationPriority());

        CivilizationConfigMatrix custom = configService.createCustomConfig(
            ResourceAllocationPriority.RESEARCH, 0.25, 0.85
        );
        configService.saveConfig(civId, custom);

        CivilizationConfigMatrix retrieved = configService.getConfig(civId);
        assertEquals(ResourceAllocationPriority.RESEARCH, retrieved.resourceAllocationPriority());
        assertEquals(0.25, retrieved.taxRate(), 0.001);
        assertEquals(0.85, retrieved.autonomyLevel(), 0.001);
    }

    @Test
    void testUpdatePartialConfig() {
        Long civId = 101L;

        configService.updateResourcePriority(civId, ResourceAllocationPriority.DEFENSE);
        assertEquals(ResourceAllocationPriority.DEFENSE, configService.getConfig(civId).resourceAllocationPriority());
        assertEquals(0.10, configService.getConfig(civId).taxRate(), 0.001);

        configService.updateTaxRate(civId, 0.30);
        assertEquals(ResourceAllocationPriority.DEFENSE, configService.getConfig(civId).resourceAllocationPriority());
        assertEquals(0.30, configService.getConfig(civId).taxRate(), 0.001);

        configService.updateAutonomyLevel(civId, 0.90);
        assertEquals(0.90, configService.getConfig(civId).autonomyLevel(), 0.001);
    }
}
