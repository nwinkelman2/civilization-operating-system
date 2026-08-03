package io.github.opencivilizationplatform.modules.social;

import io.github.opencivilizationplatform.modules.social.application.ImmigrationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ImmigrationServiceTest {

    private ImmigrationService immigrationService;

    @BeforeEach
    void setUp() {
        immigrationService = new ImmigrationService();
    }

    @Test
    void testCalculateAttractionIndex() {
        double attr = immigrationService.calculateAttractionIndex(80.0, 90.0, 70.0, 85.0);
        assertEquals(81.5, attr, 0.001);
    }

    @Test
    void testEvaluateMigrationHigherTargetAttraction() {
        ImmigrationService.CivilizationProfile source = new ImmigrationService.CivilizationProfile("source-civ", 40.0, 40.0, 40.0, 40.0, 500);
        ImmigrationService.CivilizationProfile target = new ImmigrationService.CivilizationProfile("target-civ", 90.0, 90.0, 90.0, 90.0, 200);

        ImmigrationService.MigrationResult result = immigrationService.evaluateMigration(source, target);

        assertNotNull(result);
        assertEquals("source-civ", result.sourceCivId());
        assertEquals("target-civ", result.targetCivId());
        assertTrue(result.migratedCitizens() > 0);
        assertTrue(result.attractionDelta() > 0);
    }

    @Test
    void testEvaluateMigrationLowerTargetAttraction() {
        ImmigrationService.CivilizationProfile source = new ImmigrationService.CivilizationProfile("source-civ", 90.0, 90.0, 90.0, 90.0, 500);
        ImmigrationService.CivilizationProfile target = new ImmigrationService.CivilizationProfile("target-civ", 40.0, 40.0, 40.0, 40.0, 200);

        ImmigrationService.MigrationResult result = immigrationService.evaluateMigration(source, target);

        assertEquals(0, result.migratedCitizens());
        assertTrue(result.attractionDelta() < 0);
    }

    @Test
    void testProcessMigrationCycle() {
        ImmigrationService.MigrationResult result = immigrationService.processMigrationCycle(80.0, 50.0, 1000);
        assertNotNull(result);
        assertTrue(result.migratedCitizens() > 0);
    }
}
