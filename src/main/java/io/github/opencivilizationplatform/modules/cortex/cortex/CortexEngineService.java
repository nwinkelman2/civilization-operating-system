package io.github.opencivilizationplatform.modules.cortex.cortex;

import io.github.opencivilizationplatform.core.event.BiosphereCriticalEvent;
import io.github.opencivilizationplatform.modules.civilization.domain.Civilization;
import io.github.opencivilizationplatform.modules.civilization.infrastructure.CivilizationRepository;
import io.github.opencivilizationplatform.modules.cortex.domain.ResourceTick;
import io.github.opencivilizationplatform.modules.monitoring.domain.BiosphereMetric;
import io.github.opencivilizationplatform.modules.monitoring.domain.BiosphereMetricStatus;
import io.github.opencivilizationplatform.modules.monitoring.infrastructure.BiosphereMetricRepository;
import io.github.opencivilizationplatform.modules.region.domain.ResourceRegion;
import io.github.opencivilizationplatform.modules.region.infrastructure.ResourceRegionRepository;
import io.github.opencivilizationplatform.modules.participation.domain.Rule;
import io.github.opencivilizationplatform.modules.participation.domain.RuleStatus;
import io.github.opencivilizationplatform.modules.participation.infrastructure.RuleRepository;
import io.github.opencivilizationplatform.modules.social.domain.Incident;
import io.github.opencivilizationplatform.modules.social.domain.IncidentStatus;
import io.github.opencivilizationplatform.modules.social.infrastructure.IncidentRepository;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;

import java.util.List;
import java.util.Random;
import java.util.ArrayList;

@Service
public class CortexEngineService {

    private static final Logger log = LoggerFactory.getLogger(CortexEngineService.class);

    private final CivilizationRepository civilizationRepository;
    private final ResourceRegionRepository resourceRegionRepository;
    private final RuleRepository ruleRepository;
    private final ObjectMapper objectMapper;
    private final io.github.opencivilizationplatform.modules.nexus.infrastructure.MeshTradeRepository meshTradeRepository;
    private final BiosphereMetricRepository biosphereMetricRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final io.github.opencivilizationplatform.modules.technology.infrastructure.TechnologyRepository technologyRepository;
    private final IncidentRepository incidentRepository;
    private final io.github.opencivilizationplatform.modules.events.application.GlobalEventService globalEventService;
    private final io.github.opencivilizationplatform.modules.nexus.application.TreatyService treatyService;
    private final io.github.opencivilizationplatform.modules.nexus.application.ElectionService electionService;
    private final Random random = new Random();
    private int tickCount = 0;

    public CortexEngineService(CivilizationRepository civilizationRepository,
                               ResourceRegionRepository resourceRegionRepository,
                               RuleRepository ruleRepository,
                               ObjectMapper objectMapper,
                               io.github.opencivilizationplatform.modules.nexus.infrastructure.MeshTradeRepository meshTradeRepository,
                               BiosphereMetricRepository biosphereMetricRepository,
                               ApplicationEventPublisher eventPublisher,
                               io.github.opencivilizationplatform.modules.technology.infrastructure.TechnologyRepository technologyRepository,
                               IncidentRepository incidentRepository,
                               io.github.opencivilizationplatform.modules.events.application.GlobalEventService globalEventService,
                               io.github.opencivilizationplatform.modules.nexus.application.TreatyService treatyService,
                               io.github.opencivilizationplatform.modules.nexus.application.ElectionService electionService) {
        this.civilizationRepository = civilizationRepository;
        this.resourceRegionRepository = resourceRegionRepository;
        this.ruleRepository = ruleRepository;
        this.objectMapper = objectMapper;
        this.meshTradeRepository = meshTradeRepository;
        this.biosphereMetricRepository = biosphereMetricRepository;
        this.eventPublisher = eventPublisher;
        this.technologyRepository = technologyRepository;
        this.incidentRepository = incidentRepository;
        this.globalEventService = globalEventService;
        this.treatyService = treatyService;
        this.electionService = electionService;
    }

    @Scheduled(fixedRateString = "${cortex.engine.tick-rate-ms:30000}")
    @Transactional
    @SchedulerLock(name = "cortexEngineTick", lockAtMostFor = "25s", lockAtLeastFor = "10s")
    public void tick() {
        tickCount++;
        List<Civilization> civilizations = civilizationRepository.findAll();
        if (civilizations.isEmpty()) return;

        // Every 20 ticks: maybe spawn a new global event
        if (tickCount % 20 == 0) {
            List<Long> civIds = civilizations.stream().map(Civilization::getId).toList();
            globalEventService.maybeGenerateEvent(civIds);
        }
        // Tick down active global events
        globalEventService.tickEvents();

        // Every 50 ticks: open elections in all civs that don't have one open
        if (tickCount % 50 == 0) {
            for (Civilization civ : civilizations) {
                electionService.openElection(civ.getId());
            }
        }
        // Tick down open elections
        electionService.tickElections();

        for (Civilization civ : civilizations) {
            ResourceTick tick = computeTick(civ);
            applyTick(civ, tick);
        }

        civilizationRepository.saveAll(civilizations);
        log.debug("Cortex tick completed for {} civilizations", civilizations.size());
    }

    @Transactional
    public void tickForCivilization(Long civilizationId) {
        civilizationRepository.findById(civilizationId).ifPresent(civ -> {
            ResourceTick tick = computeTick(civ);
            applyTick(civ, tick);
            civilizationRepository.save(civ);
        });
    }

    private ResourceTick computeTick(Civilization civ) {
        List<String> cortexLogs = new ArrayList<>();
        double[] resourceDelta = new double[]{0, 0, 0, 0, 0};
        double populationDelta = 0, reputationDelta = 0;

        // 1. Carregar regras de governança ativas
        List<Rule> activeRules = ruleRepository.findByCivilizationId(civ.getId()).stream()
            .filter(r -> r.getStatus() == RuleStatus.ACTIVE)
            .toList();

        boolean isBirthControlActive = activeRules.stream().anyMatch(r -> r.getLogicCode().contains("LIMIT_BIRTHS"));
        boolean isAgriPushActive = activeRules.stream().anyMatch(r -> r.getLogicCode().contains("BOOST_AGRI"));
        boolean isRobotsActive = activeRules.stream().anyMatch(r -> r.getLogicCode().contains("OPERATE_ROBOTS"));
        boolean isAutonomousTradeActive = activeRules.stream().anyMatch(r -> r.getLogicCode().contains("AUTONOMOUS_TRADE"));
        boolean isRestrictConsumptionActive = activeRules.stream().anyMatch(r -> r.getLogicCode().contains("RESTRICT_CONSUMPTION"));
        boolean isBoostProductionActive = activeRules.stream().anyMatch(r -> r.getLogicCode().contains("BOOST_PRODUCTION"));

        // 1.a Treaty modifiers: [scienceBonus, tradeMult, repDelta, scienceBotMult]
        double[] treatyMods = treatyService.computeTreatyModifiers(civ.getId());
        double treatyScienceBonus = treatyMods[0];
        double treatyTradeMult = treatyMods[1];
        double treatyRepDelta = treatyMods[2];
        double treatyScienceBotMult = treatyMods[3];
        if (treatyRepDelta > 0) {
            reputationDelta += treatyRepDelta;
            cortexLogs.add("[Tratado] Bônus de reputação de tratados ativos: +" + String.format("%.1f", treatyRepDelta) + ".");
        }

        // 1.b Global event modifiers: [foodMult, waterMult, popGrowthPenalty, tradeMult, repDelta, scienceBonus]
        double[] eventMods = new double[]{0, 0, 0, 0, 0, 0}; // additive deltas
        boolean robotsOffline = false;
        List<io.github.opencivilizationplatform.modules.events.domain.GlobalEvent> activeEvents = globalEventService.getActiveEvents();
        for (io.github.opencivilizationplatform.modules.events.domain.GlobalEvent evt : activeEvents) {
            if (globalEventService.isAffected(evt, civ.getId())) {
                globalEventService.applyEventEffects(evt, civ, resourceDelta, eventMods);
                cortexLogs.add("[🌍 Evento Global] " + evt.getType() + " ativo: " + evt.getDescription());
                if (evt.getType() == io.github.opencivilizationplatform.modules.events.domain.GlobalEventType.SOLAR_STORM) {
                    robotsOffline = true;
                }
            }
        }
        reputationDelta += eventMods[4];
        double globalTradeMult = 1.0 + eventMods[3];


        // 1.1 Carregar incidentes ativos e mitigar via robôs designados
        List<Incident> activeIncidents = incidentRepository.findByCivilizationId(civ.getId()).stream()
            .filter(i -> !IncidentStatus.RESOLVED.equals(i.getStatus()))
            .toList();

        int totalAssignedEco = 0;
        int totalAssignedSecurity = 0;
        double productivityMultiplier = 1.0;

        for (Incident inc : activeIncidents) {
            int ecoAssigned = inc.getAssignedEcoBots() != null ? inc.getAssignedEcoBots() : 0;
            int secAssigned = inc.getAssignedSecurityBots() != null ? inc.getAssignedSecurityBots() : 0;
            
            totalAssignedEco += ecoAssigned;
            totalAssignedSecurity += secAssigned;

            if (ecoAssigned > 0 || secAssigned > 0) {
                double reduction = (ecoAssigned * 10.0) + (secAssigned * 15.0);
                double currentSeverity = inc.getSeverity() == null ? 100.0 : inc.getSeverity();
                double nextSeverity = Math.max(0.0, currentSeverity - reduction);
                inc.setSeverity(nextSeverity);

                cortexLogs.add("[Incident Board] Mitigação ativa em " + inc.getType() + " no setor " + inc.getLocation() + ": severidade reduzida para " + String.format("%.1f", nextSeverity) + "%.");

                if (nextSeverity <= 0.0) {
                    inc.setStatus(IncidentStatus.RESOLVED);
                    inc.setAssignedEcoBots(0);
                    inc.setAssignedSecurityBots(0);
                    reputationDelta += 15.0; // Ganho de reputação por solução de incidentes
                    cortexLogs.add("[⚖️ Incident Board] RESOLVIDO: O incidente " + inc.getType() + " foi mitigado com sucesso pelos drones! Reputação +15.");
                }
                incidentRepository.save(inc);
            }

            if (!IncidentStatus.RESOLVED.equals(inc.getStatus())) {
                reputationDelta -= 1.0; // Penalidade contínua de reputação
                productivityMultiplier -= 0.10; // -10% de eficiência geral por incidente ativo
            }
        }

        if (productivityMultiplier < 0.50) {
            productivityMultiplier = 0.50; // Limite máximo de penalidade de 50%
        }

        // 2. Parsear histórico anterior para ler contagem de robôs
        ArrayNode historyArray;
        try {
            if (civ.getResourceHistory() == null || civ.getResourceHistory().isBlank() || civ.getResourceHistory().equals("[]")) {
                historyArray = objectMapper.createArrayNode();
            } else {
                historyArray = (ArrayNode) objectMapper.readTree(civ.getResourceHistory());
            }
        } catch (Exception e) {
            historyArray = objectMapper.createArrayNode();
        }

        int agriBots = 0;
        int aquaBots = 0;
        int exploreBots = 0;
        int utilityBots = 0;
        int ecoBots = 0;
        int scienceBots = 0;
        int securityBots = 0;

        if (historyArray.size() > 0) {
            JsonNode lastTick = historyArray.get(historyArray.size() - 1);
            if (lastTick.has("agriBots")) agriBots = lastTick.get("agriBots").asInt();
            if (lastTick.has("aquaBots")) aquaBots = lastTick.get("aquaBots").asInt();
            if (lastTick.has("exploreBots")) exploreBots = lastTick.get("exploreBots").asInt();
            if (lastTick.has("utilityBots")) utilityBots = lastTick.get("utilityBots").asInt();
            if (lastTick.has("ecoBots")) ecoBots = lastTick.get("ecoBots").asInt();
            if (lastTick.has("scienceBots")) scienceBots = lastTick.get("scienceBots").asInt();
            if (lastTick.has("securityBots")) securityBots = lastTick.get("securityBots").asInt();
        }

        // 3. Processar Automação Robótica pelo Cortex local
        double robotFoodBonus = 0;
        double robotWaterBonus = 0;
        double robotMineralBonus = 0;
        double robotHousingBonus = 0;

        if (isRobotsActive && !robotsOffline) {
            int totalRobots = agriBots + aquaBots + exploreBots + utilityBots + ecoBots + scienceBots + securityBots;
            int pop = civ.getPopulation() != null ? civ.getPopulation() : 100;
            int maxRobots = 1 + (pop / 50);
            if (maxRobots > 15) maxRobots = 15;

            double currentMinerals = civ.getMinerals() != null ? civ.getMinerals() : 0;
            double currentEnergy = civ.getEnergy() != null ? civ.getEnergy() : 0;

            // Decidir se fabrica um novo robô
            if (totalRobots < maxRobots && currentMinerals >= 15.0 && currentEnergy >= 10.0) {
                civ.setMinerals(currentMinerals - 15.0);
                civ.setEnergy(currentEnergy - 10.0);
                currentMinerals -= 15.0;
                currentEnergy -= 10.0;

                int ecoPriority = isBotTechUnlocked(civ, "ECO") ? (civ.getEcoBotsPriority() != null ? civ.getEcoBotsPriority() : 0) : 0;
                int sciencePriority = isBotTechUnlocked(civ, "SCIENCE") ? (civ.getScienceBotsPriority() != null ? civ.getScienceBotsPriority() : 0) : 0;
                int securityPriority = isBotTechUnlocked(civ, "SECURITY") ? (civ.getSecurityBotsPriority() != null ? civ.getSecurityBotsPriority() : 0) : 0;

                int sum = (civ.getAgriBotsPriority() != null ? civ.getAgriBotsPriority() : 25)
                        + (civ.getAquaBotsPriority() != null ? civ.getAquaBotsPriority() : 25)
                        + (civ.getExploreBotsPriority() != null ? civ.getExploreBotsPriority() : 25)
                        + (civ.getUtilityBotsPriority() != null ? civ.getUtilityBotsPriority() : 25)
                        + ecoPriority
                        + sciencePriority
                        + securityPriority;
                if (sum == 0) sum = 100;

                double targetAgri = (double) (civ.getAgriBotsPriority() != null ? civ.getAgriBotsPriority() : 25) / sum;
                double targetAqua = (double) (civ.getAquaBotsPriority() != null ? civ.getAquaBotsPriority() : 25) / sum;
                double targetExplore = (double) (civ.getExploreBotsPriority() != null ? civ.getExploreBotsPriority() : 25) / sum;
                double targetUtility = (double) (civ.getUtilityBotsPriority() != null ? civ.getUtilityBotsPriority() : 25) / sum;
                double targetEco = (double) ecoPriority / sum;
                double targetScience = (double) sciencePriority / sum;
                double targetSecurity = (double) securityPriority / sum;

                String botType;
                if (totalRobots == 0) {
                    if (targetAgri >= targetAqua && targetAgri >= targetExplore && targetAgri >= targetUtility && targetAgri >= targetEco && targetAgri >= targetScience && targetAgri >= targetSecurity) {
                        agriBots++;
                        botType = "Agri-Bot (Agropecuária)";
                    } else if (targetAqua >= targetExplore && targetAqua >= targetUtility && targetAqua >= targetEco && targetAqua >= targetScience && targetAqua >= targetSecurity) {
                        aquaBots++;
                        botType = "Aqua-Bot (Recursos Hídricos)";
                    } else if (targetExplore >= targetUtility && targetExplore >= targetEco && targetExplore >= targetScience && targetExplore >= targetSecurity) {
                        exploreBots++;
                        botType = "Explorer-Bot (Exploração Mineral)";
                    } else if (targetUtility >= targetEco && targetUtility >= targetScience && targetUtility >= targetSecurity) {
                        utilityBots++;
                        botType = "Utility-Bot (Infraestrutura/Moradia)";
                    } else if (targetEco >= targetScience && targetEco >= targetSecurity) {
                        ecoBots++;
                        botType = "Eco-Bot (Preservação Ambiental)";
                    } else if (targetScience >= targetSecurity) {
                        scienceBots++;
                        botType = "Science-Bot (Pesquisa Científica)";
                    } else {
                        securityBots++;
                        botType = "Security-Bot (Seguridade/Estabilidade)";
                    }
                } else {
                    double currentAgriRatio = (double) agriBots / totalRobots;
                    double currentAquaRatio = (double) aquaBots / totalRobots;
                    double currentExploreRatio = (double) exploreBots / totalRobots;
                    double currentUtilityRatio = (double) utilityBots / totalRobots;
                    double currentEcoRatio = (double) ecoBots / totalRobots;
                    double currentScienceRatio = (double) scienceBots / totalRobots;
                    double currentSecurityRatio = (double) securityBots / totalRobots;

                    double diffAgri = targetAgri - currentAgriRatio;
                    double diffAqua = targetAqua - currentAquaRatio;
                    double diffExplore = targetExplore - currentExploreRatio;
                    double diffUtility = targetUtility - currentUtilityRatio;
                    double diffEco = targetEco - currentEcoRatio;
                    double diffScience = targetScience - currentScienceRatio;
                    double diffSecurity = targetSecurity - currentSecurityRatio;

                    double maxDiff = diffAgri;
                    botType = "Agri-Bot (Agropecuária)";

                    if (diffAqua > maxDiff) {
                        maxDiff = diffAqua;
                        botType = "Aqua-Bot (Recursos Hídricos)";
                    }
                    if (diffExplore > maxDiff) {
                        maxDiff = diffExplore;
                        botType = "Explorer-Bot (Exploração Mineral)";
                    }
                    if (diffUtility > maxDiff) {
                        maxDiff = diffUtility;
                        botType = "Utility-Bot (Infraestrutura/Moradia)";
                    }
                    if (diffEco > maxDiff) {
                        maxDiff = diffEco;
                        botType = "Eco-Bot (Preservação Ambiental)";
                    }
                    if (diffScience > maxDiff) {
                        maxDiff = diffScience;
                        botType = "Science-Bot (Pesquisa Científica)";
                    }
                    if (diffSecurity > maxDiff) {
                        maxDiff = diffSecurity;
                        botType = "Security-Bot (Seguridade/Estabilidade)";
                    }

                    if (botType.contains("Agri")) agriBots++;
                    else if (botType.contains("Aqua")) aquaBots++;
                    else if (botType.contains("Explorer")) exploreBots++;
                    else if (botType.contains("Utility")) utilityBots++;
                    else if (botType.contains("Eco")) ecoBots++;
                    else if (botType.contains("Science")) scienceBots++;
                    else if (botType.contains("Security")) securityBots++;
                }
                totalRobots++;
                cortexLogs.add("[Automação] Cortex fabricou 1 " + botType + " (Custo: 15 minerais, 10 energia).");
            }

            // Operar robôs (consomem 0.15 energia cada)
            if (totalRobots > 0) {
                double energyRequired = totalRobots * 0.15;
                if (currentEnergy >= energyRequired) {
                    civ.setEnergy(currentEnergy - energyRequired);

                    int activeAgriBots = agriBots;
                    int activeAquaBots = aquaBots;
                    int activeExploreBots = exploreBots;
                    int activeUtilityBots = utilityBots;
                    int activeEcoBots = Math.max(0, ecoBots - totalAssignedEco);
                    int activeSecurityBots = Math.max(0, securityBots - totalAssignedSecurity);

                    double ruleProdMult = 1.0;
                    if (isBoostProductionActive) {
                        ruleProdMult = 1.15;
                    } else if (isRestrictConsumptionActive) {
                        ruleProdMult = 0.90;
                    }

                    robotFoodBonus = activeAgriBots * 0.8 * productivityMultiplier * ruleProdMult;
                    robotWaterBonus = activeAquaBots * 0.6 * productivityMultiplier * ruleProdMult;
                    robotMineralBonus = activeExploreBots * 0.5 * productivityMultiplier * ruleProdMult;
                    robotHousingBonus = activeUtilityBots * 0.4 * productivityMultiplier * ruleProdMult;

                    cortexLogs.add("[Automação] Sistemas Robóticos ONLINE: " + totalRobots + " drones (Ativos em produção: " + 
                        (activeAgriBots+activeAquaBots+activeExploreBots+activeUtilityBots+activeEcoBots+activeSecurityBots+scienceBots) + 
                        ", Mitigando incidentes: " + (totalAssignedEco+totalAssignedSecurity) + 
                        "). Consumo: " + String.format("%.2f", energyRequired) + " energia.");

                    // --- IMPACTO ECOLÓGICO: Drift de qualidade do ar por atividade robótica ---
                    double industrialDrift = (exploreBots + utilityBots) * 0.02;
                    double ecoRecovery = activeEcoBots * 0.04;
                    double netEcoImpact = industrialDrift - ecoRecovery;

                    if (netEcoImpact != 0) {
                        List<BiosphereMetric> metrics = biosphereMetricRepository.findAll();
                        for (BiosphereMetric metric : metrics) {
                            if (metric.getName() != null && metric.getName().contains("Qualidade do Ar")) {
                                double newValue = Math.max(0, Math.min(100.0, metric.getValue() - netEcoImpact));
                                metric.setValue(newValue);
                                if (newValue < metric.getSafetyLimit()) {
                                    metric.setStatus(BiosphereMetricStatus.CRITICAL);
                                    biosphereMetricRepository.save(metric);
                                    eventPublisher.publishEvent(new BiosphereCriticalEvent(this, metric));
                                    reputationDelta -= 3.0;
                                    cortexLogs.add("[⚠ Alerta Ecológico] Qualidade do Ar CRÍTICA (" + String.format("%.1f", newValue) + "%) — Poluição industrial superou recuperação dos Eco-Bots. Reputação -3.");
                                } else {
                                    metric.setStatus(BiosphereMetricStatus.NORMAL);
                                    biosphereMetricRepository.save(metric);
                                    cortexLogs.add("[Biosfera] Qualidade do Ar: " + String.format("%.1f", newValue) + "% (Net Impacto: " + String.format("%.3f", -netEcoImpact) + ")");
                                }
                                break;
                            }
                        }
                    }

                    // --- AUTOMAÇÃO CIENTÍFICA: Science-Bots ---
                    if (scienceBots > 0 && technologyRepository != null) {
                        List<io.github.opencivilizationplatform.modules.technology.domain.Technology> researching = 
                            technologyRepository.findByCivilizationIdAndStatus(civ.getId(), io.github.opencivilizationplatform.modules.technology.domain.TechnologyStatus.RESEARCHING);
                        if (!researching.isEmpty()) {
                            for (io.github.opencivilizationplatform.modules.technology.domain.Technology tech : researching) {
                                int progressToAdd = (int)(scienceBots * 2 * treatyScienceBotMult);
                                tech.setResearchProgress(tech.getResearchProgress() + progressToAdd);
                                cortexLogs.add("[Pesquisa] Science-Bots injetaram +" + progressToAdd + " de progresso na tecnologia '" + tech.getName() + "'" + (treatyScienceBotMult > 1.0 ? " (Aliança de Pesquisa ×" + String.format("%.0f", treatyScienceBotMult) + ")" : "") + ".");
                                if (tech.getResearchProgress() >= tech.getResearchCost()) {
                                    tech.setStatus(io.github.opencivilizationplatform.modules.technology.domain.TechnologyStatus.COMPLETED);
                                    tech.setResearchProgress(tech.getResearchCost());
                                    cortexLogs.add("[Pesquisa] ⚙️ TECNOLOGIA CONCLUÍDA: '" + tech.getName() + "' foi totalmente pesquisada pelo Cortex!");
                                }
                                technologyRepository.save(tech);
                            }
                        } else {
                            cortexLogs.add("[Pesquisa] Science-Bots ociosos: nenhuma tecnologia ativa para pesquisa.");
                        }
                    }

                    // --- SEGURANÇA E ESTABILIDADE: Security-Bots ---
                    if (activeSecurityBots > 0) {
                        double stabilityBoost = activeSecurityBots * 0.4;
                        if (civ.getFood() < 5.0 || civ.getWater() < 5.0) {
                            stabilityBoost *= 0.2; // Reduz a eficácia em 80% sob fome generalizada
                            cortexLogs.add("[Social] Security-Bots ativos: patrulhamento urbano sob escassez (Estabilidade +" + String.format("%.2f", stabilityBoost) + " — Eficácia reduzida devido a fome generalizada).");
                        } else {
                            cortexLogs.add("[Social] Security-Bots ativos: patrulhamento e pacificação urbana (Estabilidade +" + String.format("%.2f", stabilityBoost) + ").");
                        }
                        reputationDelta += stabilityBoost;
                    }
                } else {
                    cortexLogs.add("[Alerta Automação] Sistemas Robóticos OFFLINE: Reserva de energia insuficiente.");
                }
            }
        }

        // 4. Calcular baselines da região
        if (civ.getHomeRegionId() != null) {
            resourceRegionRepository.findById(civ.getHomeRegionId()).ifPresent(region -> {
                resourceDelta[0] += region.getFoodAvailability() * 0.5;
                resourceDelta[1] += region.getWaterAvailability() * 0.5;
                resourceDelta[2] += region.getMineralAvailability() * 0.5;
                resourceDelta[3] += region.getEnergyAvailability() * 0.5;
                resourceDelta[4] += region.getHousingAvailability() * 0.5;
            });
        }

        // Adicionar bônus de produção dos robôs autônomos
        resourceDelta[0] += robotFoodBonus;
        resourceDelta[1] += robotWaterBonus;
        resourceDelta[2] += robotMineralBonus;
        resourceDelta[4] += robotHousingBonus;

        // Consumo dinâmico baseado em custos reais por habitante
        double population = civ.getPopulation() != null ? civ.getPopulation() : 0;
        double foodConsumption = population * 0.10;
        double waterConsumption = population * 0.08;
        double energyConsumption = population * 0.04;

        if (isRestrictConsumptionActive) {
            foodConsumption *= 0.75;
            waterConsumption *= 0.75;
            energyConsumption *= 0.75;
            cortexLogs.add("[Racionamento] Regra de Racionamento Constitucional ativa: consumo geral reduzido em 25%.");
        }

        if (isBoostProductionActive) {
            resourceDelta[3] -= 3.0; // Consumo adicional de energia para sustentar o boost de produção
            cortexLogs.add("[Produção Intensiva] Regra de Produção Intensiva ativa: +15% de rendimento de robôs (Consumo de energia: -3.0).");
        }

        resourceDelta[0] -= foodConsumption;
        resourceDelta[1] -= waterConsumption;
        resourceDelta[3] -= energyConsumption;

        // Ruídos aleatórios de simulação para variação
        resourceDelta[0] += random.nextDouble() * 2 - 1;
        resourceDelta[1] += random.nextDouble() * 1.5 - 0.75;
        resourceDelta[2] += random.nextDouble() * 1 - 0.5;
        resourceDelta[3] += random.nextDouble() * 1.5 - 0.75;
        resourceDelta[4] += random.nextDouble() * 0.5 - 0.25;

        // 5. Natalidade vegetativa e mortes por fome
        double currentFood = civ.getFood() != null ? civ.getFood() : 100;
        double currentWater = civ.getWater() != null ? civ.getWater() : 100;

        if (currentFood > 0 && currentWater > 0) {
            double growth = 0.02 * population + 1.0;
            if (isBirthControlActive && currentFood < 30.0) {
                growth *= 0.25;
                cortexLogs.add("[Remediação Cortex] Birth Control Policy ativo devido a baixa reserva de comida (Natalidade natural -75%).");
            }
            populationDelta = growth;
        } else {
            populationDelta = -0.05 * population - 2.0;
            reputationDelta -= 5.0;
            cortexLogs.add("[Alerta Crítico] FOME E ESCASSEZ EXTREMA: Ocorrendo óbitos e migrações na sociedade.");
        }

        // Regra de Subsídio Agrícola de Emergência
        if (isAgriPushActive && currentFood < 35.0) {
            resourceDelta[0] += 5.0;
            cortexLogs.add("[Remediação Cortex] Emergency Agricultural Push ativo: Subsídio de +5.0 comida injetado.");
        }

        // Reputação baseada em recursos
        if (currentFood <= 5 || currentWater <= 5) {
            reputationDelta -= 2.0;
        } else if (currentFood > 50 && currentWater > 50) {
            reputationDelta += 0.5;
        }

        // 6. Negociação e Escambo Autônomo entre nós Cortex via Rede Mesh Nexus
        if (isAutonomousTradeActive) {
            double regionFoodAvail = 100.0;
            double regionWaterAvail = 100.0;
            double regionMineralAvail = 100.0;
            double regionEnergyAvail = 100.0;

            if (civ.getHomeRegion() != null) {
                ResourceRegion reg = civ.getHomeRegion();
                regionFoodAvail = reg.getFoodAvailability() != null ? reg.getFoodAvailability() : 100.0;
                regionWaterAvail = reg.getWaterAvailability() != null ? reg.getWaterAvailability() : 100.0;
                regionMineralAvail = reg.getMineralAvailability() != null ? reg.getMineralAvailability() : 100.0;
                regionEnergyAvail = reg.getEnergyAvailability() != null ? reg.getEnergyAvailability() : 100.0;
            }

            List<String> deficientResources = new ArrayList<>();
            if (regionFoodAvail < 15.0 || currentFood < 30.0) deficientResources.add("FOOD");
            if (regionWaterAvail < 15.0 || currentWater < 30.0) deficientResources.add("WATER");
            if (regionMineralAvail < 15.0 || civ.getMinerals() < 30.0) deficientResources.add("MINERALS");
            if (regionEnergyAvail < 15.0 || civ.getEnergy() < 30.0) deficientResources.add("ENERGY");

            for (String criticalResource : deficientResources) {
                List<Civilization> allCivs = civilizationRepository.findAll();
                Civilization partner = null;

                for (Civilization potentialPartner : allCivs) {
                    if (potentialPartner.getId().equals(civ.getId())) continue;

                    double partnerAmount = 0.0;
                    double partnerRegionAvail = 100.0;

                    if (criticalResource.equals("FOOD")) {
                        partnerAmount = potentialPartner.getFood() != null ? potentialPartner.getFood() : 0.0;
                        if (potentialPartner.getHomeRegion() != null) partnerRegionAvail = potentialPartner.getHomeRegion().getFoodAvailability() != null ? potentialPartner.getHomeRegion().getFoodAvailability() : 100.0;
                    } else if (criticalResource.equals("WATER")) {
                        partnerAmount = potentialPartner.getWater() != null ? potentialPartner.getWater() : 0.0;
                        if (potentialPartner.getHomeRegion() != null) partnerRegionAvail = potentialPartner.getHomeRegion().getWaterAvailability() != null ? potentialPartner.getHomeRegion().getWaterAvailability() : 100.0;
                    } else if (criticalResource.equals("MINERALS")) {
                        partnerAmount = potentialPartner.getMinerals() != null ? potentialPartner.getMinerals() : 0.0;
                        if (potentialPartner.getHomeRegion() != null) partnerRegionAvail = potentialPartner.getHomeRegion().getMineralAvailability() != null ? potentialPartner.getHomeRegion().getMineralAvailability() : 100.0;
                    } else if (criticalResource.equals("ENERGY")) {
                        partnerAmount = potentialPartner.getEnergy() != null ? potentialPartner.getEnergy() : 0.0;
                        if (potentialPartner.getHomeRegion() != null) partnerRegionAvail = potentialPartner.getHomeRegion().getEnergyAvailability() != null ? potentialPartner.getHomeRegion().getEnergyAvailability() : 100.0;
                    }

                    if (partnerAmount > 80.0 || partnerRegionAvail > 40.0) {
                        boolean partnerTradeActive = ruleRepository.findByCivilizationId(potentialPartner.getId()).stream()
                            .filter(r -> r.getStatus() == RuleStatus.ACTIVE)
                            .anyMatch(r -> r.getLogicCode().contains("AUTONOMOUS_TRADE"));

                        if (partnerTradeActive) {
                            partner = potentialPartner;
                            break;
                        }
                    }
                }

                if (partner != null) {
                    double currentMinerals = civ.getMinerals() != null ? civ.getMinerals() : 0;
                    double currentEnergy = civ.getEnergy() != null ? civ.getEnergy() : 0;
                    double currentFoodVal = civ.getFood() != null ? civ.getFood() : 0;
                    double currentWaterVal = civ.getWater() != null ? civ.getWater() : 0;

                    String offeredResource = null;
                    if (!criticalResource.equals("MINERALS") && currentMinerals > 60.0) offeredResource = "MINERALS";
                    else if (!criticalResource.equals("ENERGY") && currentEnergy > 60.0) offeredResource = "ENERGY";
                    else if (!criticalResource.equals("FOOD") && currentFoodVal > 60.0) offeredResource = "FOOD";
                    else if (!criticalResource.equals("WATER") && currentWaterVal > 60.0) offeredResource = "WATER";

                    if (offeredResource != null) {
                        // Prevenir loop de escambo
                        final Civilization finalPartner = partner;
                        final String finalOfferedResource = offeredResource;
                        boolean isLoop = meshTradeRepository.findAllByOrderByCreatedAtDesc().stream().limit(5).anyMatch(t -> 
                            t.getSender().getId().equals(civ.getId()) &&
                            t.getReceiver().getId().equals(finalPartner.getId()) &&
                            t.getRequestedResource().equals(finalOfferedResource) &&
                            t.getOfferedResource().equals(criticalResource)
                        );
                        if (isLoop) {
                            cortexLogs.add("[Alerta Loop] Cortex cancelou barter de " + criticalResource + " por " + offeredResource + " com '" + partner.getName() + "' (Loop de escambo detectado).");
                            continue;
                        }

                        double tradeQty = 30.0;
                        adjustResourceStock(civ, criticalResource, tradeQty, resourceDelta);
                        adjustResourceStock(partner, criticalResource, -tradeQty, null);
                        adjustResourceStock(civ, offeredResource, -tradeQty, resourceDelta);
                        adjustResourceStock(partner, offeredResource, tradeQty, null);

                        String logMsg = "[Comércio Autônomo] Cortex importou " + tradeQty + " de " + criticalResource + 
                            " da sociedade '" + partner.getName() + "' em troca de " + tradeQty + " de " + offeredResource + ".";
                        cortexLogs.add(logMsg);

                        addLogToPartnerHistory(partner, "[Comércio Autônomo] Cortex exportou " + tradeQty + " de " + criticalResource + 
                            " para '" + civ.getName() + "' em troca de " + tradeQty + " de " + offeredResource + ".");

                        saveMeshTrade(partner, civ, criticalResource, tradeQty, offeredResource, tradeQty, "RESOURCE_BARTER");
                        civilizationRepository.save(partner);
                        break;
                    } else if (population >= 40 && (population - 5) >= 30) {
                        int partnerPop = partner.getPopulation() != null ? partner.getPopulation() : 0;
                        if (partnerPop < 300) {
                            double tradeQty = 35.0;
                            adjustResourceStock(civ, criticalResource, tradeQty, resourceDelta);
                            adjustResourceStock(partner, criticalResource, -tradeQty, null);

                            populationDelta -= 5;
                            partner.setPopulation(partnerPop + 5);

                            String logMsg = "[Comércio Autônomo] Cortex importou " + tradeQty + " de " + criticalResource + 
                                " de '" + partner.getName() + "' transferindo 5 trabalhadores (pessoal/população).";
                            cortexLogs.add(logMsg);

                            addLogToPartnerHistory(partner, "[Comércio Autônomo] Cortex recebeu 5 trabalhadores de '" + civ.getName() + 
                                "' em troca de " + tradeQty + " de " + criticalResource + ".");

                            saveMeshTrade(partner, civ, criticalResource, tradeQty, "PERSONNEL", 5.0, "PERSONNEL_EXCHANGE");
                            civilizationRepository.save(partner);
                            break;
                        }
                    } else {
                        double partnerStock = getResourceStock(partner, criticalResource);
                        if (partnerStock > 100.0) {
                            double tradeQty = 25.0;
                            adjustResourceStock(civ, criticalResource, tradeQty, resourceDelta);
                            adjustResourceStock(partner, criticalResource, -tradeQty, null);

                            String logMsg = "[Auxílio Regional] Cortex importou " + tradeQty + " de " + criticalResource + 
                                " da sociedade '" + partner.getName() + "' sob regime de ajuda humanitária entre nós Cortex.";
                            cortexLogs.add(logMsg);

                            addLogToPartnerHistory(partner, "[Comércio Autônomo] Cortex enviou auxílio regional de " + tradeQty + " de " + criticalResource + 
                                " para '" + civ.getName() + "' (Solicitação autorizada autonomamente).");

                            saveMeshTrade(partner, civ, criticalResource, tradeQty, "NONE", 0.0, "REGIONAL_AID");
                            civilizationRepository.save(partner);
                            break;
                        }
                    }
                } else {
                    cortexLogs.add("[Alerta Comércio] Escassez crítica de " + criticalResource + " detectada. Sem nós Cortex parceiros disponíveis no mesh.");
                }
            }
        }

        // Simular votos de consenso constitucionais
        simulateRuleVoting(civ, cortexLogs);

        return new ResourceTick(
            civ.getId(), resourceDelta[0], resourceDelta[1], resourceDelta[2],
            resourceDelta[3], resourceDelta[4], populationDelta, reputationDelta,
            agriBots, aquaBots, exploreBots, utilityBots, ecoBots, scienceBots, securityBots, cortexLogs
        );
    }

    private void applyTick(Civilization civ, ResourceTick tick) {
        civ.setFood(clamp(civ.getFood() + tick.foodDelta(), 0, 9999));
        civ.setWater(clamp(civ.getWater() + tick.waterDelta(), 0, 9999));
        civ.setMinerals(clamp(civ.getMinerals() + tick.mineralsDelta(), 0, 9999));
        civ.setEnergy(clamp(civ.getEnergy() + tick.energyDelta(), 0, 9999));
        civ.setHousing(clamp(civ.getHousing() + tick.housingDelta(), 0, 9999));
        civ.setPopulation((int) Math.max(0, (civ.getPopulation() != null ? civ.getPopulation() : 10) + tick.populationDelta()));
        civ.setReputationScore(clamp(
            (civ.getReputationScore() != null ? civ.getReputationScore() : 50) + tick.reputationDelta(),
            0, 100
        ));

        // Gravar no histórico de recursos (JSON sliding window de 20 elementos)
        try {
            ArrayNode historyArray;
            if (civ.getResourceHistory() == null || civ.getResourceHistory().isBlank() || civ.getResourceHistory().equals("[]")) {
                historyArray = objectMapper.createArrayNode();
            } else {
                historyArray = (ArrayNode) objectMapper.readTree(civ.getResourceHistory());
            }

            ObjectNode newTick = objectMapper.createObjectNode();
            newTick.put("tick", historyArray.size() + 1);
            newTick.put("pop", civ.getPopulation());
            newTick.put("food", Math.round(civ.getFood() * 100.0) / 100.0);
            newTick.put("water", Math.round(civ.getWater() * 100.0) / 100.0);
            newTick.put("minerals", Math.round(civ.getMinerals() * 100.0) / 100.0);
            newTick.put("energy", Math.round(civ.getEnergy() * 100.0) / 100.0);
            newTick.put("housing", Math.round(civ.getHousing() * 100.0) / 100.0);
            newTick.put("agriBots", tick.agriBots());
            newTick.put("aquaBots", tick.aquaBots());
            newTick.put("exploreBots", tick.exploreBots());
            newTick.put("utilityBots", tick.utilityBots());
            newTick.put("ecoBots", tick.ecoBots());
            newTick.put("scienceBots", tick.scienceBots());
            newTick.put("securityBots", tick.securityBots());

            ArrayNode logsArray = objectMapper.createArrayNode();
            for (String logMsg : tick.logs()) {
                logsArray.add(logMsg);
            }
            newTick.set("logs", logsArray);

            historyArray.add(newTick);

            while (historyArray.size() > 20) {
                historyArray.remove(0);
            }

            civ.setResourceHistory(objectMapper.writeValueAsString(historyArray));
        } catch (Exception e) {
            log.error("Erro ao serializar histórico de recursos: {}", e.getMessage());
        }
    }

    private void addLogToPartnerHistory(Civilization partner, String logMsg) {
        try {
            ArrayNode historyArray;
            if (partner.getResourceHistory() == null || partner.getResourceHistory().isBlank() || partner.getResourceHistory().equals("[]")) {
                historyArray = objectMapper.createArrayNode();
            } else {
                historyArray = (ArrayNode) objectMapper.readTree(partner.getResourceHistory());
            }

            if (historyArray.size() > 0) {
                ObjectNode lastTick = (ObjectNode) historyArray.get(historyArray.size() - 1);
                ArrayNode logs;
                if (lastTick.has("logs")) {
                    logs = (ArrayNode) lastTick.get("logs");
                } else {
                    logs = objectMapper.createArrayNode();
                    lastTick.set("logs", logs);
                }
                logs.add(logMsg);
            } else {
                ObjectNode newTick = objectMapper.createObjectNode();
                newTick.put("tick", 1);
                newTick.put("pop", partner.getPopulation() != null ? partner.getPopulation() : 100);
                newTick.put("food", partner.getFood() != null ? partner.getFood() : 100.0);
                newTick.put("water", partner.getWater() != null ? partner.getWater() : 100.0);
                newTick.put("minerals", partner.getMinerals() != null ? partner.getMinerals() : 50.0);
                newTick.put("energy", partner.getEnergy() != null ? partner.getEnergy() : 75.0);
                newTick.put("housing", partner.getHousing() != null ? partner.getHousing() : 50.0);
                newTick.put("agriBots", 0);
                newTick.put("aquaBots", 0);
                newTick.put("exploreBots", 0);
                newTick.put("utilityBots", 0);
                newTick.put("ecoBots", 0);
                newTick.put("scienceBots", 0);
                newTick.put("securityBots", 0);

                ArrayNode logs = objectMapper.createArrayNode();
                logs.add(logMsg);
                newTick.set("logs", logs);
                historyArray.add(newTick);
            }
            partner.setResourceHistory(objectMapper.writeValueAsString(historyArray));
        } catch (Exception e) {
            log.error("Erro ao adicionar log na civilização parceira: {}", e.getMessage());
        }
    }

    private double getResourceStock(Civilization civ, String resource) {
        if (resource.equals("FOOD")) return civ.getFood() != null ? civ.getFood() : 0.0;
        if (resource.equals("WATER")) return civ.getWater() != null ? civ.getWater() : 0.0;
        if (resource.equals("MINERALS")) return civ.getMinerals() != null ? civ.getMinerals() : 0.0;
        if (resource.equals("ENERGY")) return civ.getEnergy() != null ? civ.getEnergy() : 0.0;
        return 0.0;
    }

    private void adjustResourceStock(Civilization civ, String resource, double amount, double[] delta) {
        if (resource.equals("FOOD")) {
            if (delta != null) delta[0] += amount;
            else civ.setFood(Math.max(0, (civ.getFood() != null ? civ.getFood() : 0.0) + amount));
        } else if (resource.equals("WATER")) {
            if (delta != null) delta[1] += amount;
            else civ.setWater(Math.max(0, (civ.getWater() != null ? civ.getWater() : 0.0) + amount));
        } else if (resource.equals("MINERALS")) {
            if (delta != null) delta[2] += amount;
            else civ.setMinerals(Math.max(0, (civ.getMinerals() != null ? civ.getMinerals() : 0.0) + amount));
        } else if (resource.equals("ENERGY")) {
            if (delta != null) delta[3] += amount;
            else civ.setEnergy(Math.max(0, (civ.getEnergy() != null ? civ.getEnergy() : 0.0) + amount));
        }
    }

    private void saveMeshTrade(Civilization sender, Civilization receiver, String reqRes, double reqAmt, String offRes, double offAmt, String type) {
        io.github.opencivilizationplatform.modules.nexus.domain.MeshTrade trade = new io.github.opencivilizationplatform.modules.nexus.domain.MeshTrade();
        trade.setSender(sender);
        trade.setReceiver(receiver);
        trade.setRequestedResource(reqRes);
        trade.setRequestedAmount(reqAmt);
        trade.setOfferedResource(offRes);
        trade.setOfferedAmount(offAmt);
        trade.setTradeType(type);
        meshTradeRepository.save(trade);
    }

    private double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    private boolean isBotTechUnlocked(Civilization civ, String botType) {
        if (technologyRepository == null) return true;

        java.util.List<String> requiredTechs = switch (botType) {
            case "ECO" -> java.util.List.of("Water Wells", "Irrigation Systems", "Water Treatment", "Atmospheric Processing");
            case "SCIENCE" -> java.util.List.of("Fire Mastery", "Mathematics", "Scientific Method", "Fusion Power");
            case "SECURITY" -> java.util.List.of("Shelter Building", "Town Planning", "Urban Development", "Arcologies");
            default -> java.util.List.of();
        };

        if (requiredTechs.isEmpty()) return true;

        java.util.List<io.github.opencivilizationplatform.modules.technology.domain.Technology> techs = 
            technologyRepository.findByCivilizationId(civ.getId());
        
        if (techs.isEmpty()) return true;

        return techs.stream()
            .filter(t -> t.getStatus() == io.github.opencivilizationplatform.modules.technology.domain.TechnologyStatus.COMPLETED)
            .anyMatch(t -> requiredTechs.contains(t.getName()));
    }

    private void simulateRuleVoting(Civilization civ, List<String> cortexLogs) {
        List<Rule> proposedRules = ruleRepository.findByCivilizationId(civ.getId()).stream()
            .filter(r -> r.getStatus() == RuleStatus.PROPOSED)
            .toList();
            
        if (proposedRules.isEmpty()) return;
        
        for (Rule rule : proposedRules) {
            double yesChance = 0.3; // Base chance of 30%
            
            String code = rule.getLogicCode();
            if (code != null) {
                if (code.contains("LOCK_ENTRY")) {
                    if (civ.getFood() < 35.0 || civ.getWater() < 35.0) {
                        yesChance = 0.85; // High priority under scarcity
                    } else {
                        yesChance = 0.10;
                    }
                } else if (code.contains("RESTRICT_CONSUMPTION")) {
                    if (civ.getFood() < 25.0 || civ.getWater() < 25.0 || civ.getEnergy() < 20.0) {
                        yesChance = 0.75;
                    } else {
                        yesChance = 0.15;
                    }
                } else if (code.contains("BOOST_PRODUCTION")) {
                    if (civ.getEnergy() < 15.0) {
                        yesChance = 0.20;
                    } else {
                        yesChance = 0.60;
                    }
                }
            }
            
            // Simular votes de cidadãos adicionados aleatoriamente neste tick
            int votesToAdd = random.nextInt(3); // 0, 1 ou 2 votos de "sim" por tick
            if (random.nextDouble() < yesChance && votesToAdd > 0) {
                int currentVotes = rule.getVotesCount() == null ? 0 : rule.getVotesCount();
                rule.setVotesCount(currentVotes + votesToAdd);
                
                cortexLogs.add("[Consenso] Regra proposta '" + rule.getTitle() + "' recebeu +" + votesToAdd + " votos de cidadãos (Votos: " + rule.getVotesCount() + "/10).");
                
                if (rule.getVotesCount() >= 10) {
                    rule.setStatus(RuleStatus.ACTIVE);
                    rule.setValidationStatus(io.github.opencivilizationplatform.modules.participation.domain.ValidationStatus.SCIENTIFICALLY_VALIDATED);
                    rule.setValidatedBy("Consenso Autônomo Cortex");
                    cortexLogs.add("[⚖️ Consenso] APROVADA: A regra '" + rule.getTitle() + "' atingiu o quórum de 10 votos e agora está ATIVA!");
                }
                ruleRepository.save(rule);
            }
        }
    }
}

