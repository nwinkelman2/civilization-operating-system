package io.github.opencivilizationplatform.modules.production.application;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ComplexGoodsProductionService {

    public record ProductionResult(
        double steelProduced,
        double toolsProduced,
        double electronicsProduced,
        Map<String, Double> remainingResources
    ) {}

    public double calculateMaxSteel(double ironOre, double coal) {
        if (ironOre <= 0 || coal <= 0) return 0.0;
        return Math.min(ironOre / 2.0, coal / 1.0);
    }

    public double calculateMaxTools(double steel) {
        if (steel <= 0) return 0.0;
        return steel / 2.0;
    }

    public double calculateMaxElectronics(double steel, double silicon, double copper) {
        if (steel <= 0 || silicon <= 0 || copper <= 0) return 0.0;
        return Math.min(steel / 1.0, Math.min(silicon / 2.0, copper / 2.0));
    }

    public ProductionResult processProductionCycle(Map<String, Double> availableResources) {
        Map<String, Double> inventory = new HashMap<>();
        if (availableResources != null) {
            availableResources.forEach((k, v) -> inventory.put(k.toUpperCase(), Math.max(0.0, v)));
        }

        double ironOre = inventory.getOrDefault("IRON_ORE", 0.0);
        double coal = inventory.getOrDefault("COAL", 0.0);
        double silicon = inventory.getOrDefault("SILICON", 0.0);
        double copper = inventory.getOrDefault("COPPER", 0.0);
        double initialSteel = inventory.getOrDefault("STEEL", 0.0);

        // Tier 1: Convert IRON_ORE & COAL to STEEL
        double newSteel = calculateMaxSteel(ironOre, coal);
        ironOre -= newSteel * 2.0;
        coal -= newSteel * 1.0;
        double totalSteelAvailable = initialSteel + newSteel;

        // Tier 2 & Tier 3: Convert STEEL + components to TOOLS and ELECTRONICS
        // Distribute steel evenly between tools and electronics if both can be made
        double steelForElectronics = Math.min(totalSteelAvailable / 2.0, calculateMaxElectronics(totalSteelAvailable, silicon, copper));
        double electronicsProduced = calculateMaxElectronics(steelForElectronics, silicon, copper);

        double remainingSteelAfterElectronics = totalSteelAvailable - (electronicsProduced * 1.0);
        silicon -= electronicsProduced * 2.0;
        copper -= electronicsProduced * 2.0;

        double toolsProduced = calculateMaxTools(remainingSteelAfterElectronics);
        double remainingSteelFinal = remainingSteelAfterElectronics - (toolsProduced * 2.0);

        inventory.put("IRON_ORE", Math.max(0.0, ironOre));
        inventory.put("COAL", Math.max(0.0, coal));
        inventory.put("SILICON", Math.max(0.0, silicon));
        inventory.put("COPPER", Math.max(0.0, copper));
        inventory.put("STEEL", Math.max(0.0, remainingSteelFinal));
        inventory.put("TOOLS", inventory.getOrDefault("TOOLS", 0.0) + toolsProduced);
        inventory.put("ELECTRONICS", inventory.getOrDefault("ELECTRONICS", 0.0) + electronicsProduced);

        return new ProductionResult(newSteel, toolsProduced, electronicsProduced, inventory);
    }
}
