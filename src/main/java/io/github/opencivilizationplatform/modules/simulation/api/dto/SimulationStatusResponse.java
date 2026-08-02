package io.github.opencivilizationplatform.modules.simulation.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Simulation engine status response")
public class SimulationStatusResponse {
    @Schema(description = "Engine version identifier")
    private String engine;
    @Schema(description = "Number of active rules in the engine")
    private int activeRulesCount;
    @Schema(description = "Most recent decision made by the engine")
    private String lastDecision;
    @Schema(description = "Categories being monitored by the engine")
    private List<String> monitoredCategories;
    @Schema(description = "Current simulation tick number")
    private int tick;
    @Schema(description = "History of recent decisions")
    private List<String> decisionHistory;

    public SimulationStatusResponse() {}

    public SimulationStatusResponse(String engine, int activeRulesCount, String lastDecision,
                                    List<String> monitoredCategories, int tick, List<String> decisionHistory) {
        this.engine = engine;
        this.activeRulesCount = activeRulesCount;
        this.lastDecision = lastDecision;
        this.monitoredCategories = monitoredCategories;
        this.tick = tick;
        this.decisionHistory = decisionHistory;
    }

    public String getEngine() { return engine; }
    public void setEngine(String engine) { this.engine = engine; }

    public int getActiveRulesCount() { return activeRulesCount; }
    public void setActiveRulesCount(int activeRulesCount) { this.activeRulesCount = activeRulesCount; }

    public String getLastDecision() { return lastDecision; }
    public void setLastDecision(String lastDecision) { this.lastDecision = lastDecision; }

    public List<String> getMonitoredCategories() { return monitoredCategories; }
    public void setMonitoredCategories(List<String> monitoredCategories) { this.monitoredCategories = monitoredCategories; }

    public int getTick() { return tick; }
    public void setTick(int tick) { this.tick = tick; }

    public List<String> getDecisionHistory() { return decisionHistory; }
    public void setDecisionHistory(List<String> decisionHistory) { this.decisionHistory = decisionHistory; }
}
