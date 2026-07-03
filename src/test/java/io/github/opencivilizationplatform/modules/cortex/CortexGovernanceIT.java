package io.github.opencivilizationplatform.modules.cortex;

import io.github.opencivilizationplatform.config.SharedRedisContainer;
import io.github.opencivilizationplatform.modules.civilization.domain.Civilization;
import io.github.opencivilizationplatform.config.seed.CivilizationScale;
import io.github.opencivilizationplatform.modules.civilization.domain.CivilizationStatus;
import io.github.opencivilizationplatform.modules.civilization.infrastructure.CivilizationRepository;
import io.github.opencivilizationplatform.modules.participation.domain.Rule;
import io.github.opencivilizationplatform.modules.participation.domain.RuleStatus;
import io.github.opencivilizationplatform.modules.participation.domain.ValidationStatus;
import io.github.opencivilizationplatform.modules.participation.infrastructure.RuleRepository;
import io.github.opencivilizationplatform.modules.nexus.application.MigrationService;
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
public class CortexGovernanceIT {

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
    private RuleRepository ruleRepository;

    @Autowired
    private MigrationService migrationService;

    @Test
    void testLockEntryRuleBlocksMigration() {
        // Create source civilization
        Civilization fromCiv = new Civilization();
        fromCiv.setName("Alexandria Source");
        fromCiv.setRegion("Region Alpha");
        fromCiv.setOwnerToken("owner_alpha");
        fromCiv.setScale(CivilizationScale.LOCAL);
        fromCiv.setStatus(CivilizationStatus.EMERGING);
        fromCiv = civilizationRepository.save(fromCiv);

        // Create target civilization
        Civilization toCiv = new Civilization();
        toCiv.setName("Athens Target");
        toCiv.setRegion("Region Beta");
        toCiv.setOwnerToken("owner_beta");
        toCiv.setScale(CivilizationScale.LOCAL);
        toCiv.setStatus(CivilizationStatus.EMERGING);
        toCiv = civilizationRepository.save(toCiv);

        // Add active LOCK_ENTRY rule to target
        Rule rule = new Rule();
        rule.setTitle("Block Entry");
        rule.setSector("migration");
        rule.setLogicCode("LOCK_ENTRY");
        rule.setDescription("Blocks entry to the civilization");
        rule.setStatus(RuleStatus.ACTIVE);
        rule.setValidationStatus(ValidationStatus.SCIENTIFICALLY_VALIDATED);
        rule.setCivilization(toCiv);
        ruleRepository.save(rule);

        // Try applying migration request - should throw IllegalStateException due to active LOCK_ENTRY
        final Long fromId = fromCiv.getId();
        final Long toId = toCiv.getId();
        assertThrows(IllegalStateException.class, () -> {
            migrationService.applyMigration("Citizen A", fromId, toId, "Escape conflict");
        });
    }
}
