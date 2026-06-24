package io.github.opencivilizationplatform.modules.cortex.cortex;

import io.github.opencivilizationplatform.modules.civilization.domain.Civilization;
import io.github.opencivilizationplatform.modules.civilization.infrastructure.CivilizationRepository;
import io.github.opencivilizationplatform.modules.cortex.domain.ResourceTick;
import io.github.opencivilizationplatform.modules.region.domain.ResourceRegion;
import io.github.opencivilizationplatform.modules.region.infrastructure.ResourceRegionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;

@Service
public class CortexEngineService {

    private static final Logger log = LoggerFactory.getLogger(CortexEngineService.class);

    private final CivilizationRepository civilizationRepository;
    private final ResourceRegionRepository resourceRegionRepository;
    private final Random random = new Random();

    public CortexEngineService(CivilizationRepository civilizationRepository,
                               ResourceRegionRepository resourceRegionRepository) {
        this.civilizationRepository = civilizationRepository;
        this.resourceRegionRepository = resourceRegionRepository;
    }

    @Scheduled(fixedRateString = "${cortex.engine.tick-rate-ms:30000}")
    @Transactional
    public void tick() {
        List<Civilization> civilizations = civilizationRepository.findAll();
        if (civilizations.isEmpty()) return;

        for (Civilization civ : civilizations) {
            ResourceTick tick = computeTick(civ);
            applyTick(civ, tick);
        }

        civilizationRepository.saveAll(civilizations);
        log.debug("Cortex tick completed for {} civilizations", civilizations.size());
    }

    @Transactional
    public void tickForCivilization(Long civilizationId) {
        civilizationRepository.findById(civilizationId).ifPresent(civ -> {
            ResourceTick tick = computeTick(civ);
            applyTick(civ, tick);
            civilizationRepository.save(civ);
        });
    }

    private ResourceTick computeTick(Civilization civ) {
        double[] resourceDelta = new double[]{0, 0, 0, 0, 0};
        double populationDelta = 0, reputationDelta = 0;

        if (civ.getHomeRegionId() != null) {
            resourceRegionRepository.findById(civ.getHomeRegionId()).ifPresent(region -> {
                resourceDelta[0] += region.getFoodAvailability() * 0.5;
                resourceDelta[1] += region.getWaterAvailability() * 0.5;
                resourceDelta[2] += region.getMineralAvailability() * 0.5;
                resourceDelta[3] += region.getEnergyAvailability() * 0.5;
                resourceDelta[4] += region.getHousingAvailability() * 0.5;
            });
        }

        double population = civ.getPopulation() != null ? civ.getPopulation() : 0;
        double consumptionRate = 1.0 + (population / 100.0);
        resourceDelta[0] -= consumptionRate * 0.3;
        resourceDelta[1] -= consumptionRate * 0.2;
        resourceDelta[3] -= consumptionRate * 0.1;

        resourceDelta[0] += random.nextDouble() * 2 - 1;
        resourceDelta[1] += random.nextDouble() * 1.5 - 0.75;
        resourceDelta[2] += random.nextDouble() * 1 - 0.5;
        resourceDelta[3] += random.nextDouble() * 1.5 - 0.75;
        resourceDelta[4] += random.nextDouble() * 0.5 - 0.25;

        double currentFood = civ.getFood() != null ? civ.getFood() : 100;
        double currentWater = civ.getWater() != null ? civ.getWater() : 100;

        if (currentFood > 0 && currentWater > 0) {
            populationDelta = 0.1 + random.nextDouble() * 0.2;
        } else {
            populationDelta = -0.5 - random.nextDouble() * 0.5;
        }

        if (currentFood <= 5 || currentWater <= 5) {
            reputationDelta -= 2.0;
        } else if (currentFood > 50 && currentWater > 50) {
            reputationDelta += 0.5;
        }

        return new ResourceTick(
            civ.getId(), resourceDelta[0], resourceDelta[1], resourceDelta[2],
            resourceDelta[3], resourceDelta[4], populationDelta, reputationDelta
        );
    }

    private void applyTick(Civilization civ, ResourceTick tick) {
        civ.setFood(clamp(civ.getFood() + tick.foodDelta(), 0, 9999));
        civ.setWater(clamp(civ.getWater() + tick.waterDelta(), 0, 9999));
        civ.setMinerals(clamp(civ.getMinerals() + tick.mineralsDelta(), 0, 9999));
        civ.setEnergy(clamp(civ.getEnergy() + tick.energyDelta(), 0, 9999));
        civ.setHousing(clamp(civ.getHousing() + tick.housingDelta(), 0, 9999));
        civ.setPopulation((int) Math.max(0, (civ.getPopulation() != null ? civ.getPopulation() : 10) + tick.populationDelta()));
        civ.setReputationScore(clamp(
            (civ.getReputationScore() != null ? civ.getReputationScore() : 50) + tick.reputationDelta(),
            0, 100
        ));
    }

    private double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
}
