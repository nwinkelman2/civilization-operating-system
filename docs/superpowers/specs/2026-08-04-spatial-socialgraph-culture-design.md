# Spatial Map, Social Graph & Cultural Engine Design Specification (Issues #42, #40, #48)

## Objective
Implement three core backend simulation engines:
1. **Interactive Agent Spatial Map Engine (Issue #42)**: Real-time 2D spatial coordinate tracking (`x`, `y`), waypoint navigation, and proximity detection.
2. **Social Graph & Relationship Engine (Issue #40)**: Agent-to-agent relationship matrix (`trustScore`, `conflictIndex`, `socialBonds`).
3. **Culture & Art Engine (Issue #48)**: Generation of cultural artifacts, artistic styles, and regional cultural prestige.

## Component Architecture

### 1. Spatial Map Engine (`io.github.opencivilizationplatform.modules.region.application.AgentSpatialMapService`)
- Manages agent coordinates:
  - `AgentPosition(agentId, x, y, speed)`
  - Moving agents toward target waypoints.
  - Proximity calculations for inter-agent interaction.

### 2. Social Graph Service (`io.github.opencivilizationplatform.modules.social.application.SocialGraphService`)
- Relationship matrix:
  - `Relationship(sourceAgentId, targetAgentId, trustScore, rivalryScore)`
  - Updates trust scores on cooperation and conflict events.

### 3. Culture & Art Service (`io.github.opencivilizationplatform.modules.social.application.CultureArtService`)
- Cultural artifacts:
  - `CulturalArtifact(artifactId, creatorAgentId, title, era, prestigeValue)`
  - Evaluates cultural influence on citizen morale and civilization prestige.

## Verification Plan
1. Unit tests for `AgentSpatialMapServiceTest`, `SocialGraphServiceTest`, and `CultureArtServiceTest`.
2. `./mvnw test` ensuring 100% green build.
