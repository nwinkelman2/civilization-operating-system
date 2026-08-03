# AI Diplomacy, Mortality & Immigration Engine Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Implement Diplomacy Engine (Issue #38), Agent Mortality Engine (Issue #37), and Immigration Service (Issue #35).

---

### Task 1: AI Diplomacy Engine (Issue #38)

**Files:**
- Create: `src/main/java/io/github/opencivilizationplatform/modules/diplomacy/domain/DiplomaticRelation.java`
- Create: `src/main/java/io/github/opencivilizationplatform/modules/diplomacy/application/DiplomacyEngineService.java`
- Create: `src/test/java/io/github/opencivilizationplatform/modules/diplomacy/DiplomacyEngineServiceTest.java`

- [ ] **Step 1: Create `DiplomaticRelation.java` and `DiplomacyEngineService.java`**
- [ ] **Step 2: Create `DiplomacyEngineServiceTest.java` and verify with `./mvnw test`**
- [ ] **Step 3: Commit Task 1**

---

### Task 2: Agent Mortality & Immigration Services (Issues #37 & #35)

**Files:**
- Create: `src/main/java/io/github/opencivilizationplatform/modules/life/application/AgentMortalityService.java`
- Create: `src/test/java/io/github/opencivilizationplatform/modules/life/AgentMortalityServiceTest.java`
- Create: `src/main/java/io/github/opencivilizationplatform/modules/social/application/ImmigrationService.java`
- Create: `src/test/java/io/github/opencivilizationplatform/modules/social/ImmigrationServiceTest.java`
- Modify: `src/main/java/io/github/opencivilizationplatform/modules/simulation/application/SimulationEngineService.java`

- [ ] **Step 1: Create `AgentMortalityService.java` and unit tests**
- [ ] **Step 2: Create `ImmigrationService.java` and unit tests**
- [ ] **Step 3: Integrate into `SimulationEngineService.java` and verify with `./mvnw test`**
- [ ] **Step 4: Push branch `feat/diplomacy-mortality-immigration`, open PR #78, watch CI, and merge**
