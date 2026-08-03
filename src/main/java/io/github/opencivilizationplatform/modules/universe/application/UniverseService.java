package io.github.opencivilizationplatform.modules.universe.application;

import io.github.opencivilizationplatform.modules.universe.domain.UniverseConfig;
import org.springframework.stereotype.Service;

@Service
public class UniverseService {

    private final UniverseConfig globalConfig;

    public UniverseService() {
        this(UniverseConfig.defaultConfig());
    }

    public UniverseService(UniverseConfig globalConfig) {
        this.globalConfig = globalConfig;
    }

    public UniverseConfig getGlobalConfig() {
        return globalConfig;
    }

    public boolean validateEntropyBounds(double energy) {
        return energy >= 0.0 && energy <= globalConfig.maxWorldEnergyCap();
    }
}
