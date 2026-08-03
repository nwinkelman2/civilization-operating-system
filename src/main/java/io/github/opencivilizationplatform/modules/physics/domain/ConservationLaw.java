package io.github.opencivilizationplatform.modules.physics.domain;

public final class ConservationLaw {

    private static final double DEFAULT_TOLERANCE = 1e-6;

    private ConservationLaw() {
        // Utility class constructor
    }

    public static boolean verifyConservation(double initialTotal, double finalTotal) {
        return verifyConservation(initialTotal, finalTotal, DEFAULT_TOLERANCE);
    }

    public static boolean verifyConservation(double initialTotal, double finalTotal, double tolerance) {
        return Math.abs(initialTotal - finalTotal) <= tolerance;
    }

    public static boolean verifyEnergyMassConservation(double inputEnergy, double inputMass, double outputEnergy, double outputMass) {
        return verifyEnergyMassConservation(inputEnergy, inputMass, outputEnergy, outputMass, DEFAULT_TOLERANCE);
    }

    public static boolean verifyEnergyMassConservation(double inputEnergy, double inputMass, double outputEnergy, double outputMass, double tolerance) {
        double inputTotal = inputEnergy + inputMass;
        double outputTotal = outputEnergy + outputMass;
        return verifyConservation(inputTotal, outputTotal, tolerance);
    }

    public static void enforceConservation(double initialTotal, double finalTotal) {
        if (!verifyConservation(initialTotal, finalTotal)) {
            throw new IllegalStateException(
                String.format("Conservation law violated! Initial: %.6f, Final: %.6f", initialTotal, finalTotal)
            );
        }
    }
}
