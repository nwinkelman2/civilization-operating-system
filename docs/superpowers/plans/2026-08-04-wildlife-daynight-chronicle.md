# Wildlife Ecosystem, Day/Night Cycle & History Chronicle Engine Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Implement Wildlife & Ecosystem Engine (Issue #61), Day/Night Cycle Engine (Issue #56), and Civilization Chronicle Engine (Issue #41).

---

### Task 1: Wildlife & Day/Night Cycle Engines (Issues #61 & #56)

**Files:**
- Create: `src/main/java/io/github/opencivilizationplatform/modules/physics/domain/FaunaSpecies.java`
- Create: `src/main/java/io/github/opencivilizationplatform/modules/physics/application/WildlifeEcosystemService.java`
- Create: `src/test/java/io/github/opencivilizationplatform/modules/physics/WildlifeEcosystemServiceTest.java`
- Create: `src/main/java/io/github/opencivilizationplatform/modules/physics/application/DayNightCycleService.java`
- Create: `src/test/java/io/github/opencivilizationplatform/modules/physics/DayNightCycleServiceTest.java`

- [ ] **Step 1: Create `FaunaSpecies.java` record and `WildlifeEcosystemService.java` and unit tests**
- [ ] **Step 2: Create `DayNightCycleService.java` and unit tests**
- [ ] **Step 3: Run `./mvnw test` to verify unit tests pass**
- [ ] **Step 4: Commit Task 1**

---

### Task 2: Civilization Chronicle Engine & SimulationEngine Integration (Issue #41)

**Files:**
- Create: `src/main/java/io/github/opencivilizationplatform/modules/social/domain/ChronicleEntry.java`
- Create: `src/main/java/io/github/opencivilizationplatform/modules/social/application/CivilizationChronicleService.java`
- Create: `src/test/java/io/github/opencivilizationplatform/modules/social/CivilizationChronicleServiceTest.java`
- Modify: `src/main/java/io/github/opencivilizationplatform/modules/simulation/application/SimulationEngineService.java`
- Modify: `src/test/java/io/github/opencivilizationplatform/modules/simulation/application/SimulationEngineServiceTest.java`

- [ ] **Step 1: Create `ChronicleEntry.java` record and `CivilizationChronicleService.java` and unit tests**
- [ ] **Step 2: Inject `WildlifeEcosystemService`, `DayNightCycleService`, and `CivilizationChronicleService` into `SimulationEngineService.java` with `@Autowired`**
- [ ] **Step 3: Run `./mvnw test` to verify 100% green build**
- [ ] **Step 4: Push branch `feat/wildlife-daynight-chronicle`, open PR #83, watch CI, and merge**
