package io.github.opencivilizationplatform.modules.region.domain;

public record ExplorationExpedition(
    String expeditionId,
    Long civilizationId,
    String targetRegion,
    double progress
) {}
