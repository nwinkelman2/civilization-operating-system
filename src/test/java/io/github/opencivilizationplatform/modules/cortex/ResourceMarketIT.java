package io.github.opencivilizationplatform.modules.cortex;

import io.github.opencivilizationplatform.config.SharedRedisContainer;
import io.github.opencivilizationplatform.modules.civilization.domain.Civilization;
import io.github.opencivilizationplatform.config.seed.CivilizationScale;
import io.github.opencivilizationplatform.modules.civilization.domain.CivilizationStatus;
import io.github.opencivilizationplatform.modules.civilization.infrastructure.CivilizationRepository;
import io.github.opencivilizationplatform.modules.trade.application.MarketPriceService;
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

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
public class ResourceMarketIT {

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
    private MarketPriceService marketPriceService;

    @Test
    void testScarcitySpikesResourcePrices() {
        // Clear existing civilizations to start fresh
        civilizationRepository.deleteAll();

        // 1. Check baseline prices
        marketPriceService.updatePrices();
        double initialFoodPrice = marketPriceService.getCurrentPrice("food");
        assertEquals(10.0, initialFoodPrice, "Initial food price should be base 10");

        // 2. Create a civilization with severe food scarcity (< 30 units)
        Civilization civ = new Civilization();
        civ.setName("Scarcity Settlement");
        civ.setRegion("Desert");
        civ.setOwnerToken("token_desert");
        civ.setScale(CivilizationScale.LOCAL);
        civ.setStatus(CivilizationStatus.EMERGING);
        civ.setFood(5.0); // Severe scarcity
        civ.setWater(50.0);
        civ.setMinerals(50.0);
        civ.setEnergy(50.0);
        civ.setHousing(50.0);
        civilizationRepository.save(civ);

        // 3. Trigger price recalculation
        marketPriceService.updatePrices();

        // 4. Verify food price increased due to scarcity count
        double spikedFoodPrice = marketPriceService.getCurrentPrice("food");
        assertTrue(spikedFoodPrice > 10.0, "Food price should increase due to scarcity: " + spikedFoodPrice);
        
        // Clean up
        civilizationRepository.deleteAll();
    }
}
