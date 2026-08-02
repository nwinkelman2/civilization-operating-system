package io.github.opencivilizationplatform.modules.technology.infrastructure;

import io.github.opencivilizationplatform.modules.technology.domain.Technology;
import io.github.opencivilizationplatform.modules.technology.domain.TechnologyCategory;
import io.github.opencivilizationplatform.modules.technology.domain.TechnologyStatus;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TechnologySeedData {

    private static final Logger log = LoggerFactory.getLogger(TechnologySeedData.class);
    private final TechnologyRepository repository;

    public TechnologySeedData(TechnologyRepository repository) {
        this.repository = repository;
    }

    @PostConstruct
    public void seed() {
        if (repository.count() > 0) {
            log.info("Technology table already populated, skipping seed");
            return;
        }

        String scale = System.getenv().getOrDefault("CIVILIZATION_SCALE", "LOCAL");

        List<Technology> techs = switch (scale) {
            case "GLOBAL" -> createGlobalTechs();
            case "CONTINENTAL" -> createContinentalTechs();
            case "REGIONAL" -> createRegionalTechs();
            default -> createLocalTechs();
        };

        repository.saveAll(techs);
        log.info("Seeded {} technologies for scale {}", techs.size(), scale);
    }

    private List<Technology> createLocalTechs() {
        return List.of(
            createTech("Basic Agriculture", TechnologyCategory.AGRICULTURE, 1, 50, "food", 0.2),
            createTech("Primitive Tools", TechnologyCategory.MILITARY, 1, 40, "minerals", 0.1),
            createTech("Shelter Building", TechnologyCategory.CULTURE, 1, 30, "housing", 0.3),
            createTech("Fire Mastery", TechnologyCategory.SCIENCE, 1, 35, "energy", 0.2),
            createTech("Water Wells", TechnologyCategory.AGRICULTURE, 1, 25, "water", 0.2)
        );
    }

    private List<Technology> createRegionalTechs() {
        return List.of(
            createTech("Advanced Agriculture", TechnologyCategory.AGRICULTURE, 2, 100, "food", 0.3),
            createTech("Bronze Working", TechnologyCategory.MILITARY, 2, 80, "minerals", 0.2),
            createTech("Town Planning", TechnologyCategory.CULTURE, 2, 70, "housing", 0.4),
            createTech("Mathematics", TechnologyCategory.SCIENCE, 2, 90, "energy", 0.3),
            createTech("Irrigation Systems", TechnologyCategory.AGRICULTURE, 2, 60, "water", 0.3),
            createTech("Basic Trade Routes", TechnologyCategory.SCIENCE, 2, 75, "food", 0.15, "minerals", 0.15)
        );
    }

    private List<Technology> createContinentalTechs() {
        return List.of(
            createTech("Industrial Farming", TechnologyCategory.AGRICULTURE, 3, 200, "food", 0.5),
            createTech("Steel Manufacturing", TechnologyCategory.MILITARY, 3, 180, "minerals", 0.4),
            createTech("Urban Development", TechnologyCategory.CULTURE, 3, 160, "housing", 0.6),
            createTech("Scientific Method", TechnologyCategory.SCIENCE, 3, 220, "energy", 0.4),
            createTech("Water Treatment", TechnologyCategory.AGRICULTURE, 3, 140, "water", 0.5),
            createTech("Rail Networks", TechnologyCategory.SCIENCE, 3, 200, "minerals", 0.3, "food", 0.2),
            createTech("Stock Markets", TechnologyCategory.CULTURE, 3, 190, "energy", 0.3, "housing", 0.2)
        );
    }

    private List<Technology> createGlobalTechs() {
        return List.of(
            createTech("Vertical Farming", TechnologyCategory.AGRICULTURE, 4, 400, "food", 0.8),
            createTech("Nanotechnology", TechnologyCategory.MILITARY, 4, 500, "minerals", 0.6),
            createTech("Arcologies", TechnologyCategory.CULTURE, 4, 450, "housing", 1.0),
            createTech("Fusion Power", TechnologyCategory.SCIENCE, 4, 600, "energy", 0.8),
            createTech("Atmospheric Processing", TechnologyCategory.AGRICULTURE, 4, 350, "water", 0.8),
            createTech("Global Logistics", TechnologyCategory.SCIENCE, 4, 500, "food", 0.5, "minerals", 0.5),
            createTech("Digital Currency", TechnologyCategory.CULTURE, 4, 400, "energy", 0.5, "housing", 0.5),
            createTech("Space Programs", TechnologyCategory.SCIENCE, 5, 1000, "energy", 1.0, "minerals", 1.0)
        );
    }

    private Technology createTech(String name, TechnologyCategory category, int tier,
                                   int researchCost, String resourceBonus, double bonusAmount) {
        Technology tech = new Technology();
        tech.setName(name);
        tech.setCategory(category);
        tech.setTier(tier);
        tech.setResearchCost(researchCost);
        tech.setStatus(TechnologyStatus.AVAILABLE);
        tech.setResourceBonus(resourceBonus);
        tech.setBonusAmount(bonusAmount);
        return tech;
    }

    private Technology createTech(String name, TechnologyCategory category, int tier,
                                   int researchCost, String resourceBonus1, double amount1,
                                   String resourceBonus2, double amount2) {
        Technology tech = new Technology();
        tech.setName(name);
        tech.setCategory(category);
        tech.setTier(tier);
        tech.setResearchCost(researchCost);
        tech.setStatus(TechnologyStatus.AVAILABLE);
        tech.setResourceBonus(resourceBonus1 + "," + resourceBonus2);
        tech.setBonusAmount(amount1 + amount2);
        return tech;
    }
}
