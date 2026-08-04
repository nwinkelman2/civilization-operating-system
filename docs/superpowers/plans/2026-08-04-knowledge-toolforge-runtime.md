# AI Knowledge, Toolforge & Pluggable Runtime Engine Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Implement AI Knowledge System (Issue #30), Toolforge Engine (Issue #29), and Pluggable Agent Brain Runtime (Issue #28).

---

### Task 1: AI Knowledge & Toolforge Engines (Issues #30 & #29)

**Files:**
- Create: `src/main/java/io/github/opencivilizationplatform/modules/life/domain/AgentSkill.java`
- Create: `src/main/java/io/github/opencivilizationplatform/modules/life/application/AgentKnowledgeService.java`
- Create: `src/test/java/io/github/opencivilizationplatform/modules/life/AgentKnowledgeServiceTest.java`
- Create: `src/main/java/io/github/opencivilizationplatform/modules/production/domain/CraftedTool.java`
- Create: `src/main/java/io/github/opencivilizationplatform/modules/production/application/ToolforgeService.java`
- Create: `src/test/java/io/github/opencivilizationplatform/modules/production/ToolforgeServiceTest.java`

- [ ] **Step 1: Create `AgentSkill.java` record and `AgentKnowledgeService.java` and unit tests**
- [ ] **Step 2: Create `CraftedTool.java` record and `ToolforgeService.java` and unit tests**
- [ ] **Step 3: Run `./mvnw test` to verify unit tests pass**
- [ ] **Step 4: Commit Task 1**

---

### Task 2: Pluggable Agent Brain Runtime & SimulationEngine Integration (Issue #28)

**Files:**
- Create: `src/main/java/io/github/opencivilizationplatform/modules/cortex/domain/AgentBrainDriver.java`
- Create: `src/main/java/io/github/opencivilizationplatform/modules/cortex/application/AgentBrainRuntimeService.java`
- Create: `src/test/java/io/github/opencivilizationplatform/modules/cortex/AgentBrainRuntimeServiceTest.java`
- Modify: `src/main/java/io/github/opencivilizationplatform/modules/simulation/application/SimulationEngineService.java`
- Modify: `src/test/java/io/github/opencivilizationplatform/modules/simulation/application/SimulationEngineServiceTest.java`

- [ ] **Step 1: Create `AgentBrainDriver.java` interface and `AgentBrainRuntimeService.java` and unit tests**
- [ ] **Step 2: Inject `AgentKnowledgeService`, `ToolforgeService`, and `AgentBrainRuntimeService` into `SimulationEngineService.java` with `@Autowired`**
- [ ] **Step 3: Run `./mvnw test` to verify 100% green build**
- [ ] **Step 4: Push branch `feat/knowledge-toolforge-runtime`, open PR #81, watch CI, and merge**
