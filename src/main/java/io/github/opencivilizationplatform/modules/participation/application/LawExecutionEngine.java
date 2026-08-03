package io.github.opencivilizationplatform.modules.participation.application;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import io.github.opencivilizationplatform.dto.BalanceDTO;
import io.github.opencivilizationplatform.modules.participation.domain.Rule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LawExecutionEngine {

    private static final Logger log = LoggerFactory.getLogger(LawExecutionEngine.class);
    private final ObjectMapper objectMapper;

    public LawExecutionEngine(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public record RuleEffectResult(
        Long ruleId,
        String ruleTitle,
        boolean executed,
        String detail,
        String metricAdjusted,
        double deltaApplied
    ) {}

    public record LawExecutionSummary(
        int rulesEvaluated,
        int rulesExecuted,
        List<RuleEffectResult> effects
    ) {}

    public LawExecutionSummary evaluateAndApplyLaws(List<Rule> rules, List<BalanceDTO> balances) {
        if (rules == null || rules.isEmpty()) {
            return new LawExecutionSummary(0, 0, List.of());
        }

        List<RuleEffectResult> effects = new ArrayList<>();
        int executedCount = 0;

        for (Rule rule : rules) {
            try {
                if (rule.getLogicCode() == null || rule.getLogicCode().isBlank()) {
                    effects.add(new RuleEffectResult(rule.getId(), rule.getTitle(), false, "No logic code provided", null, 0.0));
                    continue;
                }

                JsonNode logic = objectMapper.readTree(rule.getLogicCode());
                JsonNode typeNode = logic.get("type");
                if (typeNode == null) {
                    effects.add(new RuleEffectResult(rule.getId(), rule.getTitle(), false, "Logic code missing 'type' field", null, 0.0));
                    continue;
                }

                String type = typeNode.asText();

                switch (type) {
                    case "METRIC_ADJUSTMENT" -> {
                        JsonNode metricNode = logic.get("metric");
                        JsonNode deltaNode = logic.get("delta");
                        if (metricNode != null && deltaNode != null) {
                            String targetMetric = metricNode.asText();
                            double delta = deltaNode.asDouble();
                            boolean applied = applyMetricDelta(balances, targetMetric, delta);
                            executedCount++;
                            effects.add(new RuleEffectResult(
                                rule.getId(),
                                rule.getTitle(),
                                applied,
                                String.format("Applied metric adjustment %.2f to %s", delta, targetMetric),
                                targetMetric,
                                delta
                            ));
                        }
                    }
                    case "RESERVE_CHECK" -> {
                        JsonNode metricNode = logic.get("metric");
                        String metricCat = metricNode != null ? metricNode.asText() : "UNKNOWN";
                        executedCount++;
                        effects.add(new RuleEffectResult(
                            rule.getId(),
                            rule.getTitle(),
                            true,
                            String.format("Reserve check rule evaluated for metric %s", metricCat),
                            metricCat,
                            0.0
                        ));
                    }
                    case "THRESHOLD_TRIGGER" -> {
                        executedCount++;
                        effects.add(new RuleEffectResult(
                            rule.getId(),
                            rule.getTitle(),
                            true,
                            "Threshold trigger rule evaluated",
                            null,
                            0.0
                        ));
                    }
                    default -> {
                        executedCount++;
                        effects.add(new RuleEffectResult(
                            rule.getId(),
                            rule.getTitle(),
                            true,
                            String.format("Generic rule type '%s' evaluated", type),
                            null,
                            0.0
                        ));
                    }
                }
            } catch (Exception e) {
                log.error("Failed to execute law rule {}: {}", rule.getId(), e.getMessage());
                effects.add(new RuleEffectResult(rule.getId(), rule.getTitle(), false, "Error: " + e.getMessage(), null, 0.0));
            }
        }

        return new LawExecutionSummary(rules.size(), executedCount, effects);
    }

    private boolean applyMetricDelta(List<BalanceDTO> balances, String metricCategory, double delta) {
        if (balances == null || metricCategory == null) {
            return false;
        }
        for (BalanceDTO b : balances) {
            if (metricCategory.equalsIgnoreCase(b.getCategory())) {
                double currentSupply = b.getSupply() != null ? b.getSupply() : 0.0;
                b.setSupply(Math.max(0.0, currentSupply + delta));
                if (b.getDemand() != null && b.getDemand() > 0) {
                    b.setPercentageMet(Math.min(100.0, (b.getSupply() / b.getDemand()) * 100.0));
                }
                return true;
            }
        }
        return false;
    }
}
