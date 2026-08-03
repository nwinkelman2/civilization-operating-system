# Pure API Backend & High-Priority Domain Engine Design Specification

## Objective
1. Consolidate the Spring Boot backend into a pure API server (GraphQL, REST, WebSockets), removing legacy Thymeleaf SSR views (`PageController.java` & `src/main/resources/templates/`).
2. Implement key high-priority backend simulation engines:
   - **Metabolism & Energy Engine (`AgentMetabolismService.java`)**: Calculates metabolic rates, caloric consumption, and starvation flags for citizens (Issue #54).
   - **Territory & Land Control Expansion (`TerritoryControlService.java`)**: Dynamic region border claim algorithms based on civilization population density and resource strength (Issue #55).
   - **Dynamic Law Enactment Engine (`LawExecutionEngine.java`)**: Enforces rule execution triggers during simulation ticks (Issue #45).

## Component Design

### 1. Remove Legacy Thymeleaf Views
- Delete `src/main/java/io/github/opencivilizationplatform/web/controller/PageController.java`.
- Delete `src/main/resources/templates/`.
- Remove Thymeleaf dependency if present or clean up SecurityConfig.

### 2. Citizen Metabolism Service (`AgentMetabolismService.java`)
- `processMetabolism(List<Citizen> citizens)`:
  - Consumes calories (food/water) per simulation tick based on activity level.
  - Updates health score and sets starvation flags when resources are depleted.

### 3. Territory Control Service (`TerritoryControlService.java`)
- `evaluateTerritoryExpansion(Long civilizationId)`:
  - Calculates expansion eligibility based on population density, infrastructure facilities, and neighboring unallocated resource regions.

### 4. Dynamic Law Execution Engine (`LawExecutionEngine.java`)
- `evaluateActiveRules(Long civilizationId)`:
  - Evaluates active constitutional rules against current biosphere metrics and triggers balance adjustments.

## Verification Plan
1. Backend unit test suite (`./mvnw test`).
2. Verify zero Thymeleaf compilation errors.
