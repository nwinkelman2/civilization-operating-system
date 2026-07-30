package io.github.opencivilizationplatform.modules.cortex;

import io.github.opencivilizationplatform.config.SharedRedisContainer;
import io.github.opencivilizationplatform.modules.civilization.domain.Civilization;
import io.github.opencivilizationplatform.config.seed.CivilizationScale;
import io.github.opencivilizationplatform.modules.civilization.domain.CivilizationStatus;
import io.github.opencivilizationplatform.modules.civilization.infrastructure.CivilizationRepository;
import io.github.opencivilizationplatform.modules.technology.domain.Technology;
import io.github.opencivilizationplatform.modules.technology.domain.TechnologyCategory;
import io.github.opencivilizationplatform.modules.technology.domain.TechnologyStatus;
import io.github.opencivilizationplatform.modules.technology.infrastructure.TechnologyRepository;
import io.github.opencivilizationplatform.modules.technology.infrastructure.LicensedTechnologyRepository;
import io.github.opencivilizationplatform.modules.logistics.infrastructure.ShipmentRepository;
import io.github.opencivilizationplatform.modules.logistics.domain.Shipment;
import io.github.opencivilizationplatform.modules.logistics.domain.ShipmentStatus;
import io.github.opencivilizationplatform.modules.social.domain.EspionageOperation;
import io.github.opencivilizationplatform.modules.social.infrastructure.EspionageRepository;
import io.github.opencivilizationplatform.modules.nexus.domain.Treaty;
import io.github.opencivilizationplatform.modules.nexus.domain.TreatyType;
import io.github.opencivilizationplatform.modules.nexus.domain.TreatyStatus;
import io.github.opencivilizationplatform.modules.nexus.infrastructure.TreatyRepository;
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

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
public class AdvancedCooperationAndLogisticsIT {

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
    private TechnologyRepository technologyRepository;

    @Autowired
    private LicensedTechnologyRepository licensedTechnologyRepository;

    @Autowired
    private ShipmentRepository shipmentRepository;

    @Autowired
    private EspionageRepository espionageRepository;

    @Autowired
    private TreatyRepository treatyRepository;

    @Autowired
    private CortexEngineService cortexEngineService;

    @Test
    void testEspionageShipmentInterception() {
        // Cleanup
        espionageRepository.deleteAll();
        shipmentRepository.deleteAll();
        licensedTechnologyRepository.deleteAll();
        technologyRepository.deleteAll();
        treatyRepository.deleteAll();
        civilizationRepository.deleteAll();

        // 1. Create civilizations: Origin, Target, and Attacker
        Civilization origin = new Civilization();
        origin.setName("Origin_Settlement");
        origin.setRegion("RegionA");
        origin.setOwnerToken("tokA");
        origin.setScale(CivilizationScale.LOCAL);
        origin.setStatus(CivilizationStatus.EMERGING);
        origin = civilizationRepository.saveAndFlush(origin);

        Civilization target = new Civilization();
        target.setName("Target_Settlement");
        target.setRegion("RegionB");
        target.setOwnerToken("tokB");
        target.setScale(CivilizationScale.LOCAL);
        target.setStatus(CivilizationStatus.EMERGING);
        target = civilizationRepository.saveAndFlush(target);

        Civilization attacker = new Civilization();
        attacker.setName("Attacker_Settlement");
        attacker.setRegion("RegionC");
        attacker.setOwnerToken("tokC");
        attacker.setScale(CivilizationScale.LOCAL);
        attacker.setStatus(CivilizationStatus.EMERGING);
        attacker = civilizationRepository.saveAndFlush(attacker);

        // 2. Setup Shipment IN_TRANSIT from Origin to Target
        Shipment shipment = new Shipment();
        shipment.setOrigin(origin.getName());
        shipment.setDestination(target.getName());
        shipment.setCargo("FOOD");
        shipment.setQuantity(100.0);
        shipment.setUnit("UNITS");
        shipment.setStatus(ShipmentStatus.IN_TRANSIT);
        shipment.setEta(LocalDateTime.now().plusDays(1)); // future
        shipment = shipmentRepository.saveAndFlush(shipment);

        // 3. Setup Espionage Operation by Attacker against Target
        EspionageOperation op = new EspionageOperation();
        op.setInitiator(attacker);
        op.setTarget(target);
        op.setType("INTERCEPT_SHIPMENT");
        op.setSpyBotsCount(10); // success guaranteed (riskLevel is 0)
        op.setRiskLevel(0.0);
        op.setTicksRemaining(1);
        op.setStatus("IN_PROGRESS");
        op.setCreatedAt(LocalDateTime.now());
        op = espionageRepository.saveAndFlush(op);

        // 4. Tick
        cortexEngineService.performTick();

        // 5. Verify the shipment's destination was hijacked by the attacker
        Shipment updatedShipment = shipmentRepository.findById(shipment.getId()).orElseThrow();
        assertEquals(attacker.getName(), updatedShipment.getDestination());
        assertEquals(ShipmentStatus.IN_TRANSIT, updatedShipment.getStatus());

        // Verify the espionage status updated to SUCCESS
        EspionageOperation updatedOp = espionageRepository.findById(op.getId()).orElseThrow();
        assertEquals("SUCCESS", updatedOp.getStatus());
    }

    @Test
    void testResearchAlliancePassiveBonus() {
        // Cleanup
        espionageRepository.deleteAll();
        shipmentRepository.deleteAll();
        licensedTechnologyRepository.deleteAll();
        technologyRepository.deleteAll();
        treatyRepository.deleteAll();
        civilizationRepository.deleteAll();

        // 1. Create civilizations: A and B
        Civilization civA = new Civilization();
        civA.setName("CivA");
        civA.setRegion("RegionA");
        civA.setOwnerToken("tokA");
        civA.setScale(CivilizationScale.LOCAL);
        civA.setStatus(CivilizationStatus.EMERGING);
        civA = civilizationRepository.saveAndFlush(civA);

        Civilization civB = new Civilization();
        civB.setName("CivB");
        civB.setRegion("RegionB");
        civB.setOwnerToken("tokB");
        civB.setScale(CivilizationScale.LOCAL);
        civB.setStatus(CivilizationStatus.EMERGING);
        civB = civilizationRepository.saveAndFlush(civB);

        // 2. Setup RESEARCH_ALLIANCE treaty between A and B
        Treaty treaty = new Treaty();
        treaty.setTitle("Research Alliance A-B");
        treaty.setType(TreatyType.RESEARCH_ALLIANCE);
        treaty.setProposerCivId(civA.getId());
        treaty.setSignatoryCivIds("[" + civA.getId() + "," + civB.getId() + "]");
        treaty.setStatus(TreatyStatus.ACTIVE);
        treaty.setProposedAt(LocalDateTime.now());
        treaty.setExpiresAt(LocalDateTime.now().plusDays(5));
        treatyRepository.saveAndFlush(treaty);

        // 3. Setup active research technology for A
        Technology tech = new Technology();
        tech.setName("Water Wells");
        tech.setCategory(TechnologyCategory.AGRICULTURE);
        tech.setTier(1);
        tech.setResearchCost(50);
        tech.setResearchProgress(10);
        tech.setStatus(TechnologyStatus.RESEARCHING);
        tech.setCivilizationId(civA.getId());
        tech = technologyRepository.saveAndFlush(tech);

        // 4. Tick
        cortexEngineService.performTick();

        // 5. Verify science bonus is applied to tech research progress
        // (with 0 science bots, passive progress is +1.0)
        Technology updatedTech = technologyRepository.findById(tech.getId()).orElseThrow();
        assertEquals(11, updatedTech.getResearchProgress()); // 10 + 1
    }
}
