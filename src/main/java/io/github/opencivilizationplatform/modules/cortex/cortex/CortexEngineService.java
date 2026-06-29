package io.github.opencivilizationplatform.modules.cortex.cortex;

import io.github.opencivilizationplatform.modules.civilization.domain.Civilization;
import io.github.opencivilizationplatform.modules.civilization.infrastructure.CivilizationRepository;
import io.github.opencivilizationplatform.modules.cortex.domain.ResourceTick;
import io.github.opencivilizationplatform.modules.region.domain.ResourceRegion;
import io.github.opencivilizationplatform.modules.region.infrastructure.ResourceRegionRepository;
import io.github.opencivilizationplatform.modules.participation.domain.Rule;
import io.github.opencivilizationplatform.modules.participation.domain.RuleStatus;
import io.github.opencivilizationplatform.modules.participation.infrastructure.RuleRepository;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private final Random random = new Random();

    public CortexEngineService(CivilizationRepository civilizationRepository,
                               ResourceRegionRepository resourceRegionRepository,
                               RuleRepository ruleRepository,
                               ObjectMapper objectMapper,
                               io.github.opencivilizationplatform.modules.nexus.infrastructure.MeshTradeRepository meshTradeRepository) {
        this.civilizationRepository = civilizationRepository;
        this.resourceRegionRepository = resourceRegionRepository;
        this.ruleRepository = ruleRepository;
        this.objectMapper = objectMapper;
        this.meshTradeRepository = meshTradeRepository;
    }

    @Scheduled(fixedRateString = "${cortex.engine.tick-rate-ms:30000}")
    @Transactional
    @SchedulerLock(name = "cortexEngineTick", lockAtMostFor = "25s", lockAtLeastFor = "10s")
    public void tick() {
        List<Civilization> civilizations = civilizationRepository.findAll();
        if (civilizations.isEmpty()) return;

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

        if (historyArray.size() > 0) {
            JsonNode lastTick = historyArray.get(historyArray.size() - 1);
            if (lastTick.has("agriBots")) agriBots = lastTick.get("agriBots").asInt();
            if (lastTick.has("aquaBots")) aquaBots = lastTick.get("aquaBots").asInt();
            if (lastTick.has("exploreBots")) exploreBots = lastTick.get("exploreBots").asInt();
            if (lastTick.has("utilityBots")) utilityBots = lastTick.get("utilityBots").asInt();
        }

        // 3. Processar Automação Robótica pelo Cortex local
        double robotFoodBonus = 0;
        double robotWaterBonus = 0;
        double robotMineralBonus = 0;
        double robotHousingBonus = 0;

        if (isRobotsActive) {
            int totalRobots = agriBots + aquaBots + exploreBots + utilityBots;
            int pop = civ.getPopulation() != null ? civ.getPopulation() : 100;
            int maxRobots = 1 + (pop / 50);
            if (maxRobots > 12) maxRobots = 12;

            double currentMinerals = civ.getMinerals() != null ? civ.getMinerals() : 0;
            double currentEnergy = civ.getEnergy() != null ? civ.getEnergy() : 0;

            // Decidir se fabrica um novo robô
            if (totalRobots < maxRobots && currentMinerals >= 15.0 && currentEnergy >= 10.0) {
                civ.setMinerals(currentMinerals - 15.0);
                civ.setEnergy(currentEnergy - 10.0);
                currentMinerals -= 15.0;
                currentEnergy -= 10.0;

                int sum = (civ.getAgriBotsPriority() != null ? civ.getAgriBotsPriority() : 25)
                        + (civ.getAquaBotsPriority() != null ? civ.getAquaBotsPriority() : 25)
                        + (civ.getExploreBotsPriority() != null ? civ.getExploreBotsPriority() : 25)
                        + (civ.getUtilityBotsPriority() != null ? civ.getUtilityBotsPriority() : 25);
                if (sum == 0) sum = 100;

                double targetAgri = (double) (civ.getAgriBotsPriority() != null ? civ.getAgriBotsPriority() : 25) / sum;
                double targetAqua = (double) (civ.getAquaBotsPriority() != null ? civ.getAquaBotsPriority() : 25) / sum;
                double targetExplore = (double) (civ.getExploreBotsPriority() != null ? civ.getExploreBotsPriority() : 25) / sum;
                double targetUtility = (double) (civ.getUtilityBotsPriority() != null ? civ.getUtilityBotsPriority() : 25) / sum;

                String botType;
                if (totalRobots == 0) {
                    if (targetAgri >= targetAqua && targetAgri >= targetExplore && targetAgri >= targetUtility) {
                        agriBots++;
                        botType = "Agri-Bot (Agropecuária)";
                    } else if (targetAqua >= targetExplore && targetAqua >= targetUtility) {
                        aquaBots++;
                        botType = "Aqua-Bot (Recursos Hídricos)";
                    } else if (targetExplore >= targetUtility) {
                        exploreBots++;
                        botType = "Explorer-Bot (Exploração Mineral)";
                    } else {
                        utilityBots++;
                        botType = "Utility-Bot (Infraestrutura/Moradia)";
                    }
                } else {
                    double currentAgriRatio = (double) agriBots / totalRobots;
                    double currentAquaRatio = (double) aquaBots / totalRobots;
                    double currentExploreRatio = (double) exploreBots / totalRobots;
                    double currentUtilityRatio = (double) utilityBots / totalRobots;

                    double diffAgri = targetAgri - currentAgriRatio;
                    double diffAqua = targetAqua - currentAquaRatio;
                    double diffExplore = targetExplore - currentExploreRatio;
                    double diffUtility = targetUtility - currentUtilityRatio;

                    if (diffAgri >= diffAqua && diffAgri >= diffExplore && diffAgri >= diffUtility) {
                        agriBots++;
                        botType = "Agri-Bot (Agropecuária)";
                    } else if (diffAqua >= diffExplore && diffAqua >= diffUtility) {
                        aquaBots++;
                        botType = "Aqua-Bot (Recursos Hídricos)";
                    } else if (diffExplore >= diffUtility) {
                        exploreBots++;
                        botType = "Explorer-Bot (Exploração Mineral)";
                    } else {
                        utilityBots++;
                        botType = "Utility-Bot (Infraestrutura/Moradia)";
                    }
                }
                totalRobots++;
                cortexLogs.add("[Automação] Cortex fabricou 1 " + botType + " (Custo: 15 minerais, 10 energia).");
            }

            // Operar robôs (consomem 0.15 energia cada)
            if (totalRobots > 0) {
                double energyRequired = totalRobots * 0.15;
                if (currentEnergy >= energyRequired) {
                    civ.setEnergy(currentEnergy - energyRequired);
                    robotFoodBonus = agriBots * 0.8;
                    robotWaterBonus = aquaBots * 0.6;
                    robotMineralBonus = exploreBots * 0.5;
                    robotHousingBonus = utilityBots * 0.4;
                    cortexLogs.add("[Automação] Sistemas Robóticos ONLINE: " + totalRobots + " drones operando (Consumo: " + String.format("%.2f", energyRequired) + " energia).");
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
        if (isAutonomousTradeActive && (currentFood < 30.0 || currentWater < 30.0)) {
            String criticalResource = currentFood < 30.0 ? "FOOD" : "WATER";
            List<Civilization> allCivs = civilizationRepository.findAll();
            Civilization partner = null;

            for (Civilization potentialPartner : allCivs) {
                if (potentialPartner.getId().equals(civ.getId())) continue;

                double partnerAmount = criticalResource.equals("FOOD") ? 
                    (potentialPartner.getFood() != null ? potentialPartner.getFood() : 0) : 
                    (potentialPartner.getWater() != null ? potentialPartner.getWater() : 0);

                if (partnerAmount > 80.0) {
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

                if (currentMinerals > 60.0 || currentEnergy > 60.0) {
                    String offeredResource = currentMinerals > 60.0 ? "MINERALS" : "ENERGY";
                    
                    if (criticalResource.equals("FOOD")) {
                        resourceDelta[0] += 30.0;
                        partner.setFood(partner.getFood() - 30.0);
                    } else {
                        resourceDelta[1] += 30.0;
                        partner.setWater(partner.getWater() - 30.0);
                    }

                    if (offeredResource.equals("MINERALS")) {
                        resourceDelta[2] -= 30.0;
                        partner.setMinerals((partner.getMinerals() != null ? partner.getMinerals() : 0) + 30.0);
                    } else {
                        resourceDelta[3] -= 30.0;
                        partner.setEnergy((partner.getEnergy() != null ? partner.getEnergy() : 0) + 30.0);
                    }

                    String logMsg = "[Comércio Autônomo] Cortex importou 30.0 de " + (criticalResource.equals("FOOD") ? "Alimento" : "Água") + 
                        " da sociedade '" + partner.getName() + "' em troca de 30.0 de " + (offeredResource.equals("MINERALS") ? "Minerais" : "Energia") + ".";
                    cortexLogs.add(logMsg);
                    
                    addLogToPartnerHistory(partner, "[Comércio Autônomo] Cortex exportou 30.0 de " + (criticalResource.equals("FOOD") ? "Alimento" : "Água") + 
                        " para '" + civ.getName() + "' em troca de 30.0 de " + (offeredResource.equals("MINERALS") ? "Minerais" : "Energia") + ".");
                    
                    // Save to mesh_trades ledger
                    io.github.opencivilizationplatform.modules.nexus.domain.MeshTrade trade = new io.github.opencivilizationplatform.modules.nexus.domain.MeshTrade();
                    trade.setSender(partner);
                    trade.setReceiver(civ);
                    trade.setRequestedResource(criticalResource);
                    trade.setRequestedAmount(30.0);
                    trade.setOfferedResource(offeredResource);
                    trade.setOfferedAmount(30.0);
                    trade.setTradeType("RESOURCE_BARTER");
                    meshTradeRepository.save(trade);

                    civilizationRepository.save(partner);
                } else if (population >= 40) {
                    int partnerPop = partner.getPopulation() != null ? partner.getPopulation() : 0;
                    if (partnerPop < 300) {
                        if (criticalResource.equals("FOOD")) {
                            resourceDelta[0] += 35.0;
                            partner.setFood(partner.getFood() - 35.0);
                        } else {
                            resourceDelta[1] += 35.0;
                            partner.setWater(partner.getWater() - 35.0);
                        }

                        populationDelta -= 5;
                        partner.setPopulation(partnerPop + 5);

                        String logMsg = "[Comércio Autônomo] Cortex importou 35.0 de " + (criticalResource.equals("FOOD") ? "Alimento" : "Água") + 
                            " de '" + partner.getName() + "' transferindo 5 trabalhadores (pessoal/população).";
                        cortexLogs.add(logMsg);

                        addLogToPartnerHistory(partner, "[Comércio Autônomo] Cortex recebeu 5 trabalhadores de '" + civ.getName() + 
                            "' em troca de 35.0 de " + (criticalResource.equals("FOOD") ? "Alimento" : "Água") + ".");

                        // Save to mesh_trades ledger
                        io.github.opencivilizationplatform.modules.nexus.domain.MeshTrade trade = new io.github.opencivilizationplatform.modules.nexus.domain.MeshTrade();
                        trade.setSender(partner);
                        trade.setReceiver(civ);
                        trade.setRequestedResource(criticalResource);
                        trade.setRequestedAmount(35.0);
                        trade.setOfferedResource("PERSONNEL");
                        trade.setOfferedAmount(5.0);
                        trade.setTradeType("PERSONNEL_EXCHANGE");
                        meshTradeRepository.save(trade);

                        civilizationRepository.save(partner);
                    }
                }
            } else {
                cortexLogs.add("[Alerta Comércio] Escassez crítica detectada. Nenhum nó parceiro disponível no mesh.");
            }
        }

        return new ResourceTick(
            civ.getId(), resourceDelta[0], resourceDelta[1], resourceDelta[2],
            resourceDelta[3], resourceDelta[4], populationDelta, reputationDelta,
            agriBots, aquaBots, exploreBots, utilityBots, cortexLogs
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

    private double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
}

