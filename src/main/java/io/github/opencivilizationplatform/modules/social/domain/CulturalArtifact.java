package io.github.opencivilizationplatform.modules.social.domain;

public record CulturalArtifact(
    String artifactId,
    String creatorAgentId,
    String title,
    String era,
    double prestigeValue
) {
    public CulturalArtifact {
        prestigeValue = Math.max(0.0, prestigeValue);
    }
}
