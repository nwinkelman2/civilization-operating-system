package io.github.opencivilizationplatform.modules.social;

import io.github.opencivilizationplatform.modules.social.application.CultureArtService;
import io.github.opencivilizationplatform.modules.social.domain.CulturalArtifact;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class CultureArtServiceTest {

    private CultureArtService service;

    @BeforeEach
    void setUp() {
        service = new CultureArtService();
    }

    @Test
    void testCreateAndGetArtifact() {
        CulturalArtifact created = service.createArtifact("art-1", "agent-1", "Mona Lisa", "RENAISSANCE", 100.0);
        assertNotNull(created);
        assertEquals("art-1", created.artifactId());

        Optional<CulturalArtifact> retrieved = service.getArtifact("art-1");
        assertTrue(retrieved.isPresent());
        assertEquals("Mona Lisa", retrieved.get().title());
    }

    @Test
    void testGetArtifactsByCreatorAndEra() {
        service.createArtifact("art-1", "agent-1", "Mona Lisa", "RENAISSANCE", 100.0);
        service.createArtifact("art-2", "agent-1", "David", "RENAISSANCE", 90.0);
        service.createArtifact("art-3", "agent-2", "Starry Night", "IMPRESSIONISM", 95.0);

        List<CulturalArtifact> agent1Arts = service.getArtifactsByCreator("agent-1");
        assertEquals(2, agent1Arts.size());

        List<CulturalArtifact> renaissanceArts = service.getArtifactsByEra("RENAISSANCE");
        assertEquals(2, renaissanceArts.size());
    }

    @Test
    void testEvaluateTotalPrestigeAndMultiplier() {
        service.createArtifact("art-1", "agent-1", "Mona Lisa", "RENAISSANCE", 100.0);
        service.createArtifact("art-2", "agent-2", "Starry Night", "IMPRESSIONISM", 50.0);

        assertEquals(150.0, service.evaluateTotalPrestige());
        assertEquals(150.0, service.evaluatePrestige("art-1", 1.5));
    }

    @Test
    void testEvaluateCulturalMovementTrends() {
        service.createArtifact("art-1", "agent-1", "Mona Lisa", "RENAISSANCE", 100.0);
        service.createArtifact("art-2", "agent-1", "David", "RENAISSANCE", 90.0);
        service.createArtifact("art-3", "agent-2", "Starry Night", "IMPRESSIONISM", 95.0);

        Map<String, Double> trends = service.evaluateCulturalMovementTrends();
        assertEquals(190.0, trends.get("RENAISSANCE"));
        assertEquals(95.0, trends.get("IMPRESSIONISM"));
    }

    @Test
    void testProcessCultureTick() {
        service.createArtifact("art-1", "agent-1", "Mona Lisa", "RENAISSANCE", 100.0);
        assertDoesNotThrow(() -> service.processCultureTick());
    }
}
