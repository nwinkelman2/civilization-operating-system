package io.github.opencivilizationplatform.modules.simulation.api.dto;

import java.util.List;

public class SimulationStatusResponse {
    private String engine;
    private int activeRulesCount;
    private String lastDecision;
    private List<String> monitoredCategories;
    private int tick;
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
