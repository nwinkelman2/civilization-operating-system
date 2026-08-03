package io.github.opencivilizationplatform.modules.universe.domain;

public record UniverseConfig(
    long cycleDurationMs,
    double maxWorldEnergyCap,
    double entropyDecayRate
) {
    public UniverseConfig {
        if (cycleDurationMs <= 0) {
            throw new IllegalArgumentException("cycleDurationMs must be positive");
        }
        if (maxWorldEnergyCap < 0) {
            throw new IllegalArgumentException("maxWorldEnergyCap cannot be negative");
        }
        if (entropyDecayRate < 0.0 || entropyDecayRate > 1.0) {
            throw new IllegalArgumentException("entropyDecayRate must be between 0.0 and 1.0");
        }
    }

    public static UniverseConfig defaultConfig() {
        return new UniverseConfig(15000L, 1_000_000.0, 0.001);
    }

    public long getCycleDurationMs() {
        return cycleDurationMs;
    }

    public double getMaxWorldEnergyCap() {
        return maxWorldEnergyCap;
    }

    public double getEntropyDecayRate() {
        return entropyDecayRate;
    }
}
