package io.github.opencivilizationplatform.modules.strategy.domain;

public enum SocietalEra {
    AGRARIAN,
    INDUSTRIAL,
    INFORMATION,
    BIOSPHERE_HARMONY;

    public SocietalEra next() {
        return switch (this) {
            case AGRARIAN -> INDUSTRIAL;
            case INDUSTRIAL -> INFORMATION;
            case INFORMATION -> BIOSPHERE_HARMONY;
            case BIOSPHERE_HARMONY -> BIOSPHERE_HARMONY;
        };
    }
}
