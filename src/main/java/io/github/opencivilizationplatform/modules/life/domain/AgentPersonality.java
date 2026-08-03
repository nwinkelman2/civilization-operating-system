package io.github.opencivilizationplatform.modules.life.domain;

public record AgentPersonality(
    double cooperationIndex,
    double riskTolerance,
    double innovationFocus
) {
    public AgentPersonality {
        cooperationIndex = Math.max(0.0, Math.min(1.0, cooperationIndex));
        riskTolerance = Math.max(0.0, Math.min(1.0, riskTolerance));
        innovationFocus = Math.max(0.0, Math.min(1.0, innovationFocus));
    }
}
