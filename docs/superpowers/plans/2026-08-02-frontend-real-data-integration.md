# Frontend Real Data Integration Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Connect all frontend pages to real backend GraphQL endpoints and REST APIs, eliminating mock data while adding loading states, error fallbacks, and empty state UI handling (Issue #2).

**Architecture:** Extend `queries.ts` with GraphQL queries for regions, resources, shipments, global events, projects, incidents, and balance reports. Update React page components (`ResourceMap.tsx`, `Logistics.tsx`, `Social.tsx`, `Trade.tsx`, `Constitution.tsx`, `TechTree.tsx`, `Production.tsx`) to fetch real data via GraphQL or REST.

**Tech Stack:** React 19, TypeScript, Apollo Client, Vite, TailwindCSS / Vanilla CSS.

## Global Constraints
- Target frontend directory: `frontend/src/`
- GraphQL client: Apollo `@apollo/client` (`useQuery`, `useMutation`)
- REST client: `fetch('/api/v1/...')` with JSON parsing

---

### Task 1: Expand GraphQL Queries in `queries.ts`

**Files:**
- Modify: `frontend/src/graphql/queries.ts`

- [ ] **Step 1: Add new GraphQL queries to `queries.ts`**
  - Add `GET_REGIONS`, `GET_RESOURCES`, `GET_SHIPMENTS`, `GET_GLOBAL_EVENTS`, `GET_PROJECTS`, `GET_INCIDENTS`, `GET_BALANCE_REPORT`.

- [ ] **Step 2: Verify TypeScript build**
  - Check frontend build or syntax validity.

- [ ] **Step 3: Commit query updates**
  - `git add frontend/src/graphql/queries.ts`
  - `git commit -m "feat(graphql): add queries for regions, resources, shipments, events, projects, and incidents"`

---

### Task 2: Connect Pages (`ResourceMap`, `Logistics`, `Social`, `Trade`, `Constitution`, `TechTree`, `Production`)

**Files:**
- Modify: `frontend/src/pages/ResourceMap.tsx`
- Modify: `frontend/src/pages/Logistics.tsx`
- Modify: `frontend/src/pages/Social.tsx`
- Modify: `frontend/src/pages/Trade.tsx`
- Modify: `frontend/src/pages/Constitution.tsx`
- Modify: `frontend/src/pages/TechTree.tsx`
- Modify: `frontend/src/pages/Production.tsx`

- [ ] **Step 1: Update `ResourceMap.tsx`**
  - Use `GET_REGIONS` and `GET_RESOURCES` GraphQL queries to replace static regions/resources array.

- [ ] **Step 2: Update `Logistics.tsx`**
  - Use `GET_SHIPMENTS` GraphQL query to render live logistics shipments.

- [ ] **Step 3: Update `Social.tsx`**
  - Use `GET_INCIDENTS` and `GET_PROJECTS` GraphQL queries to render live social incidents and contribution projects.

- [ ] **Step 4: Update `Trade.tsx`**
  - Fetch `/api/v1/trade/agreements` to render real trade agreements.

- [ ] **Step 5: Update `Constitution.tsx`**
  - Fetch `/api/v1/rules` to render real constitutional rules.

- [ ] **Step 6: Update `TechTree.tsx`**
  - Fetch `/api/v1/technologies` to render real technology nodes.

- [ ] **Step 7: Update `Production.tsx`**
  - Fetch `/api/v1/production/facilities` (or `/api/v1/facilities`) to render real production facilities.

- [ ] **Step 8: Verify frontend build**
  - Run build check in `frontend/`.

- [ ] **Step 9: Commit page integrations**
  - `git add frontend/src/pages/`
  - `git commit -m "feat(frontend): connect all pages to real GraphQL and REST backend endpoints"`

---

### Task 3: Full Verification, Feature Branch, Push, and PR Creation

- [ ] **Step 1: Verify backend compilation and tests**
  - `./mvnw test-compile` and `./mvnw test`

- [ ] **Step 2: Create feature branch, push, and open PR via `gh`**
  - `git checkout -b feat/frontend-real-data-integration-issue-2`
  - `git push -u origin feat/frontend-real-data-integration-issue-2`
  - `gh pr create --title "feat(frontend): integrate all pages with real GraphQL/REST backend endpoints (#2)" --body "Closes #2" --head feat/frontend-real-data-integration-issue-2 --base main`
