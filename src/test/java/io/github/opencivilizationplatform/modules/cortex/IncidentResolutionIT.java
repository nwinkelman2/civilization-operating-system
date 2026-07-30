package io.github.opencivilizationplatform.modules.cortex;

import io.github.opencivilizationplatform.config.SharedRedisContainer;
import io.github.opencivilizationplatform.modules.civilization.domain.Civilization;
import io.github.opencivilizationplatform.config.seed.CivilizationScale;
import io.github.opencivilizationplatform.modules.civilization.domain.CivilizationStatus;
import io.github.opencivilizationplatform.modules.civilization.infrastructure.CivilizationRepository;
import io.github.opencivilizationplatform.modules.social.domain.Incident;
import io.github.opencivilizationplatform.modules.social.domain.IncidentStatus;
import io.github.opencivilizationplatform.modules.social.domain.IncidentType;
import io.github.opencivilizationplatform.modules.social.domain.RiskLevel;
import io.github.opencivilizationplatform.modules.social.infrastructure.IncidentRepository;
import io.github.opencivilizationplatform.modules.social.application.SocialStabilityService;
import io.github.opencivilizationplatform.modules.cortex.cortex.CortexEngineService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
public class IncidentResolutionIT {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
        DockerImageName.parse("postgis/postgis:17-3.5").asCompatibleSubstituteFor("postgres")
    )
        .withDatabaseName("testciv")
        .withUsername("test")
        .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
        registry.add("spring.jpa.database-platform", () -> "org.hibernate.dialect.PostgreSQLDialect");
        registry.add("spring.jpa.properties.hibernate.dialect", () -> "org.hibernate.dialect.PostgreSQLDialect");
        registry.add("spring.data.redis.host", SharedRedisContainer.redis::getHost);
        registry.add("spring.data.redis.port", SharedRedisContainer.redis::getFirstMappedPort);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "update");
    }

    @Autowired
    private CivilizationRepository civilizationRepository;

    @Autowired
    private IncidentRepository incidentRepository;

    @Autowired
    private SocialStabilityService socialStabilityService;

    @Autowired
    private CortexEngineService cortexEngineService;

    @Test
    void testBotAssignmentReducesSeverityAndResolves() {
        // Create civilization
        Civilization civ = new Civilization();
        civ.setName("Incident Civ");
        civ.setRegion("Region Beta");
        civ.setOwnerToken("owner2");
        civ.setReputationScore(50.0);
        civ.setScale(CivilizationScale.LOCAL);
        civ.setStatus(CivilizationStatus.EMERGING);
        civ = civilizationRepository.save(civ);

        // Propose incident
        Incident incident = new Incident();
        incident.setCivilization(civ);
        incident.setType(IncidentType.CONFLICT);
        incident.setLocation("Sector G-12");
        incident.setDescription("Conflict");
        incident.setRiskLevel(RiskLevel.HIGH);
        incident.setSeverity(100.0);
        incident.setStatus(IncidentStatus.REPORTED);
        incident = incidentRepository.save(incident);

        // Assign bots to incident
        socialStabilityService.assignBotsToIncident(incident.getId(), 2, 1);

        // Verify assignment
        Incident assigned = incidentRepository.findById(incident.getId()).orElseThrow();
        assertEquals(2, assigned.getAssignedEcoBots());
        assertEquals(1, assigned.getAssignedSecurityBots());

        // Trigger simulation tick
        cortexEngineService.tickForCivilization(civ.getId());

        // Verify severity decreases: 2 eco-bots (-20) + 1 sec-bot (-15) = -35 reduction
        Incident ticked = incidentRepository.findById(incident.getId()).orElseThrow();
        assertEquals(65.0, ticked.getSeverity(), 0.01);
        assertEquals(IncidentStatus.ANALYZING, ticked.getStatus());

        // Force resolve by assigning more bots
        socialStabilityService.assignBotsToIncident(incident.getId(), 5, 5);
        cortexEngineService.tickForCivilization(civ.getId());

        // Verify resolved and reputation increase (+15 rep score)
        Incident resolved = incidentRepository.findById(incident.getId()).orElseThrow();
        assertEquals(IncidentStatus.RESOLVED, resolved.getStatus());
        assertEquals(0.0, resolved.getSeverity());

        Civilization updatedCiv = civilizationRepository.findById(civ.getId()).orElseThrow();
        assertTrue(updatedCiv.getReputationScore() > 50.0);
    }
}
