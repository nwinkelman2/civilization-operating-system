package io.github.opencivilizationplatform.modules.life.domain;

public record EpidemicStatus(
    int infectedCount,
    double severityScore,
    boolean activeOutbreak
) {}
