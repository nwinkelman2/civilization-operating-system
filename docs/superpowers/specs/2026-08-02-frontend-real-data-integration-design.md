# Frontend Real Data Integration Design Specification (Issue #2)

## Objective
Connect all frontend pages to real backend GraphQL endpoints and REST APIs, eliminating static mock data while adding loading states, error fallbacks, empty states, and real-time updates.

## Proposed Changes

### 1. GraphQL Queries Expansion (`frontend/src/graphql/queries.ts`)
Add the following queries:
- `GET_REGIONS` (`regions(claimed: Boolean)`)
- `GET_RESOURCES` (`resources(region: String)`)
- `GET_SHIPMENTS` (`shipments(civilizationId: ID, status: ShipmentStatus)`)
- `GET_GLOBAL_EVENTS` (`globalEvents(activeOnly: Boolean)`)
- `GET_PROJECTS` (`projects(civilizationId: ID, category: ProjectCategory)`)
- `GET_INCIDENTS` (`incidents(civilizationId: ID)`)
- `GET_BALANCE_REPORT` (`balanceReport`)

### 2. Page Integrations (`frontend/src/pages/`)
- **`Dashboard.tsx`**: Add WebSocket real-time subscription for `RESOURCE_TICK` to dynamically refresh civilization resources.
- **`Trade.tsx`**: Fetch trade agreements via REST `/api/v1/trade/agreements` or GraphQL mutation `proposeTrade`.
- **`Constitution.tsx`**: Fetch constitutional rules via `/api/v1/rules` and committees via `/api/v1/governance/committees`.
- **`TechTree.tsx`**: Fetch tech tree data via `/api/v1/technologies`.
- **`Production.tsx`**: Fetch manufacturing & production facilities via `/api/v1/facilities`.
- **`Logistics.tsx`**: Use `GET_SHIPMENTS` GraphQL query.
- **`Social.tsx`**: Use `GET_INCIDENTS` and `GET_PROJECTS` GraphQL queries.
- **`ResourceMap.tsx`**: Use `GET_REGIONS` and `GET_RESOURCES` GraphQL queries.

### 3. UI/UX Polish
- Ensure loading spinners/skeletons render while data is fetching.
- Ensure informative error messages and empty state UI components render gracefully when data is empty or API fails.

## Verification Plan
1. Test TypeScript compilation inside `frontend/`.
2. Run `./mvnw test` to ensure zero regressions on backend controllers.
