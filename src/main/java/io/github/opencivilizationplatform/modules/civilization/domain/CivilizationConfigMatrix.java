package io.github.opencivilizationplatform.modules.civilization.domain;

public record CivilizationConfigMatrix(
    ResourceAllocationPriority resourceAllocationPriority,
    double taxRate,
    double autonomyLevel
) {
    public enum ResourceAllocationPriority {
        BALANCED,
        GROWTH,
        DEFENSE,
        RESEARCH
    }

    public CivilizationConfigMatrix {
        if (resourceAllocationPriority == null) {
            resourceAllocationPriority = ResourceAllocationPriority.BALANCED;
        }
        taxRate = Math.max(0.0, Math.min(0.5, taxRate));
        autonomyLevel = Math.max(0.0, Math.min(1.0, autonomyLevel));
    }

    public static CivilizationConfigMatrix defaultConfig() {
        return new CivilizationConfigMatrix(ResourceAllocationPriority.BALANCED, 0.10, 0.50);
    }
}
