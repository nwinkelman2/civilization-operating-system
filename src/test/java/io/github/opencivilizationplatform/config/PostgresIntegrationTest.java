package io.github.opencivilizationplatform.config;

import io.github.opencivilizationplatform.modules.civilization.infrastructure.CivilizationRepository;
import io.github.opencivilizationplatform.modules.region.infrastructure.ResourceRegionRepository;
import io.github.opencivilizationplatform.modules.nexus.infrastructure.NexusNodeRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
@Disabled("Requires Docker/Testcontainers")
class PostgresIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgis/postgis:17-3.5")
        .withDatabaseName("testciv")
        .withUsername("test")
        .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "validate");
        registry.add("spring.flyway.enabled", () -> "true");
        registry.add("spring.flyway.baseline-on-migrate", () -> "true");
    }

    @Autowired
    private CivilizationRepository civilizationRepository;

    @Autowired
    private ResourceRegionRepository regionRepository;

    @Autowired
    private NexusNodeRepository nodeRepository;

    @Test
    void contextLoadsAndTablesExist() {
        assertTrue(civilizationRepository.count() == 0);
        assertTrue(regionRepository.count() == 0);
        assertTrue(nodeRepository.count() == 0);
    }
}

