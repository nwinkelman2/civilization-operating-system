package io.github.opencivilizationplatform.modules.simulation.application;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import io.github.opencivilizationplatform.core.event.BiosphereCriticalEvent;
import io.github.opencivilizationplatform.dto.BalanceDTO;
import io.github.opencivilizationplatform.modules.life.application.HealthDiseaseService;
import io.github.opencivilizationplatform.modules.life.application.AgentKnowledgeService;
import io.github.opencivilizationplatform.modules.production.application.ToolforgeService;
import io.github.opencivilizationplatform.modules.cortex.application.AgentBrainRuntimeService;
import io.github.opencivilizationplatform.modules.physics.application.ClimateDisasterService;
import io.github.opencivilizationplatform.modules.production.application.ComplexGoodsProductionService;
import io.github.opencivilizationplatform.modules.diplomacy.application.DiplomacyEngineService;
import io.github.opencivilizationplatform.modules.life.application.AgentMetabolismService;
import io.github.opencivilizationplatform.modules.life.application.AgentMortalityService;
import io.github.opencivilizationplatform.modules.participation.application.LawExecutionEngine;
import io.github.opencivilizationplatform.modules.participation.application.RuleService;
import io.github.opencivilizationplatform.modules.participation.domain.Rule;
import io.github.opencivilizationplatform.modules.region.application.TerritoryControlService;
import io.github.opencivilizationplatform.modules.region.application.AgentSpatialMapService;
import io.github.opencivilizationplatform.modules.social.application.SocialGraphService;
import io.github.opencivilizationplatform.modules.social.application.CultureArtService;
import io.github.opencivilizationplatform.modules.physics.application.WildlifeEcosystemService;
import io.github.opencivilizationplatform.modules.physics.application.DayNightCycleService;
import io.github.opencivilizationplatform.modules.social.application.CivilizationChronicleService;
import io.github.opencivilizationplatform.modules.nexus.application.PluginSdkService;
import io.github.opencivilizationplatform.modules.social.application.TourismPilgrimageService;
import io.github.opencivilizationplatform.modules.trade.application.MonetaryEconomyService;
import io.github.opencivilizationplatform.modules.simulation.api.dto.SimulationStatusResponse;
import io.github.opencivilizationplatform.modules.social.application.ImmigrationService;
import io.github.opencivilizationplatform.modules.strategy.application.BalanceService;
import io.github.opencivilizationplatform.modules.universe.application.UniverseService;
import io.github.opencivilizationplatform.modules.physics.application.PhysicsEngineService;
import io.github.opencivilizationplatform.modules.physics.application.ResourceStewardshipService;
import io.github.opencivilizationplatform.modules.region.application.ExplorationColonyService;
import io.github.opencivilizationplatform.modules.strategy.application.SocietalEvolutionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class SimulationEngineService {

    private static final Logger log = LoggerFactory.getLogger(SimulationEngineService.class);
    private static final int MAX_DECISION_HISTORY = 15;

    private final RuleService ruleService;
    private final BalanceService balanceService;
    private final ObjectMapper objectMapper;
    private final UniverseService universeService;
    private final PhysicsEngineService physicsEngineService;
    private final AgentMetabolismService agentMetabolismService;
    private final TerritoryControlService territoryControlService;
    private final LawExecutionEngine lawExecutionEngine;
    private final DiplomacyEngineService diplomacyEngineService;
    private final AgentMortalityService agentMortalityService;
    private final ImmigrationService immigrationService;
    private final HealthDiseaseService healthDiseaseService;
    private final ClimateDisasterService climateDisasterService;
    private final ComplexGoodsProductionService complexGoodsProductionService;
    private final SocietalEvolutionService societalEvolutionService;
    private final ResourceStewardshipService resourceStewardshipService;
    private final ExplorationColonyService explorationColonyService;
    private final AgentKnowledgeService agentKnowledgeService;
    private final ToolforgeService toolforgeService;
    private final AgentBrainRuntimeService agentBrainRuntimeService;
    private final AgentSpatialMapService agentSpatialMapService;
    private final SocialGraphService socialGraphService;
    private final CultureArtService cultureArtService;
    private final WildlifeEcosystemService wildlifeEcosystemService;
    private final DayNightCycleService dayNightCycleService;
    private final CivilizationChronicleService civilizationChronicleService;
    private final PluginSdkService pluginSdkService;
    private final TourismPilgrimageService tourismPilgrimageService;
    private final MonetaryEconomyService monetaryEconomyService;

    private final AtomicInteger tickCounter = new AtomicInteger(0);
    private final AtomicReference<String> lastDecision = new AtomicReference<>("Initializing Civilization Cortex...");
    private final AtomicInteger activeRulesCount = new AtomicInteger(0);
    private final AtomicReference<LocalDateTime> lastTickTime = new AtomicReference<>(LocalDateTime.now());
    private final List<String> monitoredCategories = new ArrayList<>();
    private final LinkedList<String> decisionHistory = new LinkedList<>();

    @org.springframework.beans.factory.annotation.Autowired
    public SimulationEngineService(RuleService ruleService,
                                   BalanceService balanceService,
                                   ObjectMapper objectMapper,
                                   UniverseService universeService,
                                   PhysicsEngineService physicsEngineService,
                                   AgentMetabolismService agentMetabolismService,
                                   TerritoryControlService territoryControlService,
                                   LawExecutionEngine lawExecutionEngine,
                                   DiplomacyEngineService diplomacyEngineService,
                                   AgentMortalityService agentMortalityService,
                                   ImmigrationService immigrationService,
                                   HealthDiseaseService healthDiseaseService,
                                   ClimateDisasterService climateDisasterService,
                                   ComplexGoodsProductionService complexGoodsProductionService,
                                   SocietalEvolutionService societalEvolutionService,
                                   ResourceStewardshipService resourceStewardshipService,
                                   ExplorationColonyService explorationColonyService,
                                   AgentKnowledgeService agentKnowledgeService,
                                   ToolforgeService toolforgeService,
                                   AgentBrainRuntimeService agentBrainRuntimeService,
                                   AgentSpatialMapService agentSpatialMapService,
                                   SocialGraphService socialGraphService,
                                   CultureArtService cultureArtService,
                                   WildlifeEcosystemService wildlifeEcosystemService,
                                   DayNightCycleService dayNightCycleService,
                                   CivilizationChronicleService civilizationChronicleService,
                                   PluginSdkService pluginSdkService,
                                   TourismPilgrimageService tourismPilgrimageService,
                                   MonetaryEconomyService monetaryEconomyService) {
        this.ruleService = ruleService;
        this.balanceService = balanceService;
        this.objectMapper = objectMapper;
        this.universeService = universeService;
        this.physicsEngineService = physicsEngineService;
        this.agentMetabolismService = agentMetabolismService;
        this.territoryControlService = territoryControlService;
        this.lawExecutionEngine = lawExecutionEngine;
        this.diplomacyEngineService = diplomacyEngineService;
        this.agentMortalityService = agentMortalityService;
        this.immigrationService = immigrationService;
        this.healthDiseaseService = healthDiseaseService;
        this.climateDisasterService = climateDisasterService;
        this.complexGoodsProductionService = complexGoodsProductionService;
        this.societalEvolutionService = societalEvolutionService;
        this.resourceStewardshipService = resourceStewardshipService;
        this.explorationColonyService = explorationColonyService;
        this.agentKnowledgeService = agentKnowledgeService;
        this.toolforgeService = toolforgeService;
        this.agentBrainRuntimeService = agentBrainRuntimeService;
        this.agentSpatialMapService = agentSpatialMapService;
        this.socialGraphService = socialGraphService;
        this.cultureArtService = cultureArtService;
        this.wildlifeEcosystemService = wildlifeEcosystemService;
        this.dayNightCycleService = dayNightCycleService;
        this.civilizationChronicleService = civilizationChronicleService;
        this.pluginSdkService = pluginSdkService;
        this.tourismPilgrimageService = tourismPilgrimageService;
        this.monetaryEconomyService = monetaryEconomyService;
    }

    public SimulationEngineService(RuleService ruleService,
                                   BalanceService balanceService,
                                   ObjectMapper objectMapper,
                                   UniverseService universeService,
                                   PhysicsEngineService physicsEngineService,
                                   AgentMetabolismService agentMetabolismService,
                                   TerritoryControlService territoryControlService,
                                   LawExecutionEngine lawExecutionEngine) {
        this(ruleService, balanceService, objectMapper, universeService, physicsEngineService,
             agentMetabolismService, territoryControlService, lawExecutionEngine,
             null, null, null, null, null, null, null, null, null,
             null, null, null, null, null, null, null, null, null, null, null, null);
    }

    public UniverseService getUniverseService() {
        return universeService;
    }

    public PhysicsEngineService getPhysicsEngineService() {
        return physicsEngineService;
    }

    public AgentMetabolismService getAgentMetabolismService() {
        return agentMetabolismService;
    }

    public TerritoryControlService getTerritoryControlService() {
        return territoryControlService;
    }

    public LawExecutionEngine getLawExecutionEngine() {
        return lawExecutionEngine;
    }

    public DiplomacyEngineService getDiplomacyEngineService() {
        return diplomacyEngineService;
    }

    public AgentMortalityService getAgentMortalityService() {
        return agentMortalityService;
    }

    public ImmigrationService getImmigrationService() {
        return immigrationService;
    }

    public HealthDiseaseService getHealthDiseaseService() {
        return healthDiseaseService;
    }

    public ClimateDisasterService getClimateDisasterService() {
        return climateDisasterService;
    }

    public ComplexGoodsProductionService getComplexGoodsProductionService() {
        return complexGoodsProductionService;
    }

    public SocietalEvolutionService getSocietalEvolutionService() {
        return societalEvolutionService;
    }

    public ResourceStewardshipService getResourceStewardshipService() {
        return resourceStewardshipService;
    }

    public ExplorationColonyService getExplorationColonyService() {
        return explorationColonyService;
    }

    public AgentKnowledgeService getAgentKnowledgeService() {
        return agentKnowledgeService;
    }

    public ToolforgeService getToolforgeService() {
        return toolforgeService;
    }

    public AgentBrainRuntimeService getAgentBrainRuntimeService() {
        return agentBrainRuntimeService;
    }

    public AgentSpatialMapService getAgentSpatialMapService() {
        return agentSpatialMapService;
    }

    public SocialGraphService getSocialGraphService() {
        return socialGraphService;
    }

    public CultureArtService getCultureArtService() {
        return cultureArtService;
    }

    public WildlifeEcosystemService getWildlifeEcosystemService() {
        return wildlifeEcosystemService;
    }

    public DayNightCycleService getDayNightCycleService() {
        return dayNightCycleService;
    }

    public CivilizationChronicleService getCivilizationChronicleService() {
        return civilizationChronicleService;
    }

    public PluginSdkService getPluginSdkService() {
        return pluginSdkService;
    }

    public TourismPilgrimageService getTourismPilgrimageService() {
        return tourismPilgrimageService;
    }

    public MonetaryEconomyService getMonetaryEconomyService() {
        return monetaryEconomyService;
    }

    @Scheduled(fixedRate = 15000)
    public void runSimulationCycle() {
        int tick = tickCounter.incrementAndGet();
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        log.info("[CORTEX TICK {}] Simulation cycle starting...", tick);

        lastTickTime.set(LocalDateTime.now());

        List<Rule> rules = ruleService.getValidatedRules();
        if (rules == null || rules.isEmpty()) {
            log.info("[CORTEX TICK {}] No validated rules to evaluate.", tick);
            return;
        }
        activeRulesCount.set(rules.size());

        List<BalanceDTO> balance = balanceService.getBalanceReport();

        synchronized (monitoredCategories) {
            monitoredCategories.clear();
            if (balance != null) {
                balance.forEach(b -> monitoredCategories.add(b.getCategory()));
            }
        }

        // Execute core engines during tick
        if (lawExecutionEngine != null) {
            lawExecutionEngine.evaluateAndApplyLaws(rules, balance);
        }
        if (agentMetabolismService != null) {
            agentMetabolismService.processMetabolism(100, 100.0, 150.0);
        }
        if (territoryControlService != null) {
            territoryControlService.processTerritoryTick(1.0);
        }
        if (diplomacyEngineService != null) {
            diplomacyEngineService.processDiplomacyCycle();
        }
        if (agentMortalityService != null) {
            agentMortalityService.processLifecycleTick(100, 0.02, 0.01);
        }
        if (immigrationService != null) {
            immigrationService.processMigrationCycle(75.0, 50.0, 100);
        }
        if (healthDiseaseService != null) {
            healthDiseaseService.processHealthTick(100, 45.0, 50.0, 2);
        }
        if (climateDisasterService != null) {
            climateDisasterService.processClimateCycle(22.0);
        }
        if (complexGoodsProductionService != null) {
            complexGoodsProductionService.processProductionCycle(Map.of(
                "IRON_ORE", 10.0,
                "COAL", 5.0,
                "SILICON", 2.0,
                "COPPER", 2.0
            ));
        }
        if (societalEvolutionService != null) {
            societalEvolutionService.processEvolutionCycle(5, 120.0);
        }
        if (resourceStewardshipService != null) {
            resourceStewardshipService.processStewardshipTick(50.0, 20.0, 60.0, 100);
        }
        if (explorationColonyService != null) {
            explorationColonyService.processExplorationTick(10.0);
        }
        if (agentKnowledgeService != null) {
            agentKnowledgeService.processKnowledgeTick();
        }
        if (toolforgeService != null) {
            toolforgeService.processToolforgeTick();
        }
        if (agentBrainRuntimeService != null) {
            agentBrainRuntimeService.processBrainRuntimeTick();
        }
        if (agentSpatialMapService != null) {
            agentSpatialMapService.processSpatialTick();
        }
        if (socialGraphService != null) {
            socialGraphService.processSocialGraphTick();
        }
        if (cultureArtService != null) {
            cultureArtService.processCultureTick();
        }
        if (wildlifeEcosystemService != null) {
            wildlifeEcosystemService.processEcosystemTick();
        }
        if (dayNightCycleService != null) {
            dayNightCycleService.processDayNightTick();
        }
        if (civilizationChronicleService != null) {
            civilizationChronicleService.processChronicleTick();
        }
        if (pluginSdkService != null) {
            pluginSdkService.processPluginTick();
        }
        if (tourismPilgrimageService != null) {
            tourismPilgrimageService.processTourismTick();
        }
        if (monetaryEconomyService != null) {
            monetaryEconomyService.processMonetaryTick();
        }

        for (Rule rule : rules) {
            try {
                JsonNode logic = objectMapper.readTree(rule.getLogicCode());
                JsonNode typeNode = logic.get("type");
                if (typeNode == null) continue;

                String type = typeNode.asText();

                if ("RESERVE_CHECK".equals(type)) {
                    JsonNode metricNode = logic.get("metric");
                    if (metricNode == null) continue;

                    String metricCat = metricNode.asText();
                    if (balance != null) {
                        balance.stream()
                            .filter(b -> metricCat.equals(b.getCategory()))
                            .findFirst()
                            .ifPresent(b -> {
                                double percentage = b.getPercentageMet();
                                if (percentage < 100) {
                                    String decision = String.format("[%s] DECISION: %s deficiency detected (%.1f%%). Rule '%s' fired.",
                                        timestamp, metricCat, percentage, rule.getTitle());
                                    pushDecision(decision);
                                    log.info(decision);
                                }
                            });
                    }
                } else if ("THRESHOLD_TRIGGER".equals(type)) {
                    String decision = String.format("[%s] AUDIT: Rule '%s' evaluation complete. No threshold breached.",
                        timestamp, rule.getTitle());
                    pushDecision(decision);
                    log.info(decision);
                }
            } catch (Exception e) {
                log.error("Error evaluating rule {}: {}", rule.getId(), e.getMessage());
            }
        }

        log.info("[CORTEX TICK {}] Cycle complete. {} rules evaluated.", tick, rules.size());
    }

    public LocalDateTime getLastTickTime() {
        return lastTickTime.get();
    }

    @EventListener
    public void onBiosphereCritical(BiosphereCriticalEvent event) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        String decision = String.format("[%s] AUTO-REACTION: Biosphere Redline! Target: %s. Value: %.2f. Emergency protocols activated.",
            timestamp, event.getMetric().getName(), event.getMetric().getValue());
        pushDecision(decision);
        log.warn(decision);
    }

    private synchronized void pushDecision(String decision) {
        lastDecision.set(decision);
        decisionHistory.addFirst(decision);
        if (decisionHistory.size() > MAX_DECISION_HISTORY) {
            decisionHistory.removeLast();
        }
    }

    public SimulationStatusResponse getStatus() {
        List<String> categories;
        synchronized (monitoredCategories) {
            categories = new ArrayList<>(monitoredCategories);
        }
        List<String> history;
        synchronized (this) {
            history = new ArrayList<>(decisionHistory);
        }
        return new SimulationStatusResponse(
            "Cortex Simulation Engine (Java Native)",
            activeRulesCount.get(),
            lastDecision.get(),
            categories,
            tickCounter.get(),
            history
        );
    }
}
