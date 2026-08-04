package io.github.opencivilizationplatform.modules.physics.application;

import org.springframework.stereotype.Service;

@Service
public class ResourceStewardshipService {

    public record StewardshipReport(
        double ecologicalFootprint,
        double sustainabilityIndex,
        String status
    ) {}

    public double calculateEcologicalFootprint(double consumptionRate, double pollutionRate, int population) {
        double safeConsumption = Math.max(0.0, consumptionRate);
        double safePollution = Math.max(0.0, pollutionRate);
        int safePopulation = Math.max(1, population);

        double baseUsage = (safeConsumption * 0.6) + (safePollution * 0.4);
        double popFactor = safePopulation / 100.0;

        return baseUsage * popFactor;
    }

    public double calculateSustainabilityIndex(double regenerationRate, double ecologicalFootprint) {
        double safeRegen = Math.max(0.0, regenerationRate);
        double safeFootprint = Math.max(0.0, ecologicalFootprint);

        if (safeFootprint == 0.0) {
            return safeRegen > 0.0 ? 2.0 : 1.0;
        }

        return safeRegen / safeFootprint;
    }

    public StewardshipReport evaluateStewardship(double consumptionRate, double pollutionRate, double regenerationRate, int population) {
        double footprint = calculateEcologicalFootprint(consumptionRate, pollutionRate, population);
        double index = calculateSustainabilityIndex(regenerationRate, footprint);

        String status;
        if (index >= 1.0) {
            status = "SUSTAINABLE";
        } else if (index >= 0.5) {
            status = "MODERATE_IMPACT";
        } else {
            status = "ECOLOGICAL_CRITICAL";
        }

        return new StewardshipReport(footprint, index, status);
    }

    public StewardshipReport processStewardshipTick(double consumptionRate, double pollutionRate, double regenerationRate, int population) {
        return evaluateStewardship(consumptionRate, pollutionRate, regenerationRate, population);
    }
}
