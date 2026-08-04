# Societal Evolution, Resource Stewardship & Exploration Engine Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Implement Societal Evolution Engine (Issue #34), Resource Stewardship Engine (Issue #33), and Exploration & Colony Service (Issue #32).

---

### Task 1: Societal Evolution & Resource Stewardship Engines (Issues #34 & #33)

**Files:**
- Create: `src/main/java/io/github/opencivilizationplatform/modules/strategy/domain/SocietalEra.java`
- Create: `src/main/java/io/github/opencivilizationplatform/modules/strategy/application/SocietalEvolutionService.java`
- Create: `src/test/java/io/github/opencivilizationplatform/modules/strategy/SocietalEvolutionServiceTest.java`
- Create: `src/main/java/io/github/opencivilizationplatform/modules/physics/application/ResourceStewardshipService.java`
- Create: `src/test/java/io/github/opencivilizationplatform/modules/physics/ResourceStewardshipServiceTest.java`

- [ ] **Step 1: Create `SocietalEra.java` enum and `SocietalEvolutionService.java` and unit tests**
- [ ] **Step 2: Create `ResourceStewardshipService.java` and unit tests**
- [ ] **Step 3: Run `./mvnw test` to verify unit tests pass**
- [ ] **Step 4: Commit Task 1**

---

### Task 2: Exploration & Colony Service & SimulationEngine Integration (Issue #32)

**Files:**
- Create: `src/main/java/io/github/opencivilizationplatform/modules/region/domain/ExplorationExpedition.java`
- Create: `src/main/java/io/github/opencivilizationplatform/modules/region/application/ExplorationColonyService.java`
- Create: `src/test/java/io/github/opencivilizationplatform/modules/region/ExplorationColonyServiceTest.java`
- Modify: `src/main/java/io/github/opencivilizationplatform/modules/simulation/application/SimulationEngineService.java`
- Modify: `src/test/java/io/github/opencivilizationplatform/modules/simulation/application/SimulationEngineServiceTest.java`

- [ ] **Step 1: Create `ExplorationExpedition.java` record and `ExplorationColonyService.java` and unit tests**
- [ ] **Step 2: Inject `SocietalEvolutionService`, `ResourceStewardshipService`, and `ExplorationColonyService` into `SimulationEngineService.java` with `@Autowired`**
- [ ] **Step 3: Run `./mvnw test` to verify 100% green build**
- [ ] **Step 4: Push branch `feat/evolution-sustainability-exploration`, open PR #80, watch CI, and merge**
