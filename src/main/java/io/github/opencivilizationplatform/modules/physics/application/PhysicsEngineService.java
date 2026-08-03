package io.github.opencivilizationplatform.modules.physics.application;

import io.github.opencivilizationplatform.modules.physics.domain.ConservationLaw;
import org.springframework.stereotype.Service;

@Service
public class PhysicsEngineService {

    public double applyPhysicalDecay(double initialAmount, double decayRate) {
        if (decayRate < 0.0 || decayRate > 1.0) {
            throw new IllegalArgumentException("Decay rate must be between 0.0 and 1.0");
        }
        if (initialAmount < 0.0) {
            throw new IllegalArgumentException("Initial amount cannot be negative");
        }
        return initialAmount * (1.0 - decayRate);
    }

    public double applyIndustrialDrift(double baseEfficiency, double driftRate) {
        if (driftRate < 0.0) {
            throw new IllegalArgumentException("Drift rate cannot be negative");
        }
        double adjusted = baseEfficiency - driftRate;
        return Math.max(0.0, adjusted);
    }

    public boolean verifyConservation(double initialTotal, double finalTotal) {
        return ConservationLaw.verifyConservation(initialTotal, finalTotal);
    }

    public void enforceConservation(double initialTotal, double finalTotal) {
        ConservationLaw.enforceConservation(initialTotal, finalTotal);
    }
}
