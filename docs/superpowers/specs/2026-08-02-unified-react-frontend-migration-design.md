# Unified React Frontend Feature Migration & Responsiveness Design Specification

## Objective
Port and enhance rich UI components and features from the Java Thymeleaf templates (`src/main/resources/templates/`) into the unified React SPA (`frontend/`), establishing a fully responsive, mobile-first design system.

## Proposed Component & Feature Enhancements

### 1. Interactive Nexus Mesh Topology Graph (`frontend/src/components/NexusMeshGraph.tsx`)
- Render nodes as interactive SVG canvas nodes with connection links, latency badges (ms), signal strength indicators, and pulse animations for live WebSocket messages.

### 2. Biosphere Metric Gauges & Simulation Controls (`frontend/src/components/BiosphereGauges.tsx` & `Simulation.tsx`)
- Visual metric gauges for Air Quality (AQI), Water pH, Soil Fertility, and Biosphere Industrial Drift.
- Simulation engine control bar with Play, Pause, Single Step, and Tick Rate controls.

### 3. Trade Proposal Modal & Sliders (`frontend/src/components/TradeProposalModal.tsx`)
- Interactive modal for proposing inter-civilization trades with resource drop-downs, quantity sliders, and trade bot automation toggle.

### 4. Constitutional Rule Proposal & Validation Meters (`frontend/src/components/RuleProposalModal.tsx`)
- Modal for submittal of new constitutional rules, scientific validation score meters, and community voting progress bars.

### 5. Civilization Detail View & History Timeline (`frontend/src/pages/CivilizationDetail.tsx`)
- Dedicated view for individual civilization stats, population growth charts (Recharts), citizen roster, and historical event timeline.

### 6. Population Needs & Community Purpose (`frontend/src/components/NeedsProgress.tsx`)
- Visual progress bars for Population Needs (Housing, Food, Water, Energy) and Community Purpose projects.

### 7. Responsive Mobile Shell (`frontend/src/components/Layout.tsx` & `Sidebar.tsx`)
- Add Hamburger drawer overlay for mobile screens (< 768px) and flexible CSS Grid breakpoints for all pages.

## Verification Plan
1. Test TypeScript compilation inside `frontend/`.
2. Run Playwright E2E suite (`npx playwright test`) to verify all new components and responsive layouts pass 100%.
