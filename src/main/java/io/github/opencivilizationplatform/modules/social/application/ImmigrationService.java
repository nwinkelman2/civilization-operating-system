package io.github.opencivilizationplatform.modules.social.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ImmigrationService {

    private static final Logger log = LoggerFactory.getLogger(ImmigrationService.class);

    public record CivilizationProfile(
        String civId,
        double prosperityIndex,
        double safetyIndex,
        double freedomIndex,
        double resourceAvailability,
        int population
    ) {}

    public record MigrationResult(
        String sourceCivId,
        String targetCivId,
        int migratedCitizens,
        double sourceAttraction,
        double targetAttraction,
        double attractionDelta
    ) {}

    public double calculateAttractionIndex(double prosperity, double safety, double freedom, double resourceAvailability) {
        double rawAttraction = (prosperity * 0.35) + (safety * 0.25) + (freedom * 0.20) + (resourceAvailability * 0.20);
        return Math.max(0.0, Math.min(100.0, rawAttraction));
    }

    public double calculateAttractionIndex(CivilizationProfile profile) {
        if (profile == null) return 0.0;
        return calculateAttractionIndex(
            profile.prosperityIndex(),
            profile.safetyIndex(),
            profile.freedomIndex(),
            profile.resourceAvailability()
        );
    }

    public MigrationResult evaluateMigration(CivilizationProfile sourceCiv, CivilizationProfile targetCiv) {
        if (sourceCiv == null || targetCiv == null || sourceCiv.population() <= 0) {
            return new MigrationResult(
                sourceCiv != null ? sourceCiv.civId() : "unknown",
                targetCiv != null ? targetCiv.civId() : "unknown",
                0, 0.0, 0.0, 0.0
            );
        }

        double sourceAttr = calculateAttractionIndex(sourceCiv);
        double targetAttr = calculateAttractionIndex(targetCiv);
        double delta = targetAttr - sourceAttr;

        int migrated = 0;
        if (delta > 5.0) {
            double migrationRate = Math.min(0.15, (delta / 100.0) * 0.2);
            migrated = (int) Math.round(sourceCiv.population() * migrationRate);
        }

        log.info("Migration evaluation from {} (attr={}) to {} (attr={}): delta={}, migrated={}",
            sourceCiv.civId(), sourceAttr, targetCiv.civId(), targetAttr, delta, migrated);

        return new MigrationResult(
            sourceCiv.civId(),
            targetCiv.civId(),
            migrated,
            sourceAttr,
            targetAttr,
            delta
        );
    }

    public MigrationResult processMigrationCycle(double targetAttraction, double sourceAttraction, int sourcePopulation) {
        double delta = targetAttraction - sourceAttraction;
        int migrated = 0;
        if (delta > 5.0 && sourcePopulation > 0) {
            double migrationRate = Math.min(0.15, (delta / 100.0) * 0.2);
            migrated = (int) Math.round(sourcePopulation * migrationRate);
        }
        return new MigrationResult("source", "target", migrated, sourceAttraction, targetAttraction, delta);
    }
}
