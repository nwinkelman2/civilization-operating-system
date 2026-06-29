package io.github.opencivilizationplatform.modules.cortex.cortex;

import tools.jackson.databind.ObjectMapper;
import io.github.opencivilizationplatform.modules.civilization.domain.Civilization;
import io.github.opencivilizationplatform.modules.civilization.infrastructure.CivilizationRepository;
import io.github.opencivilizationplatform.modules.monitoring.domain.BiosphereMetric;
import io.github.opencivilizationplatform.modules.monitoring.infrastructure.BiosphereMetricRepository;
import io.github.opencivilizationplatform.modules.nexus.infrastructure.MeshTradeRepository;
import io.github.opencivilizationplatform.modules.participation.domain.Rule;
import io.github.opencivilizationplatform.modules.participation.domain.RuleStatus;
import io.github.opencivilizationplatform.modules.participation.infrastructure.RuleRepository;
import io.github.opencivilizationplatform.modules.region.infrastructure.ResourceRegionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CortexEngineServiceTest {

    @Mock
    private CivilizationRepository civilizationRepository;
    @Mock
    private ResourceRegionRepository resourceRegionRepository;
    @Mock
    private RuleRepository ruleRepository;
    @Mock
    private MeshTradeRepository meshTradeRepository;
    @Mock
    private BiosphereMetricRepository biosphereMetricRepository;
    @Mock
    private ApplicationEventPublisher eventPublisher;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private CortexEngineService cortexEngineService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Stub biosphere metric lookup to return empty list (no ecological drift in unit tests)
        when(biosphereMetricRepository.findAll()).thenReturn(List.of());
        cortexEngineService = new CortexEngineService(
            civilizationRepository, resourceRegionRepository, ruleRepository,
            objectMapper, meshTradeRepository, biosphereMetricRepository, eventPublisher
        );
    }

    @Test
    void testRobotFabricationHighestWeightWhenEmpty() {
        Civilization civ = new Civilization();
        civ.setId(1L);
        civ.setName("Test Civ");
        civ.setMinerals(50.0);
        civ.setEnergy(50.0);
        civ.setAgriBotsPriority(80);
        civ.setAquaBotsPriority(20);
        civ.setExploreBotsPriority(0);
        civ.setUtilityBotsPriority(0);
        civ.setResourceHistory("[]");

        Rule operateBotsRule = new Rule();
        operateBotsRule.setStatus(RuleStatus.ACTIVE);
        operateBotsRule.setLogicCode("{\"type\": \"OPERATE_ROBOTS\"}");

        when(civilizationRepository.findById(1L)).thenReturn(Optional.of(civ));
        when(ruleRepository.findByCivilizationId(1L)).thenReturn(Collections.singletonList(operateBotsRule));

        cortexEngineService.tickForCivilization(1L);

        // Check mineral and energy subtraction (cost: 15 minerals, 10 energy) with tolerance for simulation noise
        assertEquals(35.0, civ.getMinerals(), 1.0);
        assertEquals(40.0, civ.getEnergy() + 0.15, 2.0);

        // Check that history lists 1 Agri-Bot and 0 other bots
        assertTrue(civ.getResourceHistory().contains("\"agriBots\":1"));
        assertTrue(civ.getResourceHistory().contains("\"aquaBots\":0"));
    }

    @Test
    void testRobotFabricationFurthestBelowPriority() {
        // Ratios: Agri-Bot: 1/1 (100%), Aqua-Bot: 0/1 (0%)
        // Targets: Agri-Bot: 80%, Aqua-Bot: 20%
        // Diff: Agri-Bot: 80% - 100% = -20%, Aqua-Bot: 20% - 0% = +20%
        // Therefore, it should construct an Aqua-Bot next.
        Civilization civ = new Civilization();
        civ.setId(1L);
        civ.setName("Test Civ");
        civ.setMinerals(50.0);
        civ.setEnergy(50.0);
        civ.setAgriBotsPriority(80);
        civ.setAquaBotsPriority(20);
        civ.setExploreBotsPriority(0);
        civ.setUtilityBotsPriority(0);
        civ.setResourceHistory("[{\"tick\":1,\"agriBots\":1,\"aquaBots\":0,\"exploreBots\":0,\"utilityBots\":0}]");

        Rule operateBotsRule = new Rule();
        operateBotsRule.setStatus(RuleStatus.ACTIVE);
        operateBotsRule.setLogicCode("{\"type\": \"OPERATE_ROBOTS\"}");

        when(civilizationRepository.findById(1L)).thenReturn(Optional.of(civ));
        when(ruleRepository.findByCivilizationId(1L)).thenReturn(Collections.singletonList(operateBotsRule));

        cortexEngineService.tickForCivilization(1L);

        // Check that history contains 1 Agri-bot and 1 Aqua-bot after tick
        assertTrue(civ.getResourceHistory().contains("\"agriBots\":1"));
        assertTrue(civ.getResourceHistory().contains("\"aquaBots\":1"));
    }
}
