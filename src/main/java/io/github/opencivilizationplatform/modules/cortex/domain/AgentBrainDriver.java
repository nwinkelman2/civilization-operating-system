package io.github.opencivilizationplatform.modules.cortex.domain;

public interface AgentBrainDriver {
    String getDriverType();
    String processDecision(String context);
}
