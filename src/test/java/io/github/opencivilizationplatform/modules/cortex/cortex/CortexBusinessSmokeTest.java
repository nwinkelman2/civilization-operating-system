package io.github.opencivilizationplatform.modules.cortex.cortex;

import io.github.opencivilizationplatform.config.SharedRedisContainer;
import io.github.opencivilizationplatform.config.seed.CivilizationScale;
import io.github.opencivilizationplatform.modules.civilization.domain.Civilization;
import io.github.opencivilizationplatform.modules.civilization.domain.CivilizationStatus;
import io.github.opencivilizationplatform.modules.civilization.infrastructure.CivilizationRepository;
import io.github.opencivilizationplatform.modules.region.domain.ResourceRegion;
import io.github.opencivilizationplatform.modules.region.infrastructure.ResourceRegionRepository;
import io.github.opencivilizationplatform.modules.participation.domain.Rule;
import io.github.opencivilizationplatform.modules.participation.domain.RuleStatus;
import io.github.opencivilizationplatform.modules.participation.infrastructure.RuleRepository;
import io.github.opencivilizationplatform.modules.nexus.infrastructure.MeshTradeRepository;
import io.github.opencivilizationplatform.modules.technology.infrastructure.TechnologyRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class CortexBusinessSmokeTest {

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", SharedRedisContainer.redis::getHost);
        registry.add("spring.data.redis.port", SharedRedisContainer.redis::getFirstMappedPort);
    }

    @Autowired
    private CortexEngineService cortexEngineService;

    @Autowired
    private CivilizationRepository civilizationRepository;

    @Autowired
    private ResourceRegionRepository resourceRegionRepository;

    @Autowired
    private RuleRepository ruleRepository;

    @Autowired
    private TechnologyRepository technologyRepository;

    @Autowired
    private MeshTradeRepository meshTradeRepository;

    @BeforeEach
    void setUp() {
        meshTradeRepository.deleteAll();
        ruleRepository.deleteAll();
        civilizationRepository.deleteAll();
        resourceRegionRepository.deleteAll();
        technologyRepository.deleteAll();
    }

    @Test
    void testPersonnelDrainLoophole() {
        // Setup: A civilization has 45 population, which meets the "population >= 40" barter condition
        // But if it needs multiple scarce resources, it might trigger multiple trades and drain the population down below the stability limits.
        
        ResourceRegion region1 = new ResourceRegion();
        region1.setName("Arid Region");
        region1.setScale(CivilizationScale.LOCAL);
        region1.setFoodAvailability(5.0); // deficient
        region1.setWaterAvailability(5.0); // deficient
        region1.setMineralAvailability(90.0);
        region1.setEnergyAvailability(90.0);
        region1.setHousingAvailability(90.0);
        region1 = resourceRegionRepository.save(region1);

        Civilization civ = new Civilization();
        civ.setName("Starving Community");
        civ.setHomeRegion(region1);
        civ.setPopulation(45);
        civ.setFood(10.0);
        civ.setWater(10.0);
        civ.setMinerals(10.0);
        civ.setEnergy(10.0);
        civ.setHousing(50.0);
        civ.setScale(CivilizationScale.LOCAL);
        civ.setRegion("Test Region 1");
        civ.setStatus(CivilizationStatus.ACTIVE);
        civ.setAgriBotsPriority(25);
        civ.setAquaBotsPriority(25);
        civ.setExploreBotsPriority(25);
        civ.setUtilityBotsPriority(25);
        civ.setOwnerToken("token1");
        civ = civilizationRepository.save(civ);

        // Setup a rich partner who has plenty of food/water
        ResourceRegion region2 = new ResourceRegion();
        region2.setName("Fertile Oasis");
        region2.setScale(CivilizationScale.LOCAL);
        region2.setFoodAvailability(95.0);
        region2.setWaterAvailability(95.0);
        region2.setMineralAvailability(50.0);
        region2.setEnergyAvailability(50.0);
        region2.setHousingAvailability(50.0);
        region2 = resourceRegionRepository.save(region2);

        Civilization partner = new Civilization();
        partner.setName("Abundant Oasis");
        partner.setHomeRegion(region2);
        partner.setPopulation(100);
        partner.setFood(150.0);
        partner.setWater(150.0);
        partner.setMinerals(50.0);
        partner.setEnergy(50.0);
        partner.setHousing(50.0);
        partner.setScale(CivilizationScale.LOCAL);
        partner.setRegion("Test Region 2");
        partner.setStatus(CivilizationStatus.ACTIVE);
        partner.setAgriBotsPriority(25);
        partner.setAquaBotsPriority(25);
        partner.setExploreBotsPriority(25);
        partner.setUtilityBotsPriority(25);
        partner.setOwnerToken("token2");
        partner = civilizationRepository.save(partner);

        // Enable autonomous trading rule on both
        Rule tradeRule1 = new Rule();
        tradeRule1.setCivilization(civ);
        tradeRule1.setTitle("Mesh Trade Protocol");
        tradeRule1.setDescription("Allows autonomous mesh barters and exchanges");
        tradeRule1.setLogicCode("{\"type\": \"AUTOMATION\", \"action\": \"AUTONOMOUS_TRADE\"}");
        tradeRule1.setStatus(RuleStatus.ACTIVE);
        tradeRule1.setValidationStatus(io.github.opencivilizationplatform.modules.participation.domain.ValidationStatus.SCIENTIFICALLY_VALIDATED);
        ruleRepository.save(tradeRule1);

        Rule tradeRule2 = new Rule();
        tradeRule2.setCivilization(partner);
        tradeRule2.setTitle("Mesh Trade Protocol");
        tradeRule2.setDescription("Allows autonomous mesh barters and exchanges");
        tradeRule2.setLogicCode("{\"type\": \"AUTOMATION\", \"action\": \"AUTONOMOUS_TRADE\"}");
        tradeRule2.setStatus(RuleStatus.ACTIVE);
        tradeRule2.setValidationStatus(io.github.opencivilizationplatform.modules.participation.domain.ValidationStatus.SCIENTIFICALLY_VALIDATED);
        ruleRepository.save(tradeRule2);

        // Run tick for civ
        cortexEngineService.tickForCivilization(civ.getId());

        // Reload from DB
        Civilization updatedCiv = civilizationRepository.findById(civ.getId()).orElseThrow();
        Civilization updatedPartner = civilizationRepository.findById(partner.getId()).orElseThrow();

        // One resource deficit (FOOD) was processed.
        // It successfully imported food in exchange for personnel.
        // Population should be: 45 - 5 (trade) + 1 (birth/growth) = 41.
        assertEquals(41, updatedCiv.getPopulation());
        assertEquals(105, updatedPartner.getPopulation());

        // Gap check: if we run it again, does it allow trading below 40?
        // Let's force the population to 39 and check if personnel swap still occurs.
        updatedCiv.setPopulation(39);
        updatedCiv.setFood(10.0);
        civilizationRepository.save(updatedCiv);

        cortexEngineService.tickForCivilization(updatedCiv.getId());

        Civilization postTickCiv = civilizationRepository.findById(civ.getId()).orElseThrow();
        // Since population is < 40, no personnel barter should occur.
        // Population gets +1 from natural growth: 39 + 1 = 40.
        assertEquals(40, postTickCiv.getPopulation());
    }

    @Test
    void testSecurityBotReputationMaskingExploit() {
        // If a civilization is starving (0 food, 0 water), reputation should fall.
        // But if it operates 15 Security-Bots, they add +6 reputation score per tick.
        // This is a business loophole: security forces mask the starvation of the population.
        
        Civilization civ = new Civilization();
        civ.setName("Police State");
        civ.setPopulation(100);
        civ.setFood(0.0); // starving!
        civ.setWater(0.0); // starving!
        civ.setMinerals(100.0);
        civ.setEnergy(100.0);
        civ.setHousing(50.0);
        civ.setScale(CivilizationScale.LOCAL);
        civ.setRegion("Test Region 3");
        civ.setStatus(CivilizationStatus.ACTIVE);
        civ.setReputationScore(50.0);
        
        // Max priorities for security
        civ.setAgriBotsPriority(0);
        civ.setAquaBotsPriority(0);
        civ.setExploreBotsPriority(0);
        civ.setUtilityBotsPriority(0);
        civ.setEcoBotsPriority(0);
        civ.setScienceBotsPriority(0);
        civ.setSecurityBotsPriority(100);
        
        // Mock 15 security bots in history
        civ.setResourceHistory("[{\"tick\":1,\"agriBots\":0,\"aquaBots\":0,\"exploreBots\":0,\"utilityBots\":0,\"ecoBots\":0,\"scienceBots\":0,\"securityBots\":15}]");
        civ.setOwnerToken("token3");
        civ = civilizationRepository.save(civ);

        Rule operateBotsRule = new Rule();
        operateBotsRule.setCivilization(civ);
        operateBotsRule.setTitle("Robots Act");
        operateBotsRule.setDescription("Configures bot priorities and operations");
        operateBotsRule.setLogicCode("{\"type\": \"AUTOMATION\", \"action\": \"OPERATE_ROBOTS\"}");
        operateBotsRule.setStatus(RuleStatus.ACTIVE);
        operateBotsRule.setValidationStatus(io.github.opencivilizationplatform.modules.participation.domain.ValidationStatus.SCIENTIFICALLY_VALIDATED);
        ruleRepository.save(operateBotsRule);

        cortexEngineService.tickForCivilization(civ.getId());

        Civilization updatedCiv = civilizationRepository.findById(civ.getId()).orElseThrow();

        // Starvation penalty = -5.0. Resource stock penalty = -2.0. Total penalty = -7.0.
        // Security-Bot stability boost is scaled down by 80% due to starvation: 15 * 0.4 * 0.2 = +1.2.
        // Net delta is -5.8. Final reputation: 50.0 - 5.8 = 44.2.
        // This validates that Security-Bots can no longer fully mask starvation.
        assertEquals(44.2, updatedCiv.getReputationScore(), 0.001);
    }

    @Test
    void testTechnologyPrerequisitesLock() {
        Civilization civ = new Civilization();
        civ.setName("Pre-Industrial Society");
        civ.setPopulation(100);
        civ.setFood(100.0);
        civ.setWater(100.0);
        civ.setMinerals(100.0);
        civ.setEnergy(100.0);
        civ.setHousing(50.0);
        civ.setScale(CivilizationScale.LOCAL);
        civ.setRegion("Test Region 4");
        civ.setStatus(CivilizationStatus.ACTIVE);
        civ.setReputationScore(50.0);
        
        // Priority all to Eco-Bots
        civ.setAgriBotsPriority(0);
        civ.setAquaBotsPriority(0);
        civ.setExploreBotsPriority(0);
        civ.setUtilityBotsPriority(0);
        civ.setEcoBotsPriority(100);
        civ.setScienceBotsPriority(0);
        civ.setSecurityBotsPriority(0);
        civ.setResourceHistory("[]");
        civ.setOwnerToken("token4");
        civ = civilizationRepository.save(civ);

        // Save a mock technology that is NOT completed (e.g. status = RESEARCHING)
        io.github.opencivilizationplatform.modules.technology.domain.Technology tech = new io.github.opencivilizationplatform.modules.technology.domain.Technology();
        tech.setName("Water Wells");
        tech.setCategory(io.github.opencivilizationplatform.modules.technology.domain.TechnologyCategory.AGRICULTURE);
        tech.setTier(1);
        tech.setResearchCost(50);
        tech.setResearchProgress(10);
        tech.setStatus(io.github.opencivilizationplatform.modules.technology.domain.TechnologyStatus.RESEARCHING);
        tech.setCivilizationId(civ.getId());
        technologyRepository.save(tech);

        Rule operateBotsRule = new Rule();
        operateBotsRule.setCivilization(civ);
        operateBotsRule.setTitle("Robots Act");
        operateBotsRule.setDescription("Configures bot priorities and operations");
        operateBotsRule.setLogicCode("{\"type\": \"AUTOMATION\", \"action\": \"OPERATE_ROBOTS\"}");
        operateBotsRule.setStatus(RuleStatus.ACTIVE);
        operateBotsRule.setValidationStatus(io.github.opencivilizationplatform.modules.participation.domain.ValidationStatus.SCIENTIFICALLY_VALIDATED);
        ruleRepository.save(operateBotsRule);

        cortexEngineService.tickForCivilization(civ.getId());

        Civilization updatedCiv = civilizationRepository.findById(civ.getId()).orElseThrow();
        // Since "Water Wells" (the local tech required for Eco-Bots) is not COMPLETED, no Eco-Bot should be fabricated.
        assertFalse(updatedCiv.getResourceHistory().contains("\"ecoBots\":1"));
    }
}
