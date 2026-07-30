package io.github.opencivilizationplatform.modules.cortex;

import io.github.opencivilizationplatform.config.SharedRedisContainer;
import io.github.opencivilizationplatform.modules.civilization.domain.Civilization;
import io.github.opencivilizationplatform.config.seed.CivilizationScale;
import io.github.opencivilizationplatform.modules.civilization.domain.CivilizationStatus;
import io.github.opencivilizationplatform.modules.civilization.infrastructure.CivilizationRepository;
import io.github.opencivilizationplatform.modules.participation.domain.Rule;
import io.github.opencivilizationplatform.modules.participation.domain.RuleStatus;
import io.github.opencivilizationplatform.modules.participation.infrastructure.RuleRepository;
import io.github.opencivilizationplatform.modules.nexus.infrastructure.MeshTradeRepository;
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
public class CircularBarterIT {

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
    private MeshTradeRepository meshTradeRepository;

    @Autowired
    private CortexEngineService cortexEngineService;

    @Test
    void testTriangularCircularBarterRouting() {
        civilizationRepository.deleteAll();
        ruleRepository.deleteAll();
        meshTradeRepository.deleteAll();

        // 1. Create Civilization A: Deficient in FOOD (20.0), Surplus of MINERALS (100.0)
        Civilization A = new Civilization();
        A.setName("A_Settlement");
        A.setRegion("A_Reg");
        A.setOwnerToken("tokA");
        A.setScale(CivilizationScale.LOCAL);
        A.setStatus(CivilizationStatus.EMERGING);
        A.setFood(20.0); // Deficient (< 30.0)
        A.setWater(100.0);
        A.setMinerals(100.0); // OfferedResource Surplus (> 60.0)
        A.setEnergy(100.0);
        A.setHousing(100.0);
        A = civilizationRepository.saveAndFlush(A);

        // 2. Create Civilization B (Partner 1): Stock of FOOD > 80.0, Stock of MINERALS < 30.0, Surplus of ENERGY > 60.0
        Civilization B = new Civilization();
        B.setName("B_Settlement");
        B.setRegion("B_Reg");
        B.setOwnerToken("tokB");
        B.setScale(CivilizationScale.LOCAL);
        B.setStatus(CivilizationStatus.EMERGING);
        B.setFood(90.0); // Stock > 80.0
        B.setWater(10.0); // Lower water so it's not selected as surplus before ENERGY
        B.setMinerals(10.0); // OfferedResource Stock < 30.0
        B.setEnergy(100.0); // Surplus p1SurplusResource > 60.0
        B.setHousing(100.0);
        B = civilizationRepository.saveAndFlush(B);

        // 3. Create Civilization C (Partner 2): Stock of ENERGY < 30.0, Stock of MINERALS > 80.0, Stock of FOOD > 50.0
        Civilization C = new Civilization();
        C.setName("C_Settlement");
        C.setRegion("C_Reg");
        C.setOwnerToken("tokC");
        C.setScale(CivilizationScale.LOCAL);
        C.setStatus(CivilizationStatus.EMERGING);
        C.setFood(100.0);
        C.setWater(100.0);
        C.setMinerals(90.0); // OfferedResource Stock > 80.0
        C.setEnergy(10.0); // Surplus p1SurplusResource < 30.0
        C.setHousing(100.0);
        C = civilizationRepository.saveAndFlush(C);

        // 4. Configure AUTONOMOUS_TRADE rule for all 3 civilizations
        for (Civilization civ : List.of(A, B, C)) {
            Rule r = new Rule();
            r.setCivilization(civ);
            r.setTitle("Autonomous Trade Protocol");
            r.setDescription("Allows automated mesh trade cycles");
            r.setStatus(RuleStatus.ACTIVE);
            r.setLogicCode("{\"type\": \"AUTONOMOUS_TRADE\"}");
            r.setValidationStatus(io.github.opencivilizationplatform.modules.participation.domain.ValidationStatus.SCIENTIFICALLY_VALIDATED);
            ruleRepository.saveAndFlush(r);
        }

        // 5. Run cortexEngineService.performTick()
        cortexEngineService.performTick();

        // 6. Verify trade logs / mesh trades saved in database
        List<io.github.opencivilizationplatform.modules.nexus.domain.MeshTrade> trades = meshTradeRepository.findAll();
        boolean hasA = trades.stream().anyMatch(t -> "TRIANGULAR_BARTER_A".equals(t.getTradeType()));
        boolean hasB = trades.stream().anyMatch(t -> "TRIANGULAR_BARTER_B".equals(t.getTradeType()));
        boolean hasC = trades.stream().anyMatch(t -> "TRIANGULAR_BARTER_C".equals(t.getTradeType()));

        assertTrue(hasA, "Should have executed TRIANGULAR_BARTER_A");
        assertTrue(hasB, "Should have executed TRIANGULAR_BARTER_B");
        assertTrue(hasC, "Should have executed TRIANGULAR_BARTER_C");
    }
}
