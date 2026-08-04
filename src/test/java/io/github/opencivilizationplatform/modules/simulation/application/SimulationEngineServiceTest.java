package io.github.opencivilizationplatform.modules.simulation.application;

import tools.jackson.databind.ObjectMapper;
import io.github.opencivilizationplatform.core.event.BiosphereCriticalEvent;
import io.github.opencivilizationplatform.dto.BalanceDTO;
import io.github.opencivilizationplatform.modules.diplomacy.application.DiplomacyEngineService;
import io.github.opencivilizationplatform.modules.life.application.AgentMetabolismService;
import io.github.opencivilizationplatform.modules.life.application.AgentMortalityService;
import io.github.opencivilizationplatform.modules.life.application.HealthDiseaseService;
import io.github.opencivilizationplatform.modules.monitoring.domain.BiosphereMetric;
import io.github.opencivilizationplatform.modules.participation.application.LawExecutionEngine;
import io.github.opencivilizationplatform.modules.participation.application.RuleService;
import io.github.opencivilizationplatform.modules.participation.domain.Rule;
import io.github.opencivilizationplatform.modules.physics.application.ClimateDisasterService;
import io.github.opencivilizationplatform.modules.production.application.ComplexGoodsProductionService;
import io.github.opencivilizationplatform.modules.region.application.TerritoryControlService;
import io.github.opencivilizationplatform.modules.simulation.api.dto.SimulationStatusResponse;
import io.github.opencivilizationplatform.modules.social.application.ImmigrationService;
import io.github.opencivilizationplatform.modules.strategy.application.BalanceService;
import io.github.opencivilizationplatform.modules.universe.application.UniverseService;
import io.github.opencivilizationplatform.modules.physics.application.PhysicsEngineService;
import io.github.opencivilizationplatform.modules.life.application.AgentKnowledgeService;
import io.github.opencivilizationplatform.modules.production.application.ToolforgeService;
import io.github.opencivilizationplatform.modules.cortex.application.AgentBrainRuntimeService;
import io.github.opencivilizationplatform.modules.physics.application.ResourceStewardshipService;
import io.github.opencivilizationplatform.modules.region.application.ExplorationColonyService;
import io.github.opencivilizationplatform.modules.strategy.application.SocietalEvolutionService;
import io.github.opencivilizationplatform.modules.region.application.AgentSpatialMapService;
import io.github.opencivilizationplatform.modules.social.application.SocialGraphService;
import io.github.opencivilizationplatform.modules.social.application.CultureArtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SimulationEngineServiceTest {

    @Mock
    private RuleService ruleService;

    @Mock
    private BalanceService balanceService;

    @Mock
    private UniverseService universeService;

    @Mock
    private PhysicsEngineService physicsEngineService;

    @Mock
    private AgentMetabolismService agentMetabolismService;

    @Mock
    private TerritoryControlService territoryControlService;

    @Mock
    private LawExecutionEngine lawExecutionEngine;

    @Mock
    private DiplomacyEngineService diplomacyEngineService;

    @Mock
    private AgentMortalityService agentMortalityService;

    @Mock
    private ImmigrationService immigrationService;

    @Mock
    private HealthDiseaseService healthDiseaseService;

    @Mock
    private ClimateDisasterService climateDisasterService;

    @Mock
    private ComplexGoodsProductionService complexGoodsProductionService;

    @Mock
    private SocietalEvolutionService societalEvolutionService;

    @Mock
    private ResourceStewardshipService resourceStewardshipService;

    @Mock
    private ExplorationColonyService explorationColonyService;

    @Mock
    private AgentKnowledgeService agentKnowledgeService;

    @Mock
    private ToolforgeService toolforgeService;

    @Mock
    private AgentBrainRuntimeService agentBrainRuntimeService;

    @Mock
    private AgentSpatialMapService agentSpatialMapService;

    @Mock
    private SocialGraphService socialGraphService;

    @Mock
    private CultureArtService cultureArtService;

    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private SimulationEngineService simulationEngineService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRunSimulationCycleNoRules() {
        when(ruleService.getValidatedRules()).thenReturn(new ArrayList<>());
        
        simulationEngineService.runSimulationCycle();
        
        SimulationStatusResponse status = simulationEngineService.getStatus();
        assertEquals(0, status.getActiveRulesCount());
        verify(balanceService, never()).getBalanceReport();
        verify(lawExecutionEngine, never()).evaluateAndApplyLaws(any(), any());
    }

    @Test
    void testRunSimulationCycleWithReserveCheck() throws Exception {
        Rule rule = new Rule();
        rule.setId(1L);
        rule.setTitle("Water Scarcity Rule");
        rule.setLogicCode("{\"type\": \"RESERVE_CHECK\", \"metric\": \"WATER\"}");
        
        List<Rule> rules = List.of(rule);
        when(ruleService.getValidatedRules()).thenReturn(rules);
        
        BalanceDTO balanceItem = new BalanceDTO("WATER", 75.0, 100.0, "units", 75.0, "DEFICIT");
        List<BalanceDTO> balance = List.of(balanceItem);
        when(balanceService.getBalanceReport()).thenReturn(balance);
        
        simulationEngineService.runSimulationCycle();
        
        SimulationStatusResponse status = simulationEngineService.getStatus();
        assertEquals(1, status.getActiveRulesCount());
        assertTrue(status.getLastDecision().contains("WATER deficiency detected (75.0%)"));

        verify(lawExecutionEngine, times(1)).evaluateAndApplyLaws(rules, balance);
        verify(agentMetabolismService, times(1)).processMetabolism(100, 100.0, 150.0);
        verify(territoryControlService, times(1)).processTerritoryTick(1.0);
        verify(diplomacyEngineService, times(1)).processDiplomacyCycle();
        verify(agentMortalityService, times(1)).processLifecycleTick(100, 0.02, 0.01);
        verify(immigrationService, times(1)).processMigrationCycle(75.0, 50.0, 100);
        verify(healthDiseaseService, times(1)).processHealthTick(100, 45.0, 50.0, 2);
        verify(climateDisasterService, times(1)).processClimateCycle(22.0);
        verify(complexGoodsProductionService, times(1)).processProductionCycle(any());
        verify(societalEvolutionService, times(1)).processEvolutionCycle(5, 120.0);
        verify(resourceStewardshipService, times(1)).processStewardshipTick(50.0, 20.0, 60.0, 100);
        verify(explorationColonyService, times(1)).processExplorationTick(10.0);
        verify(agentKnowledgeService, times(1)).processKnowledgeTick();
        verify(toolforgeService, times(1)).processToolforgeTick();
        verify(agentBrainRuntimeService, times(1)).processBrainRuntimeTick();
        verify(agentSpatialMapService, times(1)).processSpatialTick();
        verify(socialGraphService, times(1)).processSocialGraphTick();
        verify(cultureArtService, times(1)).processCultureTick();
    }

    @Test
    void testOnBiosphereCritical() {
        BiosphereMetric metric = new BiosphereMetric();
        metric.setName("CO2_LEVEL");
        metric.setValue(450.5);
        
        BiosphereCriticalEvent event = new BiosphereCriticalEvent(this, metric);
        
        simulationEngineService.onBiosphereCritical(event);
        
        SimulationStatusResponse status = simulationEngineService.getStatus();
        assertTrue(status.getLastDecision().contains("AUTO-REACTION: Biosphere Redline! Target: CO2_LEVEL"));
    }
}
