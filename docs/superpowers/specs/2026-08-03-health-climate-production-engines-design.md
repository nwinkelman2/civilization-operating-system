# Health, Climate & Production Chains Engine Design Specification (Issues #46, #47, #60)

## Objective
Implement three core backend engines:
1. **Health, Disease & Medicine Engine (Issue #46)**: Epidemic propagation, contagion calculation, citizen immunity, and medical infrastructure.
2. **Climate, Seasons & Natural Disasters Engine (Issue #47)**: Seasonal cycles (Spring, Summer, Autumn, Winter), resource output modifiers, and random natural disaster events.
3. **Production Chains & Complex Goods Engine (Issue #60)**: Multi-tier production chains converting raw minerals/energy into processed goods (Steel, Tools, Electronics).

## Component Architecture

### 1. Health & Disease Engine (`io.github.opencivilizationplatform.modules.life.application.HealthDiseaseService`)
- Tracks civilization outbreak status:
  - Infection rate per tick based on AQI and population density.
  - Cures and mortality reduction provided by medical facilities.

### 2. Climate & Disasters Engine (`io.github.opencivilizationplatform.modules.physics.application.ClimateDisasterService`)
- Manages global season ticks:
  - Season cycle: `SPRING` -> `SUMMER` -> `AUTUMN` -> `WINTER`.
  - Resource yield multipliers (e.g. Winter reduces food yield by 50%).
  - Triggers random natural disaster events (`DROUGHT`, `SOLAR_FLARE`, `EARTHQUAKE`).

### 3. Production Chains Engine (`io.github.opencivilizationplatform.modules.production.application.ComplexGoodsProductionService`)
- Processes multi-tier resource conversions:
  - Raw minerals + energy -> `STEEL`
  - Steel + minerals -> `TOOLS`
  - Energy + minerals -> `ELECTRONICS`

## Verification Plan
1. Unit tests for `HealthDiseaseServiceTest`, `ClimateDisasterServiceTest`, and `ComplexGoodsProductionServiceTest`.
2. `./mvnw test` ensuring 100% green build.
