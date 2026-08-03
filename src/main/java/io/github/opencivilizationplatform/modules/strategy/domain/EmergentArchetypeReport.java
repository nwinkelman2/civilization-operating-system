package io.github.opencivilizationplatform.modules.strategy.domain;

public record EmergentArchetypeReport(
    String archetype,
    Long civilizationId,
    String civilizationName,
    double emergenceScore,
    String keyFeature
) {}
