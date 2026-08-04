# Spatial Map, Social Graph & Culture Engine Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Implement Interactive Agent Spatial Map Engine (Issue #42), Social Graph & Relationship Engine (Issue #40), and Culture & Art Engine (Issue #48).

---

### Task 1: Spatial Map & Social Graph Engines (Issues #42 & #40)

**Files:**
- Create: `src/main/java/io/github/opencivilizationplatform/modules/region/domain/AgentPosition.java`
- Create: `src/main/java/io/github/opencivilizationplatform/modules/region/application/AgentSpatialMapService.java`
- Create: `src/test/java/io/github/opencivilizationplatform/modules/region/AgentSpatialMapServiceTest.java`
- Create: `src/main/java/io/github/opencivilizationplatform/modules/social/domain/AgentRelationship.java`
- Create: `src/main/java/io/github/opencivilizationplatform/modules/social/application/SocialGraphService.java`
- Create: `src/test/java/io/github/opencivilizationplatform/modules/social/SocialGraphServiceTest.java`

- [ ] **Step 1: Create `AgentPosition.java` record and `AgentSpatialMapService.java` and unit tests**
- [ ] **Step 2: Create `AgentRelationship.java` record and `SocialGraphService.java` and unit tests**
- [ ] **Step 3: Run `./mvnw test` to verify unit tests pass**
- [ ] **Step 4: Commit Task 1**

---

### Task 2: Culture & Art Engine & SimulationEngine Integration (Issue #48)

**Files:**
- Create: `src/main/java/io/github/opencivilizationplatform/modules/social/domain/CulturalArtifact.java`
- Create: `src/main/java/io/github/opencivilizationplatform/modules/social/application/CultureArtService.java`
- Create: `src/test/java/io/github/opencivilizationplatform/modules/social/CultureArtServiceTest.java`
- Modify: `src/main/java/io/github/opencivilizationplatform/modules/simulation/application/SimulationEngineService.java`
- Modify: `src/test/java/io/github/opencivilizationplatform/modules/simulation/application/SimulationEngineServiceTest.java`

- [ ] **Step 1: Create `CulturalArtifact.java` record and `CultureArtService.java` and unit tests**
- [ ] **Step 2: Inject `AgentSpatialMapService`, `SocialGraphService`, and `CultureArtService` into `SimulationEngineService.java` with `@Autowired`**
- [ ] **Step 3: Run `./mvnw test` to verify 100% green build**
- [ ] **Step 4: Push branch `feat/spatial-socialgraph-culture`, open PR #82, watch CI, and merge**
