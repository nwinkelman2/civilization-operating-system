package io.github.opencivilizationplatform.modules.physics.application;

import io.github.opencivilizationplatform.modules.physics.domain.Season;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicReference;

@Service
public class ClimateDisasterService {

    private final AtomicReference<Season> currentSeason = new AtomicReference<>(Season.SPRING);

    public record DisasterEvent(
        String disasterType,
        double severity,
        boolean occurred
    ) {}

    public record ClimateCycleResult(
        Season season,
        double yieldMultiplier,
        DisasterEvent disasterEvent
    ) {}

    public Season getCurrentSeason() {
        return currentSeason.get();
    }

    public synchronized Season advanceSeason() {
        Season next = currentSeason.get().next();
        currentSeason.set(next);
        return next;
    }

    public double calculateResourceYieldMultiplier(Season season) {
        if (season == null) {
            return 1.0;
        }
        return switch (season) {
            case SPRING -> 1.2;
            case SUMMER -> 1.5;
            case AUTUMN -> 1.0;
            case WINTER -> 0.5;
        };
    }

    public double calculateResourceYieldMultiplier() {
        return calculateResourceYieldMultiplier(getCurrentSeason());
    }

    public DisasterEvent triggerDisasterEvent(Season season, double globalTemperature) {
        Season targetSeason = season != null ? season : getCurrentSeason();

        if (targetSeason == Season.SUMMER && globalTemperature > 35.0) {
            double severity = Math.min(1.0, (globalTemperature - 35.0) * 0.1 + 0.3);
            return new DisasterEvent("HEATWAVE", severity, true);
        } else if (targetSeason == Season.WINTER && globalTemperature < -10.0) {
            double severity = Math.min(1.0, Math.abs(globalTemperature - (-10.0)) * 0.08 + 0.3);
            return new DisasterEvent("BLIZZARD", severity, true);
        } else if (targetSeason == Season.AUTUMN && globalTemperature > 25.0) {
            return new DisasterEvent("HURRICANE", 0.6, true);
        } else if (targetSeason == Season.SPRING && globalTemperature > 20.0) {
            return new DisasterEvent("FLOOD", 0.4, true);
        }

        return new DisasterEvent("NONE", 0.0, false);
    }

    public ClimateCycleResult processClimateCycle(double globalTemperature) {
        Season season = advanceSeason();
        double multiplier = calculateResourceYieldMultiplier(season);
        DisasterEvent disaster = triggerDisasterEvent(season, globalTemperature);
        return new ClimateCycleResult(season, multiplier, disaster);
    }
}
