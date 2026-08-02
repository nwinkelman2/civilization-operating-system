package io.github.opencivilizationplatform.modules.simulation.application;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import io.github.opencivilizationplatform.core.event.BiosphereCriticalEvent;
import io.github.opencivilizationplatform.dto.BalanceDTO;
import io.github.opencivilizationplatform.modules.participation.application.RuleService;
import io.github.opencivilizationplatform.modules.participation.domain.Rule;
import io.github.opencivilizationplatform.modules.simulation.api.dto.SimulationStatusResponse;
import io.github.opencivilizationplatform.modules.strategy.application.BalanceService;
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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class SimulationEngineService {

    private static final Logger log = LoggerFactory.getLogger(SimulationEngineService.class);
    private static final int MAX_DECISION_HISTORY = 15;

    private final RuleService ruleService;
    private final BalanceService balanceService;
    private final ObjectMapper objectMapper;

    private final AtomicInteger tickCounter = new AtomicInteger(0);
    private final AtomicReference<String> lastDecision = new AtomicReference<>("Initializing Civilization Cortex...");
    private final AtomicInteger activeRulesCount = new AtomicInteger(0);
    private final AtomicReference<LocalDateTime> lastTickTime = new AtomicReference<>(LocalDateTime.now());
    private final List<String> monitoredCategories = new ArrayList<>();
    private final LinkedList<String> decisionHistory = new LinkedList<>();

    public SimulationEngineService(RuleService ruleService, BalanceService balanceService, ObjectMapper objectMapper) {
        this.ruleService = ruleService;
        this.balanceService = balanceService;
        this.objectMapper = objectMapper;
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
            balance.forEach(b -> monitoredCategories.add(b.getCategory()));
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
