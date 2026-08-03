package io.github.opencivilizationplatform.modules.participation;

import tools.jackson.databind.ObjectMapper;
import io.github.opencivilizationplatform.dto.BalanceDTO;
import io.github.opencivilizationplatform.modules.participation.application.LawExecutionEngine;
import io.github.opencivilizationplatform.modules.participation.application.LawExecutionEngine.LawExecutionSummary;
import io.github.opencivilizationplatform.modules.participation.domain.Rule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LawExecutionEngineTest {

    private LawExecutionEngine lawExecutionEngine;

    @BeforeEach
    void setUp() {
        lawExecutionEngine = new LawExecutionEngine(new ObjectMapper());
    }

    @Test
    void testEvaluateAndApplyLawsEmptyRules() {
        LawExecutionSummary summary = lawExecutionEngine.evaluateAndApplyLaws(List.of(), List.of());
        assertEquals(0, summary.rulesEvaluated());
        assertEquals(0, summary.rulesExecuted());
        assertTrue(summary.effects().isEmpty());
    }

    @Test
    void testEvaluateAndApplyLawsMetricAdjustment() {
        Rule rule = new Rule();
        rule.setId(10L);
        rule.setTitle("Clean Water Initiative");
        rule.setLogicCode("{\"type\": \"METRIC_ADJUSTMENT\", \"metric\": \"WATER\", \"delta\": 25.0}");

        BalanceDTO waterBalance = new BalanceDTO("WATER", 50.0, 100.0, "units", 50.0, "NORMAL");
        List<BalanceDTO> balances = new ArrayList<>(List.of(waterBalance));

        LawExecutionSummary summary = lawExecutionEngine.evaluateAndApplyLaws(List.of(rule), balances);

        assertEquals(1, summary.rulesEvaluated());
        assertEquals(1, summary.rulesExecuted());
        assertEquals(1, summary.effects().size());
        assertTrue(summary.effects().get(0).executed());
        assertEquals(75.0, waterBalance.getSupply());
        assertEquals(75.0, waterBalance.getPercentageMet());
    }

    @Test
    void testEvaluateAndApplyLawsReserveCheck() {
        Rule rule = new Rule();
        rule.setId(11L);
        rule.setTitle("Reserve Check Rule");
        rule.setLogicCode("{\"type\": \"RESERVE_CHECK\", \"metric\": \"FOOD\"}");

        LawExecutionSummary summary = lawExecutionEngine.evaluateAndApplyLaws(List.of(rule), List.of());

        assertEquals(1, summary.rulesEvaluated());
        assertEquals(1, summary.rulesExecuted());
        assertTrue(summary.effects().get(0).executed());
    }
}
