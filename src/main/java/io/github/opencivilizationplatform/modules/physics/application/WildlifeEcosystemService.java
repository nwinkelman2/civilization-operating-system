package io.github.opencivilizationplatform.modules.physics.application;

import io.github.opencivilizationplatform.modules.physics.domain.FaunaSpecies;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class WildlifeEcosystemService {

    private static final Logger log = LoggerFactory.getLogger(WildlifeEcosystemService.class);
    private final Map<String, FaunaSpecies> speciesMap = new ConcurrentHashMap<>();

    public FaunaSpecies registerSpecies(String speciesName, long population, double reproductionRate) {
        if (speciesName == null || speciesName.isBlank()) {
            return null;
        }
        FaunaSpecies species = new FaunaSpecies(speciesName, Math.max(0, population), Math.max(0.0, reproductionRate));
        speciesMap.put(speciesName, species);
        return species;
    }

    public FaunaSpecies registerSpecies(FaunaSpecies species) {
        if (species == null || species.speciesName() == null || species.speciesName().isBlank()) {
            return null;
        }
        speciesMap.put(species.speciesName(), species);
        return species;
    }

    public Optional<FaunaSpecies> getSpecies(String speciesName) {
        if (speciesName == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(speciesMap.get(speciesName));
    }

    public List<FaunaSpecies> getAllSpecies() {
        return new ArrayList<>(speciesMap.values());
    }

    public double calculateBiodiversityIndex() {
        if (speciesMap.isEmpty()) {
            return 0.0;
        }
        long totalPopulation = speciesMap.values().stream()
                .mapToLong(FaunaSpecies::population)
                .sum();
        if (totalPopulation == 0) {
            return 0.0;
        }

        double shannonIndex = 0.0;
        for (FaunaSpecies species : speciesMap.values()) {
            if (species.population() > 0) {
                double p = (double) species.population() / totalPopulation;
                shannonIndex -= p * Math.log(p);
            }
        }

        double maxIndex = Math.log(Math.max(1, speciesMap.size()));
        if (maxIndex == 0.0) {
            return speciesMap.size() > 0 ? 100.0 : 0.0;
        }
        double index = (shannonIndex / maxIndex) * 100.0;
        return Math.min(100.0, Math.max(0.0, index));
    }

    public double calculateFoodWebStability() {
        if (speciesMap.isEmpty()) {
            return 0.0;
        }
        long totalPopulation = speciesMap.values().stream()
                .mapToLong(FaunaSpecies::population)
                .sum();
        if (totalPopulation == 0) {
            return 0.0;
        }

        double avgReproductionRate = speciesMap.values().stream()
                .mapToDouble(FaunaSpecies::reproductionRate)
                .average().orElse(0.0);
        double activeSpeciesRatio = (double) speciesMap.values().stream().filter(s -> s.population() > 0).count() / speciesMap.size();

        double stability = (activeSpeciesRatio * 0.6 + Math.min(1.0, avgReproductionRate) * 0.4) * 100.0;
        return Math.min(100.0, Math.max(0.0, stability));
    }

    public void processEcosystemTick() {
        double biodiversity = calculateBiodiversityIndex();
        double stability = calculateFoodWebStability();
        log.info("[WILDLIFE ECOSYSTEM TICK] Species Count: {}, Biodiversity Index: {}, Food Web Stability: {}",
                speciesMap.size(), String.format("%.2f", biodiversity), String.format("%.2f", stability));
    }
}
