package io.github.opencivilizationplatform.modules.cortex.domain;

import java.util.List;

public record ResourceTick(
    Long civilizationId,
    double foodDelta,
    double waterDelta,
    double mineralsDelta,
    double energyDelta,
    double housingDelta,
    double populationDelta,
    double reputationDelta,
    int agriBots,
    int aquaBots,
    int exploreBots,
    int utilityBots,
    int ecoBots,
    int scienceBots,
    int securityBots,
    int spyBots,
    List<String> logs
) {}
