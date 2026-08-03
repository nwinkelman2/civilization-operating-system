package io.github.opencivilizationplatform.modules.civilization.application;

import io.github.opencivilizationplatform.modules.civilization.domain.CivilizationConfigMatrix;
import io.github.opencivilizationplatform.modules.civilization.domain.CivilizationConfigMatrix.ResourceAllocationPriority;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CivilizationConfigService {

    private final Map<Long, CivilizationConfigMatrix> configRegistry = new ConcurrentHashMap<>();

    public CivilizationConfigMatrix getDefaultConfig() {
        return CivilizationConfigMatrix.defaultConfig();
    }

    public CivilizationConfigMatrix createCustomConfig(
        ResourceAllocationPriority priority,
        double taxRate,
        double autonomyLevel
    ) {
        return new CivilizationConfigMatrix(priority, taxRate, autonomyLevel);
    }

    public CivilizationConfigMatrix saveConfig(Long civilizationId, CivilizationConfigMatrix config) {
        if (civilizationId == null) {
            throw new IllegalArgumentException("civilizationId cannot be null");
        }
        if (config == null) {
            config = getDefaultConfig();
        }
        configRegistry.put(civilizationId, config);
        return config;
    }

    public CivilizationConfigMatrix getConfig(Long civilizationId) {
        if (civilizationId == null) {
            return getDefaultConfig();
        }
        return configRegistry.getOrDefault(civilizationId, getDefaultConfig());
    }

    public CivilizationConfigMatrix updateResourcePriority(Long civilizationId, ResourceAllocationPriority priority) {
        CivilizationConfigMatrix current = getConfig(civilizationId);
        CivilizationConfigMatrix updated = new CivilizationConfigMatrix(priority, current.taxRate(), current.autonomyLevel());
        return saveConfig(civilizationId, updated);
    }

    public CivilizationConfigMatrix updateTaxRate(Long civilizationId, double taxRate) {
        CivilizationConfigMatrix current = getConfig(civilizationId);
        CivilizationConfigMatrix updated = new CivilizationConfigMatrix(current.resourceAllocationPriority(), taxRate, current.autonomyLevel());
        return saveConfig(civilizationId, updated);
    }

    public CivilizationConfigMatrix updateAutonomyLevel(Long civilizationId, double autonomyLevel) {
        CivilizationConfigMatrix current = getConfig(civilizationId);
        CivilizationConfigMatrix updated = new CivilizationConfigMatrix(current.resourceAllocationPriority(), current.taxRate(), autonomyLevel);
        return saveConfig(civilizationId, updated);
    }
}
