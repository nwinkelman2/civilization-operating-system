package io.github.opencivilizationplatform.modules.region.application;

import io.github.opencivilizationplatform.modules.region.domain.ResourceRegion;
import io.github.opencivilizationplatform.modules.region.infrastructure.ResourceRegionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class TerritoryControlService {

    private final ResourceRegionRepository repository;

    public TerritoryControlService(ResourceRegionRepository repository) {
        this.repository = repository;
    }

    public record TerritoryExpansionResult(
        Long regionId,
        String regionName,
        double oldRadiusKm,
        double newRadiusKm,
        double areaKm2,
        boolean expanded
    ) {}

    public boolean evaluateClaimEligibility(ResourceRegion region, Long civilizationId, double influenceScore, double minRequiredInfluence) {
        if (region == null || civilizationId == null) {
            return false;
        }
        if (Boolean.TRUE.equals(region.getClaimed()) && !civilizationId.equals(region.getClaimedByCivilizationId())) {
            return false; // Already claimed by another civilization
        }
        return influenceScore >= minRequiredInfluence;
    }

    @Transactional
    public ResourceRegion processClaimEvaluation(ResourceRegion region, Long civilizationId, double influenceScore, double minRequiredInfluence) {
        if (evaluateClaimEligibility(region, civilizationId, influenceScore, minRequiredInfluence)) {
            region.setClaimed(true);
            region.setClaimedByCivilizationId(civilizationId);
            if (repository != null) {
                return repository.save(region);
            }
        }
        return region;
    }

    public TerritoryExpansionResult calculateExpansion(ResourceRegion region, double growthRate, int currentPopulation) {
        if (region == null) {
            return new TerritoryExpansionResult(null, "Unknown", 0.0, 0.0, 0.0, false);
        }

        double currentRadius = region.getRadiusKm() != null ? region.getRadiusKm() : 10.0;
        if (growthRate <= 0.0 || currentPopulation <= 0) {
            double area = Math.PI * Math.pow(currentRadius, 2);
            return new TerritoryExpansionResult(region.getId(), region.getName(), currentRadius, currentRadius, area, false);
        }

        double populationFactor = Math.log10(Math.max(1, currentPopulation)) * 0.1;
        double radiusDelta = growthRate * (1.0 + populationFactor);
        double maxRadius = 1000.0;
        double newRadius = Math.min(maxRadius, currentRadius + radiusDelta);
        boolean expanded = newRadius > currentRadius;

        region.setRadiusKm(newRadius);
        double area = Math.PI * Math.pow(newRadius, 2);

        return new TerritoryExpansionResult(region.getId(), region.getName(), currentRadius, newRadius, area, expanded);
    }

    @Transactional
    public List<TerritoryExpansionResult> processTerritoryTick(double baseGrowthFactor) {
        List<TerritoryExpansionResult> results = new ArrayList<>();
        if (repository == null) {
            return results;
        }

        List<ResourceRegion> claimedRegions = repository.findAll().stream()
            .filter(r -> Boolean.TRUE.equals(r.getClaimed()))
            .toList();

        for (ResourceRegion region : claimedRegions) {
            TerritoryExpansionResult result = calculateExpansion(region, baseGrowthFactor, 100);
            repository.save(region);
            results.add(result);
        }
        return results;
    }
}
