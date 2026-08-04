# Plugin SDK, Tourism & Monetary Economy Engine Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Implement Plugin SDK (Issue #57), Tourism & Pilgrimage Engine (Issue #51), and Monetary Economy Engine (Issue #49).

---

### Task 1: Plugin SDK & Tourism Engines (Issues #57 & #51)

**Files:**
- Create: `src/main/java/io/github/opencivilizationplatform/modules/nexus/domain/CivilizationPlugin.java`
- Create: `src/main/java/io/github/opencivilizationplatform/modules/nexus/application/PluginSdkService.java`
- Create: `src/test/java/io/github/opencivilizationplatform/modules/nexus/PluginSdkServiceTest.java`
- Create: `src/main/java/io/github/opencivilizationplatform/modules/social/domain/Landmark.java`
- Create: `src/main/java/io/github/opencivilizationplatform/modules/social/application/TourismPilgrimageService.java`
- Create: `src/test/java/io/github/opencivilizationplatform/modules/social/TourismPilgrimageServiceTest.java`

- [ ] **Step 1: Create `CivilizationPlugin.java` record and `PluginSdkService.java` and unit tests**
- [ ] **Step 2: Create `Landmark.java` record and `TourismPilgrimageService.java` and unit tests**
- [ ] **Step 3: Run `./mvnw test` to verify unit tests pass**
- [ ] **Step 4: Commit Task 1**

---

### Task 2: Monetary Economy Engine & SimulationEngine Integration (Issue #49)

**Files:**
- Create: `src/main/java/io/github/opencivilizationplatform/modules/trade/application/MonetaryEconomyService.java`
- Create: `src/test/java/io/github/opencivilizationplatform/modules/trade/MonetaryEconomyServiceTest.java`
- Modify: `src/main/java/io/github/opencivilizationplatform/modules/simulation/application/SimulationEngineService.java`
- Modify: `src/test/java/io/github/opencivilizationplatform/modules/simulation/application/SimulationEngineServiceTest.java`

- [ ] **Step 1: Create `MonetaryEconomyService.java` and unit tests**
- [ ] **Step 2: Inject `PluginSdkService`, `TourismPilgrimageService`, and `MonetaryEconomyService` into `SimulationEngineService.java` with `@Autowired`**
- [ ] **Step 3: Run `./mvnw test` to verify 100% green build**
- [ ] **Step 4: Push branch `feat/plugin-tourism-monetary`, open PR #84, watch CI, and merge**
