package io.github.opencivilizationplatform.modules.cortex.domain;

public record ResourceTick(
    Long civilizationId,
    double foodDelta,
    double waterDelta,
    double mineralsDelta,
    double energyDelta,
    double housingDelta,
    double populationDelta,
    double reputationDelta
) {}
