package io.github.opencivilizationplatform.modules.life.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AgentMortalityService {

    private static final Logger log = LoggerFactory.getLogger(AgentMortalityService.class);

    public record LifecycleStatus(
        int age,
        double health,
        boolean isDead,
        String causeOfDeath
    ) {}

    public record MortalityCycleResult(
        int startingPopulation,
        int deaths,
        int births,
        int finalPopulation,
        double mortalityRate,
        double birthRate
    ) {}

    public LifecycleStatus evaluateAgingAndHealth(int currentAge, double currentHealth, double environmentalStress) {
        int newAge = currentAge + 1;
        double healthLoss = 0.5;

        if (newAge > 65) {
            healthLoss += (newAge - 65) * 0.2;
        }

        if (environmentalStress > 0.5) {
            healthLoss += (environmentalStress - 0.5) * 10.0;
        }

        double updatedHealth = Math.max(0.0, Math.min(100.0, currentHealth - healthLoss));
        boolean dead = updatedHealth <= 0.0 || evaluateMortalityEvent(newAge, updatedHealth);

        String cause = null;
        if (dead) {
            if (updatedHealth <= 0.0) {
                cause = "HEALTH_FAILURE";
            } else if (newAge > 80) {
                cause = "OLD_AGE";
            } else {
                cause = "ENVIRONMENTAL_COMPLICATION";
            }
        }

        return new LifecycleStatus(newAge, updatedHealth, dead, cause);
    }

    public boolean evaluateMortalityEvent(int age, double health) {
        if (health <= 0.0) {
            return true;
        }
        double baseMortalityRisk = 0.001;
        if (age > 70) {
            baseMortalityRisk += (age - 70) * 0.01;
        }
        if (health < 30.0) {
            baseMortalityRisk += (30.0 - health) * 0.01;
        }
        return Math.random() < Math.min(1.0, baseMortalityRisk);
    }

    public int calculateBirthAdditions(int currentPopulation, double birthRate, double averageHealth) {
        if (currentPopulation <= 0) {
            return 0;
        }
        double healthFactor = Math.max(0.5, averageHealth / 100.0);
        double effectiveBirthRate = birthRate * healthFactor;
        return (int) Math.round(currentPopulation * effectiveBirthRate);
    }

    public int calculateMortalityDeaths(int currentPopulation, double deathRate, double averageHealth) {
        if (currentPopulation <= 0) {
            return 0;
        }
        double healthFactor = averageHealth < 50.0 ? (1.0 + (50.0 - averageHealth) / 50.0) : 1.0;
        double effectiveDeathRate = deathRate * healthFactor;
        return (int) Math.round(currentPopulation * effectiveDeathRate);
    }

    public MortalityCycleResult processLifecycleTick(int population, double baseBirthRate, double baseDeathRate) {
        if (population <= 0) {
            return new MortalityCycleResult(0, 0, 0, 0, 0.0, 0.0);
        }

        double averageHealth = 80.0;
        int deaths = calculateMortalityDeaths(population, baseDeathRate, averageHealth);
        int births = calculateBirthAdditions(population, baseBirthRate, averageHealth);

        int finalPopulation = Math.max(0, population - deaths + births);
        log.info("Agent mortality tick complete: start = {}, deaths = {}, births = {}, final = {}",
            population, deaths, births, finalPopulation);

        return new MortalityCycleResult(
            population,
            deaths,
            births,
            finalPopulation,
            population > 0 ? (double) deaths / population : 0.0,
            population > 0 ? (double) births / population : 0.0
        );
    }
}
