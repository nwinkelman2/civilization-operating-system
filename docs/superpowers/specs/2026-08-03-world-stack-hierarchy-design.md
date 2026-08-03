# World Stack Hierarchical Alignment Design Specification (Issue #71)

## Objective
Refactor domain layers and simulation engine to strictly enforce a 4-tier World Stack hierarchy:
`Universe -> Physics -> Life -> Civilization`.

## Hierarchy & Rule Enforcements

1. **Tier 1: Universe Layer (`io.github.opencivilizationplatform.modules.universe`)**:
   - Manages global universe parameters: `cycleDurationMs`, `maxWorldEnergyCap`, `entropyDecayRate`.
   - Immutable to lower layers (Physics, Life, Civilization).

2. **Tier 2: Physics Layer (`io.github.opencivilizationplatform.modules.physics`)**:
   - Enforces conservation laws: total energy, water, and food within resource regions cannot be created ex-nihilo.
   - Calculates resource degradation and industrial carbon drift.

3. **Tier 3: Life Layer (`io.github.opencivilizationplatform.modules.life`)**:
   - Manages citizen metabolism, housing satisfaction, health indices, and population growth curves.

4. **Tier 4: Civilization Layer (`io.github.opencivilizationplatform.modules.civilization`)**:
   - Emergent layer: Trade agreements, governance rules, research trees, and Nexus node communications.

## Verification Plan
1. Create unit & integration tests verifying lower layer immutability (`UniverseServiceTest`, `PhysicsConservationTest`).
2. Run `./mvnw test` to ensure 100% green backend tests.
