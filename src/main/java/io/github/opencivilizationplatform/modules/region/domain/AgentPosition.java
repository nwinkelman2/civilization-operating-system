package io.github.opencivilizationplatform.modules.region.domain;

public record AgentPosition(
    String agentId,
    double x,
    double y,
    double speed
) {
    public AgentPosition {
        if (speed < 0) {
            speed = 0.0;
        }
    }
}
