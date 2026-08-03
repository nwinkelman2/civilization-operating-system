# Unified React Frontend Feature Migration & Responsiveness Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Port rich UI features from Java Thymeleaf templates into the React SPA, implementing interactive SVG mesh topology, biosphere gauges, trade proposal modal, rule creation modal, civilization detail view, population needs bars, and mobile responsiveness.

**Architecture:** Create reusable React components (`NexusMeshGraph.tsx`, `BiosphereGauges.tsx`, `TradeProposalModal.tsx`, `RuleProposalModal.tsx`, `NeedsProgress.tsx`) and pages (`CivilizationDetail.tsx`), integrating them into existing React routes while ensuring mobile responsiveness via CSS drawer and flex/grid breakpoints.

---

### Task 1: Responsive Shell & Drawer Menu (`Layout.tsx`, `Sidebar.tsx`, `index.css`)

**Files:**
- Modify: `frontend/src/components/Layout.tsx`
- Modify: `frontend/src/components/Sidebar.tsx`
- Modify: `frontend/src/index.css`

- [ ] **Step 1: Update `Sidebar.tsx` and `Layout.tsx` for mobile drawer toggle**
  - Add mobile hamburger button in `Layout.tsx` header for screens < 768px.
  - Implement mobile overlay drawer toggle state.

- [ ] **Step 2: Add CSS breakpoints for mobile responsiveness in `index.css`**
  - Ensure `.dashboard-grid` and tables render gracefully on mobile.

- [ ] **Step 3: Commit mobile shell improvements**
  - `git add frontend/src/components/ frontend/src/index.css`
  - `git commit -m "feat(frontend): implement mobile drawer and responsive grid breakpoints"`

---

### Task 2: Interactive SVG Nexus Mesh Graph & Biosphere Gauges

**Files:**
- Create: `frontend/src/components/NexusMeshGraph.tsx`
- Create: `frontend/src/components/BiosphereGauges.tsx`
- Modify: `frontend/src/pages/NexusMesh.tsx`
- Modify: `frontend/src/pages/Simulation.tsx`

- [ ] **Step 1: Build `NexusMeshGraph.tsx`**
  - SVG node graph showing active nodes, connection lines, latency badges, and signal strength.

- [ ] **Step 2: Integrate `NexusMeshGraph.tsx` into `NexusMesh.tsx`**

- [ ] **Step 3: Build `BiosphereGauges.tsx`**
  - Metric gauges for AQI, pH, Soil Fertility, and Carbon Drift.

- [ ] **Step 4: Integrate `BiosphereGauges.tsx` into `Simulation.tsx`**

- [ ] **Step 5: Commit mesh graph & biosphere gauges**
  - `git add frontend/src/components/ frontend/src/pages/`
  - `git commit -m "feat(frontend): add SVG Nexus mesh graph and biosphere metric gauges"`

---

### Task 3: Interactive Modals & Civilization Detail View

**Files:**
- Create: `frontend/src/components/TradeProposalModal.tsx`
- Create: `frontend/src/components/RuleProposalModal.tsx`
- Create: `frontend/src/components/NeedsProgress.tsx`
- Create: `frontend/src/pages/CivilizationDetail.tsx`
- Modify: `frontend/src/pages/Trade.tsx`
- Modify: `frontend/src/pages/Constitution.tsx`
- Modify: `frontend/src/App.tsx`

- [ ] **Step 1: Create `TradeProposalModal.tsx` and integrate into `Trade.tsx`**
  - Modal with resource selector, quantity slider, and POST `/api/v1/trade/propose` submission.

- [ ] **Step 2: Create `RuleProposalModal.tsx` and integrate into `Constitution.tsx`**
  - Modal to propose rules with scientific validation meter.

- [ ] **Step 3: Create `NeedsProgress.tsx` for population satisfaction**

- [ ] **Step 4: Create `CivilizationDetail.tsx` page and route `/civilizations/:id` in `App.tsx`**
  - Detailed view with history timeline, population growth chart, and territory status.

- [ ] **Step 5: Verify Playwright E2E test suite**
  - Run `npx playwright test` inside `frontend/`.

- [ ] **Step 6: Commit interactive components and civilization detail view**
  - `git add frontend/`
  - `git commit -m "feat(frontend): add trade proposal modal, rule proposal modal, needs progress, and civilization detail view"`

---

### Task 4: Full E2E Verification, Feature Branch, Push, and PR Creation

- [ ] **Step 1: Run full Playwright test suite**
  - `npx playwright test` inside `frontend/`

- [ ] **Step 2: Create feature branch, push, and open PR via `gh`**
  - `git checkout -b feat/unified-frontend-feature-migration`
  - `git push -u origin feat/unified-frontend-feature-migration`
  - `gh pr create --title "feat(frontend): port Java UI features, interactive graph, modals & mobile responsiveness" --body "Unifies frontend into React SPA with mobile drawer, SVG mesh graph, biosphere gauges, proposal modals, and civilization detail view" --head feat/unified-frontend-feature-migration --base main`
