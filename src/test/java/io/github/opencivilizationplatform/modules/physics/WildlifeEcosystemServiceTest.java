package io.github.opencivilizationplatform.modules.physics;

import io.github.opencivilizationplatform.modules.physics.application.WildlifeEcosystemService;
import io.github.opencivilizationplatform.modules.physics.domain.FaunaSpecies;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class WildlifeEcosystemServiceTest {

    private WildlifeEcosystemService service;

    @BeforeEach
    void setUp() {
        service = new WildlifeEcosystemService();
    }

    @Test
    void testRegisterAndGetSpecies() {
        FaunaSpecies species = service.registerSpecies("Wolf", 50, 0.15);
        assertNotNull(species);
        assertEquals("Wolf", species.speciesName());
        assertEquals(50, species.population());
        assertEquals(0.15, species.reproductionRate());

        Optional<FaunaSpecies> retrieved = service.getSpecies("Wolf");
        assertTrue(retrieved.isPresent());
        assertEquals("Wolf", retrieved.get().speciesName());
    }

    @Test
    void testGetAllSpecies() {
        service.registerSpecies("Wolf", 50, 0.15);
        service.registerSpecies("Deer", 200, 0.25);

        List<FaunaSpecies> allSpecies = service.getAllSpecies();
        assertEquals(2, allSpecies.size());
    }

    @Test
    void testCalculateBiodiversityIndexEmpty() {
        assertEquals(0.0, service.calculateBiodiversityIndex());
    }

    @Test
    void testCalculateBiodiversityIndexWithSpecies() {
        service.registerSpecies("Wolf", 100, 0.1);
        service.registerSpecies("Deer", 100, 0.2);

        double biodiversity = service.calculateBiodiversityIndex();
        assertTrue(biodiversity > 0.0 && biodiversity <= 100.0);
    }

    @Test
    void testCalculateFoodWebStability() {
        assertEquals(0.0, service.calculateFoodWebStability());

        service.registerSpecies("Wolf", 50, 0.15);
        service.registerSpecies("Rabbit", 500, 0.40);

        double stability = service.calculateFoodWebStability();
        assertTrue(stability > 0.0 && stability <= 100.0);
    }

    @Test
    void testProcessEcosystemTick() {
        service.registerSpecies("Bear", 20, 0.05);
        assertDoesNotThrow(() -> service.processEcosystemTick());
    }
}
