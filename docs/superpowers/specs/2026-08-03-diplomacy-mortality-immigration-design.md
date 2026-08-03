# AI Diplomacy, Mortality & Immigration Engine Design Specification (Issues #38, #37, #35)

## Objective
Implement core backend engines for Diplomacy & Conflict Resolution (Issue #38), Agent Lifecycle & Mortality (Issue #37), and AI Immigration & Merit Selection (Issue #35).

## Component Architecture

### 1. AI Diplomacy & Conflict Engine (`io.github.opencivilizationplatform.modules.diplomacy.application.DiplomacyEngineService`)
- Manages bilateral relations between civilizations:
  - Relation status: `NEUTRAL`, `ALLIED`, `NON_AGGRESSION_PACT`, `HOSTILE`, `WAR`.
  - Calculates tension index and processes peace/alliance proposals.

### 2. Agent Lifecycle & Mortality Engine (`io.github.opencivilizationplatform.modules.life.application.AgentMortalityService`)
- Manages population demographics:
  - Age progression per simulation cycle.
  - Mortality evaluation based on age, health index, and starvation state.
  - Birth rate calculation based on housing and food abundance.

### 3. AI Immigration Service (`io.github.opencivilizationplatform.modules.social.application.ImmigrationService`)
- Evaluates citizen migration preferences between civilizations:
  - Calculates civilization attractiveness score (satisfaction + food/housing availability + stability).
  - Migrates citizens from low-satisfaction to high-satisfaction civilizations.

## Verification Plan
1. Backend unit tests for `DiplomacyEngineServiceTest`, `AgentMortalityServiceTest`, and `ImmigrationServiceTest`.
2. Run `./mvnw test` ensuring 100% green pass.
