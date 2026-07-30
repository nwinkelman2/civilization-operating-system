package io.github.opencivilizationplatform.modules.cortex;

import io.github.opencivilizationplatform.config.SharedRedisContainer;
import io.github.opencivilizationplatform.modules.civilization.domain.Civilization;
import io.github.opencivilizationplatform.config.seed.CivilizationScale;
import io.github.opencivilizationplatform.modules.civilization.domain.CivilizationStatus;
import io.github.opencivilizationplatform.modules.civilization.infrastructure.CivilizationRepository;
import io.github.opencivilizationplatform.modules.cortex.cortex.CortexEngineService;
import io.github.opencivilizationplatform.modules.region.domain.ResourceRegion;
import io.github.opencivilizationplatform.modules.region.infrastructure.ResourceRegionRepository;
import io.github.opencivilizationplatform.modules.participation.domain.Rule;
import io.github.opencivilizationplatform.modules.participation.domain.RuleStatus;
import io.github.opencivilizationplatform.modules.participation.domain.ValidationStatus;
import io.github.opencivilizationplatform.modules.participation.infrastructure.RuleRepository;
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
public class BiosphereCollapseIT {

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
    private ResourceRegionRepository resourceRegionRepository;

    @Autowired
    private RuleRepository ruleRepository;

    @Autowired
    private CortexEngineService cortexEngineService;

    @Test
    void testExtractionDepletesSoilFertilityAndRegenerates() {
        // Create resource region
        ResourceRegion region = new ResourceRegion();
        region.setName("Biosphere Test Region");
        region.setScale(CivilizationScale.LOCAL);
        region.setFoodAvailability(90.0);
        region.setWaterAvailability(90.0);
        region.setMineralAvailability(90.0);
        region.setEnergyAvailability(50.0);
        region.setHousingAvailability(50.0);
        region.setSoilFertility(100.0);
        region.setWaterTable(100.0);
        region = resourceRegionRepository.save(region);

        // Create civilization located in this region
        Civilization civ = new Civilization();
        civ.setName("Ecosystem Lab");
        civ.setRegion("Biosphere Test Region");
        civ.setHomeRegion(region);
        civ.setOwnerToken("token_lab");
        civ.setScale(CivilizationScale.LOCAL);
        civ.setStatus(CivilizationStatus.EMERGING);
        civ.setEnergy(500.0); // Abundant energy to operate robots
        civ.setFood(50.0);
        civ.setWater(50.0);
        civ.setMinerals(50.0);
        civ.setHousing(50.0);
        
        // High robot priorities to trigger fabrication of Agri-Bots (Agropecuária)
        civ.setAgriBotsPriority(100);
        civ.setAquaBotsPriority(0);
        civ.setExploreBotsPriority(0);
        civ.setUtilityBotsPriority(0);
        
        // Let's seed history to contain 20 Agri-Bots so extraction is immediately massive
        civ.setResourceHistory("[{\"agriBots\": 20, \"aquaBots\": 0, \"exploreBots\": 0, \"utilityBots\": 0, \"ecoBots\": 0, \"scienceBots\": 0, \"securityBots\": 0}]");
        civ = civilizationRepository.save(civ);

        // Add OPERATE_ROBOTS governance rule to enable robots operation
        Rule rule = new Rule();
        rule.setTitle("Operate Robots");
        rule.setSector("GENERAL");
        rule.setLogicCode("OPERATE_ROBOTS");
        rule.setDescription("Allows robot operations");
        rule.setStatus(RuleStatus.ACTIVE);
        rule.setValidationStatus(ValidationStatus.SCIENTIFICALLY_VALIDATED);
        rule.setCivilization(civ);
        ruleRepository.save(rule);

        // Run simulation tick
        cortexEngineService.tickForCivilization(civ.getId());

        // Verify soil fertility is depleted (under high Agri-Bots extraction, soil decays)
        ResourceRegion updatedRegion = resourceRegionRepository.findById(region.getId()).orElseThrow();
        assertTrue(updatedRegion.getSoilFertility() < 100.0, "Soil fertility should have been depleted, but was: " + updatedRegion.getSoilFertility());
        
        // Let's reset bot count to 0 in history and add 2 Eco-Bots to verify recovery
        civ.setResourceHistory("[{\"agriBots\": 0, \"aquaBots\": 0, \"exploreBots\": 0, \"utilityBots\": 0, \"ecoBots\": 2, \"scienceBots\": 0, \"securityBots\": 0}]");
        civilizationRepository.save(civ);

        // Set food availability to 0 to prevent base extraction from keeping soil in decay
        updatedRegion.setFoodAvailability(0.0);
        updatedRegion = resourceRegionRepository.save(updatedRegion);

        double depletedSoil = updatedRegion.getSoilFertility();

        // Run tick with no extraction and 2 eco bots
        cortexEngineService.tickForCivilization(civ.getId());

        ResourceRegion regeneratedRegion = resourceRegionRepository.findById(region.getId()).orElseThrow();
        assertTrue(regeneratedRegion.getSoilFertility() > depletedSoil, "Soil fertility should have regenerated with eco-bots active");
    }
}
