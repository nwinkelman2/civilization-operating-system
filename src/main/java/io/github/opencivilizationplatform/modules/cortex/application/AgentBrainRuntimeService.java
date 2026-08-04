package io.github.opencivilizationplatform.modules.cortex.application;

import io.github.opencivilizationplatform.modules.cortex.domain.AgentBrainDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AgentBrainRuntimeService {

    private static final Logger log = LoggerFactory.getLogger(AgentBrainRuntimeService.class);

    public static final String RULE_BASED = "RULE_BASED";
    public static final String LLM_PROMPT = "LLM_PROMPT";
    public static final String HYBRID = "HYBRID";

    private final Map<String, AgentBrainDriver> drivers = new ConcurrentHashMap<>();
    private volatile String activeDriverType = RULE_BASED;

    @Autowired(required = false)
    public AgentBrainRuntimeService(List<AgentBrainDriver> customDrivers) {
        // Register built-in fallback drivers
        registerDriver(new DefaultRuleBasedDriver());
        registerDriver(new DefaultLlmPromptDriver());
        registerDriver(new DefaultHybridDriver());

        if (customDrivers != null) {
            for (AgentBrainDriver driver : customDrivers) {
                registerDriver(driver);
            }
        }
    }

    public AgentBrainRuntimeService() {
        this(Collections.emptyList());
    }

    public void registerDriver(AgentBrainDriver driver) {
        if (driver != null && driver.getDriverType() != null) {
            String type = driver.getDriverType().toUpperCase();
            drivers.put(type, driver);
            log.info("[BRAIN RUNTIME] Registered driver type: {}", type);
        }
    }

    public AgentBrainDriver getDriver(String driverType) {
        if (driverType == null) return null;
        return drivers.get(driverType.toUpperCase());
    }

    public String getActiveDriverType() {
        return activeDriverType;
    }

    public void setActiveDriverType(String driverType) {
        if (driverType != null && drivers.containsKey(driverType.toUpperCase())) {
            this.activeDriverType = driverType.toUpperCase();
            log.info("[BRAIN RUNTIME] Active driver switched to: {}", this.activeDriverType);
        } else {
            log.warn("[BRAIN RUNTIME] Driver type '{}' is not registered. Active driver remains: {}", driverType, activeDriverType);
        }
    }

    public Map<String, AgentBrainDriver> getRegisteredDrivers() {
        return Map.copyOf(drivers);
    }

    public String processDecision(String driverType, String context) {
        AgentBrainDriver driver = getDriver(driverType);
        if (driver == null) {
            log.warn("[BRAIN RUNTIME] Driver type '{}' not found, falling back to active driver '{}'", driverType, activeDriverType);
            driver = getDriver(activeDriverType);
        }
        if (driver == null) {
            return "[FALLBACK] No driver available to process context: " + context;
        }
        return driver.processDecision(context);
    }

    public String processActiveDriverDecision(String context) {
        return processDecision(activeDriverType, context);
    }

    public void processBrainRuntimeTick() {
        log.info("[BRAIN RUNTIME] Tick processing with active driver [{}]...", activeDriverType);
    }

    // Built-in drivers
    private static class DefaultRuleBasedDriver implements AgentBrainDriver {
        @Override
        public String getDriverType() {
            return RULE_BASED;
        }

        @Override
        public String processDecision(String context) {
            return "[RULE_BASED] Rule evaluation complete for context: " + (context != null ? context : "NONE");
        }
    }

    private static class DefaultLlmPromptDriver implements AgentBrainDriver {
        @Override
        public String getDriverType() {
            return LLM_PROMPT;
        }

        @Override
        public String processDecision(String context) {
            return "[LLM_PROMPT] LLM prompt decision synthesized for context: " + (context != null ? context : "NONE");
        }
    }

    private static class DefaultHybridDriver implements AgentBrainDriver {
        @Override
        public String getDriverType() {
            return HYBRID;
        }

        @Override
        public String processDecision(String context) {
            return "[HYBRID] Hybrid rule-LLM consensus generated for context: " + (context != null ? context : "NONE");
        }
    }
}
