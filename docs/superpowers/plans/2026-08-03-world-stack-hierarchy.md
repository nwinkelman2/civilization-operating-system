# World Stack Hierarchical Alignment Implementation Plan (Issue #71)

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Implement Universe, Physics, and Life domain packages and align simulation engine rules according to the 4-tier World Stack hierarchy.

---

### Task 1: Create Universe & Physics Domain Packages

**Files:**
- Create: `src/main/java/io/github/opencivilizationplatform/modules/universe/domain/UniverseConfig.java`
- Create: `src/main/java/io/github/opencivilizationplatform/modules/universe/application/UniverseService.java`
- Create: `src/main/java/io/github/opencivilizationplatform/modules/physics/domain/ConservationLaw.java`
- Create: `src/main/java/io/github/opencivilizationplatform/modules/physics/application/PhysicsEngineService.java`
- Create: `src/test/java/io/github/opencivilizationplatform/modules/physics/PhysicsEngineServiceTest.java`

- [ ] **Step 1: Create `UniverseConfig.java` and `UniverseService.java`**
- [ ] **Step 2: Create `ConservationLaw.java` and `PhysicsEngineService.java`**
- [ ] **Step 3: Create `PhysicsEngineServiceTest.java` and verify with `./mvnw test`**
- [ ] **Step 4: Commit Task 1**

---

### Task 2: Integrate World Stack into Simulation Engine & Feature Branch/PR

**Files:**
- Modify: `src/main/java/io/github/opencivilizationplatform/modules/simulation/application/SimulationEngineService.java`
- Create: `src/test/java/io/github/opencivilizationplatform/modules/universe/UniverseServiceTest.java`

- [ ] **Step 1: Inject `UniverseService` and `PhysicsEngineService` into `SimulationEngineService`**
- [ ] **Step 2: Run full backend test suite (`./mvnw test`)**
- [ ] **Step 3: Push branch `feat/world-stack-hierarchy`, open PR #74, watch CI, and merge**
