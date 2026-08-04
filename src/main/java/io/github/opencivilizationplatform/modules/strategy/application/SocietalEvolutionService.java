package io.github.opencivilizationplatform.modules.strategy.application;

import io.github.opencivilizationplatform.modules.strategy.domain.SocietalEra;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicReference;

@Service
public class SocietalEvolutionService {

    private final AtomicReference<SocietalEra> currentEra = new AtomicReference<>(SocietalEra.AGRARIAN);

    public record EvolutionResult(
        SocietalEra currentEra,
        SocietalEra targetEra,
        boolean eraAdvanced,
        String reason
    ) {}

    public SocietalEra getCurrentEra() {
        return currentEra.get();
    }

    public void setCurrentEra(SocietalEra era) {
        if (era != null) {
            this.currentEra.set(era);
        }
    }

    public boolean canAdvanceEra(SocietalEra era, int techUnlocks, double resourceUsage) {
        if (era == null) {
            return false;
        }
        return switch (era) {
            case AGRARIAN -> techUnlocks >= 5 && resourceUsage >= 100.0;
            case INDUSTRIAL -> techUnlocks >= 15 && resourceUsage >= 500.0;
            case INFORMATION -> techUnlocks >= 30 && resourceUsage >= 1000.0;
            case BIOSPHERE_HARMONY -> false;
        };
    }

    public synchronized EvolutionResult evaluateEraProgression(int techUnlocks, double resourceUsage) {
        SocietalEra era = currentEra.get();
        if (era == SocietalEra.BIOSPHERE_HARMONY) {
            return new EvolutionResult(era, era, false, "Already at pinnacle era: BIOSPHERE_HARMONY");
        }

        if (canAdvanceEra(era, techUnlocks, resourceUsage)) {
            SocietalEra nextEra = era.next();
            currentEra.set(nextEra);
            String reason = String.format("Advanced from %s to %s with %d tech unlocks and %.1f resource usage.",
                era, nextEra, techUnlocks, resourceUsage);
            return new EvolutionResult(era, nextEra, true, reason);
        }

        String reason = String.format("Requirements not met for advancement from %s (Tech: %d, Usage: %.1f).",
            era, techUnlocks, resourceUsage);
        return new EvolutionResult(era, era, false, reason);
    }

    public EvolutionResult processEvolutionCycle(int techUnlocks, double resourceUsage) {
        return evaluateEraProgression(techUnlocks, resourceUsage);
    }
}
