package io.github.opencivilizationplatform.modules.cortex;

import io.github.opencivilizationplatform.modules.cortex.application.AgentBrainRuntimeService;
import io.github.opencivilizationplatform.modules.cortex.domain.AgentBrainDriver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AgentBrainRuntimeServiceTest {

    private AgentBrainRuntimeService runtimeService;

    @BeforeEach
    void setUp() {
        runtimeService = new AgentBrainRuntimeService();
    }

    @Test
    void testDefaultDriversRegistered() {
        assertNotNull(runtimeService.getDriver("RULE_BASED"));
        assertNotNull(runtimeService.getDriver("LLM_PROMPT"));
        assertNotNull(runtimeService.getDriver("HYBRID"));
        assertEquals("RULE_BASED", runtimeService.getActiveDriverType());
    }

    @Test
    void testProcessDecisionWithBuiltInDrivers() {
        String ruleDecision = runtimeService.processDecision("RULE_BASED", "scarcity_event");
        assertTrue(ruleDecision.contains("[RULE_BASED]"));

        String llmDecision = runtimeService.processDecision("LLM_PROMPT", "diplomatic_envoy");
        assertTrue(llmDecision.contains("[LLM_PROMPT]"));

        String hybridDecision = runtimeService.processDecision("HYBRID", "trade_dispute");
        assertTrue(hybridDecision.contains("[HYBRID]"));
    }

    @Test
    void testActiveDriverSwitch() {
        runtimeService.setActiveDriverType("HYBRID");
        assertEquals("HYBRID", runtimeService.getActiveDriverType());

        String decision = runtimeService.processActiveDriverDecision("active_context");
        assertTrue(decision.contains("[HYBRID]"));
    }

    @Test
    void testRegisterCustomDriver() {
        AgentBrainDriver customDriver = new AgentBrainDriver() {
            @Override
            public String getDriverType() {
                return "NEURAL_NET";
            }

            @Override
            public String processDecision(String context) {
                return "[NEURAL_NET] Processed " + context;
            }
        };

        runtimeService.registerDriver(customDriver);
        assertNotNull(runtimeService.getDriver("NEURAL_NET"));

        String decision = runtimeService.processDecision("NEURAL_NET", "test_input");
        assertEquals("[NEURAL_NET] Processed test_input", decision);
    }

    @Test
    void testUnknownDriverFallback() {
        String decision = runtimeService.processDecision("NON_EXISTENT_DRIVER", "some_context");
        // Falls back to active driver (RULE_BASED by default)
        assertTrue(decision.contains("[RULE_BASED]"));
    }

    @Test
    void testProcessBrainRuntimeTick() {
        assertDoesNotThrow(() -> runtimeService.processBrainRuntimeTick());
    }
}
