package io.github.opencivilizationplatform.modules.life.application;

import io.github.opencivilizationplatform.modules.life.domain.EpidemicStatus;
import org.springframework.stereotype.Service;

@Service
public class HealthDiseaseService {

    public double calculateContagionRisk(double airQualityIndex, double populationDensity) {
        double safeAqiThreshold = 50.0;
        double aqiRisk = airQualityIndex > safeAqiThreshold ? (airQualityIndex - safeAqiThreshold) * 0.002 : 0.0;
        double densityRisk = Math.max(0.0, populationDensity) * 0.0001;
        double totalRisk = 0.05 + aqiRisk + densityRisk;
        return Math.min(1.0, Math.max(0.0, totalRisk));
    }

    public double calculateCureRate(int medicalFacilities, double medicalTechLevel) {
        if (medicalFacilities <= 0) {
            return 0.0;
        }
        double baseCureRate = medicalFacilities * 0.1;
        double techMultiplier = 1.0 + Math.max(0.0, medicalTechLevel);
        double totalCureRate = baseCureRate * techMultiplier;
        return Math.min(1.0, Math.max(0.0, totalCureRate));
    }

    public EpidemicStatus evaluateEpidemicStatus(int totalPopulation, double airQualityIndex, double populationDensity,
                                                  int medicalFacilities, double medicalTechLevel) {
        if (totalPopulation <= 0) {
            return new EpidemicStatus(0, 0.0, false);
        }

        double contagionRisk = calculateContagionRisk(airQualityIndex, populationDensity);
        double cureRate = calculateCureRate(medicalFacilities, medicalTechLevel);

        double netRisk = Math.max(0.0, contagionRisk * (1.0 - cureRate));
        int infectedCount = (int) Math.round(totalPopulation * netRisk);
        double severityScore = Math.min(1.0, netRisk * 1.5);
        boolean activeOutbreak = severityScore >= 0.25 || infectedCount > 20;

        return new EpidemicStatus(infectedCount, severityScore, activeOutbreak);
    }

    public EpidemicStatus processHealthTick(int totalPopulation, double airQualityIndex, double populationDensity, int medicalFacilities) {
        return evaluateEpidemicStatus(totalPopulation, airQualityIndex, populationDensity, medicalFacilities, 1.0);
    }
}
