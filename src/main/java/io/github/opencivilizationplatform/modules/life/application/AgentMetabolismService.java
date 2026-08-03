package io.github.opencivilizationplatform.modules.life.application;

import io.github.opencivilizationplatform.modules.contribution.domain.Citizen;
import io.github.opencivilizationplatform.modules.contribution.domain.Role;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AgentMetabolismService {

    private static final double BASE_METABOLIC_RATE = 1.0;
    private static final double FOOD_PER_METABOLIC_UNIT = 1.0;
    private static final double WATER_PER_METABOLIC_UNIT = 1.5;

    public record MetabolismResult(
        double foodConsumed,
        double waterConsumed,
        boolean isStarving,
        double starvationSeverity,
        int starvingCitizensCount
    ) {}

    public double calculateMetabolicRate(Citizen citizen) {
        if (citizen == null) {
            return BASE_METABOLIC_RATE;
        }
        Role role = citizen.getRole();
        if (role == null) {
            return BASE_METABOLIC_RATE;
        }
        return switch (role) {
            case FOUNDER -> 1.3;
            case NEXUS_COORDINATOR -> 1.2;
            case SECTOR_DELEGATE -> 1.1;
            case CITIZEN -> 1.0;
            default -> BASE_METABOLIC_RATE;
        };
    }

    public double calculateFoodRequirement(int citizenCount, double averageMetabolicRate) {
        return Math.max(0, citizenCount) * averageMetabolicRate * FOOD_PER_METABOLIC_UNIT;
    }

    public double calculateWaterRequirement(int citizenCount, double averageMetabolicRate) {
        return Math.max(0, citizenCount) * averageMetabolicRate * WATER_PER_METABOLIC_UNIT;
    }

    public MetabolismResult processMetabolism(int population, double foodSupply, double waterSupply) {
        if (population <= 0) {
            return new MetabolismResult(0.0, 0.0, false, 0.0, 0);
        }

        double totalFoodRequired = calculateFoodRequirement(population, BASE_METABOLIC_RATE);
        double totalWaterRequired = calculateWaterRequirement(population, BASE_METABOLIC_RATE);

        double foodConsumed = Math.min(foodSupply, totalFoodRequired);
        double waterConsumed = Math.min(waterSupply, totalWaterRequired);

        double foodDeficiency = totalFoodRequired > 0 ? Math.max(0.0, (totalFoodRequired - foodSupply) / totalFoodRequired) : 0.0;
        double waterDeficiency = totalWaterRequired > 0 ? Math.max(0.0, (totalWaterRequired - waterSupply) / totalWaterRequired) : 0.0;

        double starvationSeverity = Math.min(1.0, Math.max(foodDeficiency, waterDeficiency));
        boolean isStarving = starvationSeverity > 0.0;
        int starvingCitizensCount = isStarving ? (int) Math.ceil(population * starvationSeverity) : 0;

        return new MetabolismResult(foodConsumed, waterConsumed, isStarving, starvationSeverity, starvingCitizensCount);
    }

    public MetabolismResult processMetabolismForCitizens(List<Citizen> citizens, double foodSupply, double waterSupply) {
        if (citizens == null || citizens.isEmpty()) {
            return new MetabolismResult(0.0, 0.0, false, 0.0, 0);
        }

        double totalMetabolicUnits = citizens.stream()
            .mapToDouble(this::calculateMetabolicRate)
            .sum();

        double totalFoodRequired = totalMetabolicUnits * FOOD_PER_METABOLIC_UNIT;
        double totalWaterRequired = totalMetabolicUnits * WATER_PER_METABOLIC_UNIT;

        double foodConsumed = Math.min(foodSupply, totalFoodRequired);
        double waterConsumed = Math.min(waterSupply, totalWaterRequired);

        double foodDeficiency = totalFoodRequired > 0 ? Math.max(0.0, (totalFoodRequired - foodSupply) / totalFoodRequired) : 0.0;
        double waterDeficiency = totalWaterRequired > 0 ? Math.max(0.0, (totalWaterRequired - waterSupply) / totalWaterRequired) : 0.0;

        double starvationSeverity = Math.min(1.0, Math.max(foodDeficiency, waterDeficiency));
        boolean isStarving = starvationSeverity > 0.0;
        int starvingCitizensCount = isStarving ? (int) Math.ceil(citizens.size() * starvationSeverity) : 0;

        return new MetabolismResult(foodConsumed, waterConsumed, isStarving, starvationSeverity, starvingCitizensCount);
    }
}
