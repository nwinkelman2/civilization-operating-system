package io.github.opencivilizationplatform.modules.cortex;

import io.github.opencivilizationplatform.config.SharedRedisContainer;
import io.github.opencivilizationplatform.modules.civilization.domain.Civilization;
import io.github.opencivilizationplatform.config.seed.CivilizationScale;
import io.github.opencivilizationplatform.modules.civilization.domain.CivilizationStatus;
import io.github.opencivilizationplatform.modules.civilization.infrastructure.CivilizationRepository;
import io.github.opencivilizationplatform.modules.social.domain.EspionageOperation;
import io.github.opencivilizationplatform.modules.social.infrastructure.EspionageRepository;
import io.github.opencivilizationplatform.modules.social.api.EspionageController;
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

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
public class EspionageIT {

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
    private EspionageRepository espionageRepository;

    @Autowired
    private EspionageController espionageController;

    @Autowired
    private CortexEngineService cortexEngineService;

    @Test
    void testLaunchAndTickEspionageOperation() {
        civilizationRepository.deleteAll();
        espionageRepository.deleteAll();

        // 1. Create Initiator Civilization
        Civilization initiator = new Civilization();
        initiator.setName("Atlantis");
        initiator.setRegion("Atlantic");
        initiator.setOwnerToken("tok1");
        initiator.setScale(CivilizationScale.LOCAL);
        initiator.setStatus(CivilizationStatus.EMERGING);
        initiator.setFood(100.0);
        initiator.setWater(100.0);
        initiator.setMinerals(100.0);
        initiator.setEnergy(100.0);
        initiator.setHousing(100.0);
        initiator = civilizationRepository.saveAndFlush(initiator);

        // 2. Create Target Civilization
        Civilization target = new Civilization();
        target.setName("Mu");
        target.setRegion("Pacific");
        target.setOwnerToken("tok2");
        target.setScale(CivilizationScale.LOCAL);
        target.setStatus(CivilizationStatus.EMERGING);
        target.setFood(100.0);
        target.setWater(100.0);
        target.setMinerals(100.0);
        target.setEnergy(100.0);
        target.setHousing(100.0);
        target = civilizationRepository.saveAndFlush(target);

        // 3. Launch operation
        EspionageOperation op = espionageController.launchOperation(
            initiator.getId(), target.getId(), "STEAL_TECH", 3
        );
        espionageRepository.flush();

        assertNotNull(op.getId());
        assertEquals("IN_PROGRESS", op.getStatus());
        assertEquals(4, op.getTicksRemaining());

        // 4. Tick several times to resolve the operation
        for (int i = 0; i < 4; i++) {
            cortexEngineService.performTick();
        }

        EspionageOperation updatedOp = espionageRepository.findById(op.getId()).orElseThrow();
        assertNotEquals("IN_PROGRESS", updatedOp.getStatus(), "Operation status should have changed from IN_PROGRESS");
        assertTrue(List.of("SUCCESS", "FAILED").contains(updatedOp.getStatus()), "Status should be SUCCESS or FAILED");
    }
}
