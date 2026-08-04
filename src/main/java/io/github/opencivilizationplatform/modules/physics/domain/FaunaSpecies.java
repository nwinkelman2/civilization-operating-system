package io.github.opencivilizationplatform.modules.physics.domain;

public record FaunaSpecies(
    String speciesName,
    long population,
    double reproductionRate
) {}
