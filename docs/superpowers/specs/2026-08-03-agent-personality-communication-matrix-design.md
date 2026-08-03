# Agent Personality, Communication & Config Matrix Design Specification (Issues #52, #44, #39)

## Objective
Implement core backend engines for Agent Personality/Memory (Issue #39), Agent Communication Protocol (Issue #44), and Civilization Configuration Matrix (Issue #52).

## Component Architecture

### 1. AI Personality & Episodic Memory (`io.github.opencivilizationplatform.modules.life.domain.AgentPersonality`)
- **Personality Vectors**:
  - `cooperationIndex` (0.0 - 1.0)
  - `riskTolerance` (0.0 - 1.0)
  - `innovationFocus` (0.0 - 1.0)
- **Episodic Memory Log**:
  - `List<EpisodicMemoryEvent>` recording key agent experiences (successful trade, resource scarcity, rule enforcement).

### 2. Agent Communication Protocol (`io.github.opencivilizationplatform.modules.nexus.application.AgentCommunicationService`)
- Manages structured agent-to-agent dialogs:
  - Dialogue intent classification (`TRADE_NEGOTIATION`, `ALLIANCE_PROPOSAL`, `KNOWLEDGE_SHARE`).
  - Broadcasts communication events via Kafka topic `civos.nexus.message_sent`.

### 3. Civilization Configuration Matrix (`io.github.opencivilizationplatform.modules.civilization.domain.CivilizationConfigMatrix`)
- Configuration matrix determining civilization baseline parameters:
  - `resourceAllocationPriority` (`BALANCED`, `GROWTH`, `DEFENSE`, `RESEARCH`)
  - `taxRate` (0.0 - 0.5)
  - `autonomyLevel` (0.0 - 1.0)

## Verification Plan
1. Backend unit tests for `AgentPersonalityService`, `AgentCommunicationService`, and `CivilizationConfigMatrixService`.
2. `./mvnw test` verifying 100% green build.
