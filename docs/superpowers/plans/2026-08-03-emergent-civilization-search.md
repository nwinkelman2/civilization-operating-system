# Emergent Civilization Search Engine Implementation Plan (Issue #72)

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Create `EmergentCivilizationSearchService` backend analytics, expose GraphQL `@QueryMapping emergentArchetypes`, add frontend query, and render Emergent Archetypes visualization on Dashboard.

---

### Task 1: Backend Analytics Service & GraphQL Schema Expansion

**Files:**
- Create: `src/main/java/io/github/opencivilizationplatform/modules/strategy/domain/EmergentArchetypeReport.java`
- Create: `src/main/java/io/github/opencivilizationplatform/modules/strategy/application/EmergentCivilizationSearchService.java`
- Create: `src/test/java/io/github/opencivilizationplatform/modules/strategy/EmergentCivilizationSearchServiceTest.java`
- Modify: `src/main/resources/graphql/schema.graphqls`
- Modify: `src/main/java/io/github/opencivilizationplatform/graphql/CivilizationGraphQLController.java`

- [ ] **Step 1: Create `EmergentArchetypeReport.java` record and `EmergentCivilizationSearchService.java`**
- [ ] **Step 2: Add `emergentArchetypes` to `schema.graphqls` and `@QueryMapping` in `CivilizationGraphQLController.java`**
- [ ] **Step 3: Create `EmergentCivilizationSearchServiceTest.java` and verify with `./mvnw test`**
- [ ] **Step 4: Commit Task 1**

---

### Task 2: Frontend Integration & Playwright E2E Verification

**Files:**
- Modify: `frontend/src/graphql/queries.ts`
- Modify: `frontend/src/pages/Dashboard.tsx`
- Modify: `frontend/e2e/pages.spec.ts`

- [ ] **Step 1: Add `GET_EMERGENT_ARCHETYPES` to `queries.ts`**
- [ ] **Step 2: Render Emergent Archetypes section in `Dashboard.tsx`**
- [ ] **Step 3: Run Vitest & Playwright E2E tests (`npx playwright test`)**
- [ ] **Step 4: Push branch `feat/emergent-search-engine`, open PR #75, watch CI, and merge**
