# Health, Climate & Production Chains Engine Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Implement Health & Disease Engine (Issue #46), Climate & Seasons Engine (Issue #47), and Complex Goods Production Service (Issue #60).

---

### Task 1: Health & Disease Engine (Issue #46)

**Files:**
- Create: `src/main/java/io/github/opencivilizationplatform/modules/life/domain/EpidemicStatus.java`
- Create: `src/main/java/io/github/opencivilizationplatform/modules/life/application/HealthDiseaseService.java`
- Create: `src/test/java/io/github/opencivilizationplatform/modules/life/HealthDiseaseServiceTest.java`

- [ ] **Step 1: Create `EpidemicStatus.java` record and `HealthDiseaseService.java`**
- [ ] **Step 2: Create `HealthDiseaseServiceTest.java` and verify with `./mvnw test`**
- [ ] **Step 3: Commit Task 1**

---

### Task 2: Climate & Disasters Engine & Complex Goods Production (Issues #47 & #60)

**Files:**
- Create: `src/main/java/io/github/opencivilizationplatform/modules/physics/domain/Season.java`
- Create: `src/main/java/io/github/opencivilizationplatform/modules/physics/application/ClimateDisasterService.java`
- Create: `src/test/java/io/github/opencivilizationplatform/modules/physics/ClimateDisasterServiceTest.java`
- Create: `src/main/java/io/github/opencivilizationplatform/modules/production/application/ComplexGoodsProductionService.java`
- Create: `src/test/java/io/github/opencivilizationplatform/modules/production/ComplexGoodsProductionServiceTest.java`
- Modify: `src/main/java/io/github/opencivilizationplatform/modules/simulation/application/SimulationEngineService.java`

- [ ] **Step 1: Create `Season.java` enum and `ClimateDisasterService.java` and unit tests**
- [ ] **Step 2: Create `ComplexGoodsProductionService.java` and unit tests**
- [ ] **Step 3: Inject into `SimulationEngineService.java` and verify with `./mvnw test`**
- [ ] **Step 4: Push branch `feat/health-climate-production-engines`, open PR #79, watch CI, and merge**
