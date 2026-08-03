# Pure API Backend & High-Priority Domain Engine Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Remove legacy Java SSR PageController and Thymeleaf templates, transforming backend into a pure API server, and implement core backend simulation services (Metabolism, Territory Control, Law Execution).

---

### Task 1: Remove Legacy SSR Frontend & Pure API Transition

**Files:**
- Delete: `src/main/java/io/github/opencivilizationplatform/web/controller/PageController.java`
- Delete: `src/main/resources/templates/`
- Modify: `pom.xml` (remove/update thymeleaf if needed)
- Modify: `src/main/java/io/github/opencivilizationplatform/config/SecurityConfig.java`

- [ ] **Step 1: Remove `PageController.java` and delete `src/main/resources/templates/`**
- [ ] **Step 2: Update `SecurityConfig.java` to pure REST/GraphQL/WebSocket API config**
- [ ] **Step 3: Run `./mvnw test-compile` to verify clean Java compilation**
- [ ] **Step 4: Commit Task 1**

---

### Task 2: Implement High-Priority Backend Domain Engines (Metabolism, Territory, Law Enactment)

**Files:**
- Create: `src/main/java/io/github/opencivilizationplatform/modules/life/application/AgentMetabolismService.java`
- Create: `src/test/java/io/github/opencivilizationplatform/modules/life/AgentMetabolismServiceTest.java`
- Create: `src/main/java/io/github/opencivilizationplatform/modules/region/application/TerritoryControlService.java`
- Create: `src/test/java/io/github/opencivilizationplatform/modules/region/TerritoryControlServiceTest.java`
- Create: `src/main/java/io/github/opencivilizationplatform/modules/participation/application/LawExecutionEngine.java`
- Create: `src/test/java/io/github/opencivilizationplatform/modules/participation/LawExecutionEngineTest.java`
- Modify: `src/main/java/io/github/opencivilizationplatform/modules/simulation/application/SimulationEngineService.java`

- [ ] **Step 1: Create `AgentMetabolismService.java` and unit tests**
- [ ] **Step 2: Create `TerritoryControlService.java` and unit tests**
- [ ] **Step 3: Create `LawExecutionEngine.java` and unit tests**
- [ ] **Step 4: Integrate new services into `SimulationEngineService.java`**
- [ ] **Step 5: Run `./mvnw test` to verify 100% green pass**
- [ ] **Step 6: Push branch `feat/backend-pure-api-engines`, open PR #76, watch CI, and merge**
