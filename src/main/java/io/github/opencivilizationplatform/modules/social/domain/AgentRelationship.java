package io.github.opencivilizationplatform.modules.social.domain;

public record AgentRelationship(
    String sourceAgentId,
    String targetAgentId,
    double trustScore,
    double rivalryScore
) {
    public AgentRelationship {
        trustScore = Math.max(0.0, Math.min(100.0, trustScore));
        rivalryScore = Math.max(0.0, Math.min(100.0, rivalryScore));
    }
}
