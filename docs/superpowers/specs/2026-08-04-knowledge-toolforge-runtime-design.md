# AI Knowledge, Toolforge & Pluggable Runtime Engine Design Specification (Issues #30, #29, #28)

## Objective
Implement three core backend simulation engines:
1. **AI Knowledge & Education System (Issue #30)**: Manages citizen skill progression, knowledge bases, and educational efficiency.
2. **AI Toolforge Engine (Issue #29)**: Allows agents to craft custom tools (`HARVESTING_RIG`, `NEXUS_TRANSMITTER`, `SOLAR_CONVERTER`) boosting productivity.
3. **Pluggable Agent Brain Runtime (Issue #28)**: Provides an extensible `AgentBrainDriver` interface supporting `RULE_BASED`, `LLM_PROMPT`, and `HYBRID` brain drivers.

## Component Architecture

### 1. AI Knowledge Service (`io.github.opencivilizationplatform.modules.life.application.AgentKnowledgeService`)
- Tracks citizen skill levels (`AGRICULTURE`, `ENGINEERING`, `GOVERNANCE`, `PHYSICS`).
- Processes learning ticks and knowledge sharing between citizens in educational facilities.

### 2. AI Toolforge Service (`io.github.opencivilizationplatform.modules.production.application.ToolforgeService`)
- Manages agent tool crafting recipes:
  - Requires raw materials (Steel, Energy, Minerals).
  - Produces equipment items increasing regional resource extraction rates.

### 3. Pluggable Agent Brain Runtime (`io.github.opencivilizationplatform.modules.cortex.application.AgentBrainRuntimeService`)
- Extensible `AgentBrainDriver` strategy pattern:
  - `RuleBasedBrainDriver`: Deterministic heuristic decision-making.
  - `LlmPromptBrainDriver`: Formats prompt context for external LLM inference.
  - `HybridBrainDriver`: Combines heuristics with LLM fallback.

## Verification Plan
1. Unit tests for `AgentKnowledgeServiceTest`, `ToolforgeServiceTest`, and `AgentBrainRuntimeServiceTest`.
2. `./mvnw test` ensuring 100% green build.
