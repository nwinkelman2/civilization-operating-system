package io.github.opencivilizationplatform.modules.cortex;

import io.github.opencivilizationplatform.config.SharedRedisContainer;
import io.github.opencivilizationplatform.config.seed.CivilizationScale;
import io.github.opencivilizationplatform.modules.civilization.domain.Civilization;
import io.github.opencivilizationplatform.modules.civilization.domain.CivilizationStatus;
import io.github.opencivilizationplatform.modules.civilization.infrastructure.CivilizationRepository;
import io.github.opencivilizationplatform.modules.technology.domain.Technology;
import io.github.opencivilizationplatform.modules.technology.domain.TechnologyCategory;
import io.github.opencivilizationplatform.modules.technology.domain.TechnologyStatus;
import io.github.opencivilizationplatform.modules.technology.infrastructure.TechnologyRepository;
import io.github.opencivilizationplatform.modules.technology.infrastructure.LicensedTechnologyRepository;
import io.github.opencivilizationplatform.modules.technology.domain.LicensedTechnology;
import io.github.opencivilizationplatform.modules.logistics.infrastructure.ShipmentRepository;
import io.github.opencivilizationplatform.modules.logistics.domain.Shipment;
import io.github.opencivilizationplatform.modules.logistics.domain.ShipmentStatus;
import io.github.opencivilizationplatform.modules.technology.application.TechnologyService;
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
public class LogisticsAndLicensingIT {

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
    private TechnologyService technologyService;

    @Autowired
    private CortexEngineService cortexEngineService;

    @Test
    void testLicensingAndRoyalties() {
        licensedTechnologyRepository.deleteAll();
        technologyRepository.deleteAll();
        shipmentRepository.deleteAll();
        civilizationRepository.deleteAll();

        // 1. Create Licensor A
        Civilization licensor = new Civilization();
        licensor.setName("Licensor_Settlement");
        licensor.setRegion("RegionA");
        licensor.setOwnerToken("tokA");
        licensor.setScale(CivilizationScale.LOCAL);
        licensor.setStatus(CivilizationStatus.EMERGING);
        licensor.setConsensusCoins(10.0);
        licensor = civilizationRepository.saveAndFlush(licensor);

        // 2. Create Licensee B
        Civilization licensee = new Civilization();
        licensee.setName("Licensee_Settlement");
        licensee.setRegion("RegionB");
        licensee.setOwnerToken("tokB");
        licensee.setScale(CivilizationScale.LOCAL);
        licensee.setStatus(CivilizationStatus.EMERGING);
        licensee.setConsensusCoins(20.0);
        licensee = civilizationRepository.saveAndFlush(licensee);

        // 3. Create completed Tech for Licensor A
        Technology tech = new Technology();
        tech.setName("Water Wells");
        tech.setCategory(TechnologyCategory.AGRICULTURE);
        tech.setTier(1);
        tech.setResearchCost(10);
        tech.setResearchProgress(10);
        tech.setStatus(TechnologyStatus.COMPLETED);
        tech.setCivilizationId(licensor.getId());
        tech = technologyRepository.saveAndFlush(tech);

        // 4. Create license
        LicensedTechnology lt = technologyService.licenseTechnology(tech.getId(), licensee.getId(), 5.0);
        assertNotNull(lt);
        assertEquals("Water Wells", lt.getTechName());
        assertEquals(5.0, lt.getFeePerTick());

        // 5. Tick
        cortexEngineService.performTick();

        // 6. Verify Consensus Coins
        Civilization updatedLicensor = civilizationRepository.findById(licensor.getId()).orElseThrow();
        Civilization updatedLicensee = civilizationRepository.findById(licensee.getId()).orElseThrow();

        assertEquals(15.0, updatedLicensor.getConsensusCoins()); // 10 + 5
        assertEquals(15.0, updatedLicensee.getConsensusCoins()); // 20 - 5
    }

    @Test
    void testLogisticsShipments() {
        licensedTechnologyRepository.deleteAll();
        technologyRepository.deleteAll();
        shipmentRepository.deleteAll();
        civilizationRepository.deleteAll();

        // Create Destination Civ
        Civilization dest = new Civilization();
        dest.setName("Destination_Settlement");
        dest.setRegion("RegionDest");
        dest.setOwnerToken("tokDest");
        dest.setScale(CivilizationScale.LOCAL);
        dest.setStatus(CivilizationStatus.EMERGING);
        dest.setFood(10.0);
        dest = civilizationRepository.saveAndFlush(dest);

        // Create Shipment in transit with past ETA
        Shipment shipment = new Shipment();
        shipment.setOrigin("Origin_Settlement");
        shipment.setDestination(dest.getName());
        shipment.setCargo("FOOD");
        shipment.setQuantity(50.0);
        shipment.setUnit("UNITS");
        shipment.setStatus(ShipmentStatus.IN_TRANSIT);
        shipment.setEta(LocalDateTime.now().minusMinutes(1)); // expired
        shipmentRepository.saveAndFlush(shipment);

        // Tick
        cortexEngineService.performTick();

        // Verify Shipment status updated to DELIVERED
        Shipment updatedShipment = shipmentRepository.findById(shipment.getId()).orElseThrow();
        assertEquals(ShipmentStatus.DELIVERED, updatedShipment.getStatus());

        // Verify resource added to destination stock (delivered 50 minus consumption during tick)
        Civilization updatedDest = civilizationRepository.findById(dest.getId()).orElseThrow();
        assertTrue(updatedDest.getFood() > 40.0 && updatedDest.getFood() <= 60.0);
    }
}
