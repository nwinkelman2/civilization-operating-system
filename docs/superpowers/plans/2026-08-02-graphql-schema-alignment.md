# GraphQL Schema Alignment (Issue #11) Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Align the GraphQL schema (`schema.graphqls`) and controller (`CivilizationGraphQLController`) with the renamed `Nexus` domain entities and newly added backend modules (`logistics`, `events`, `contribution`, `governance`/`nexus`, `social`), resolving all Java compilation errors.

**Architecture:** Refactor `schema.graphqls` to rename legacy `Voxtex` references to `Nexus` and add new types/queries for new domain modules. Update `CivilizationGraphQLController` to inject module services and implement `@QueryMapping`/`@MutationMapping` methods. Fix legacy import references in saga, web socket, and worker applications.

**Tech Stack:** Java 25, Spring Boot 3 / Spring GraphQL, GraphQL Java, Apollo Client (frontend), Vitest, JUnit 5.

## Global Constraints
- Target Java source version: Java 25.
- Package root: `io.github.opencivilizationplatform`.
- Schema path: `src/main/resources/graphql/schema.graphqls`.
- Controller path: `src/main/java/io/github/opencivilizationplatform/graphql/CivilizationGraphQLController.java`.

---

### Task 1: Resolve Broken Java Imports & Fix Compilation

**Files:**
- Modify: `src/main/java/io/github/opencivilizationplatform/graphql/CivilizationGraphQLController.java`
- Modify: `src/main/java/io/github/opencivilizationplatform/saga/FoundCivilizationSagaSteps.java`
- Modify: `src/main/java/io/github/opencivilizationplatform/config/WebSocketConfig.java`
- Modify: `src/main/java/io/github/opencivilizationplatform/modules/nexus/application/NexusMeshService.java`
- Modify: `src/main/java/io/github/opencivilizationplatform/worker/CortexWorkerApplication.java`

**Interfaces:**
- Consumes: `io.github.opencivilizationplatform.modules.nexus.application.NexusMeshService`, `io.github.opencivilizationplatform.modules.nexus.domain.NexusNodeType`
- Produces: Clean Java compilation across all backend packages.

- [ ] **Step 1: Fix `CivilizationGraphQLController.java` imports**

Replace imports of `modules.voxtex.*` with `modules.nexus.*`:
```java
import io.github.opencivilizationplatform.modules.nexus.application.NexusMeshService;
import io.github.opencivilizationplatform.modules.nexus.domain.NexusNodeType;
```
Update fields and constructor parameters from `VoxtexMeshService` to `NexusMeshService`.

- [ ] **Step 2: Fix `FoundCivilizationSagaSteps.java` imports**

Replace imports of `modules.voxtex.*` with `modules.nexus.*`:
```java
import io.github.opencivilizationplatform.modules.nexus.application.NexusMeshService;
import io.github.opencivilizationplatform.modules.nexus.domain.NexusNodeType;
```
Update method references from `VoxtexMeshService` and `VoxtexNodeType` to `NexusMeshService` and `NexusNodeType`.

- [ ] **Step 3: Fix `WebSocketConfig.java` references**

Update `WebSocketConfig.java` to import `NexusWebSocketHandler` instead of `VoxtexWebSocketHandler`.

- [ ] **Step 4: Fix `NexusMeshService.java` and `CortexWorkerApplication.java`**

In `NexusMeshService.java`, remove any remaining `voxtex` package imports and update to `nexus`.
In `CortexWorkerApplication.java`, add:
```java
import org.springframework.boot.autoconfigure.domain.EntityScan;
```

- [ ] **Step 5: Run compilation check**

Run: `./mvnw test-compile`
Expected: `BUILD SUCCESS` (0 compilation errors).

- [ ] **Step 6: Commit**

```bash
git add src/main/java/
git commit -m "fix(backend): update legacy voxtex imports to nexus and fix compilation"
```

---

### Task 2: Align GraphQL Schema (`schema.graphqls`)

**Files:**
- Modify: `src/main/resources/graphql/schema.graphqls`

**Interfaces:**
- Consumes: JPA Entity models across `nexus`, `logistics`, `events`, `contribution`, `governance`, `social`.
- Produces: Valid Spring GraphQL schema matching backend entities.

- [ ] **Step 1: Update `schema.graphqls` with Nexus and New Types**

Replace `VoxtexNode` and `VoxtexMessage` definitions with `NexusNode` and `NexusMessage`.
Add `Shipment`, `GlobalEvent`, `Project`, `Election`, `Treaty`, `Incident` types and enums.

```graphql
type Query {
    civilizations(page: Int, size: Int): CivilizationPage!
    civilization(id: ID!): Civilization
    nexusNodes(civilizationId: ID): [NexusNode!]!
    nexusMessages(sourceNodeId: ID, targetNodeId: ID): [NexusMessage!]!
    resources(region: String): [Resource!]!
    regions(claimed: Boolean): [ResourceRegion!]!
    events(civilizationId: ID!): [GameEvent!]!
    globalEvents(activeOnly: Boolean): [GlobalEvent!]!
    shipments(civilizationId: ID, status: ShipmentStatus): [Shipment!]!
    projects(civilizationId: ID, category: ProjectCategory): [Project!]!
    elections(civilizationId: ID, status: ElectionStatus): [Election!]!
    incidents(civilizationId: ID): [Incident!]!
    treaties(civilizationId: ID): [Treaty!]!
    leaderboard: [CivilizationScore!]!
    simulationStatus: SimulationStatus!
    balanceReport: [BalanceEntry!]!
}

type Mutation {
    createCivilization(name: String!, scale: CivilizationScale, region: String): Civilization!
    foundCivilization(name: String!, scale: CivilizationScale, regionId: ID!): Civilization!
    sendNexusMessage(sourceNodeId: ID!, targetNodeId: ID!, messageType: NexusMessageType!, content: String!): NexusMessage!
    registerNexusNode(name: String!, type: NexusNodeType!, region: String, civilizationId: ID!, knowledgeBase: String): NexusNode!
    proposeTrade(fromCivId: ID!, toCivId: ID!, resourceType: String!, quantity: Float!): TradeAgreement!
    createEvent(title: String!, description: String!, type: EventType!, severity: EventSeverity!, targetCivId: ID!): GameEvent!
}

enum NexusNodeType { PRIMARY SECONDARY RELAY }
enum NexusNodeStatus { BOOTING ACTIVE IDLE OFFLINE }
enum NexusMessageType { DATA SIGNAL BROADCAST COMMAND }

type NexusNode {
    id: ID!
    name: String!
    type: NexusNodeType!
    status: NexusNodeStatus!
    region: String
    civilizationId: ID!
    knowledgeBase: String
    lastActiveAt: String
    messageCount: Int
}

type NexusMessage {
    id: ID!
    sourceNodeId: ID!
    targetNodeId: ID!
    messageType: NexusMessageType!
    content: String!
    sentAt: String!
    delivered: Boolean!
    hopCount: Int!
}

# Logistics
enum ShipmentStatus { PENDING IN_TRANSIT DELIVERED CANCELLED }
type Shipment {
    id: ID!
    originRegion: String!
    destinationRegion: String!
    resourceType: String!
    quantity: Float!
    status: ShipmentStatus!
    civilizationId: ID!
    createdAt: String!
}

# Global Events
enum GlobalEventType { SOLAR_FLARE CLIMATE_SHIFT PANDEMIC ECONOMIC_BOOM DIPLOMATIC_SUMMIT TECH_REVOLUTION }
type GlobalEvent {
    id: ID!
    title: String!
    description: String!
    type: GlobalEventType!
    severity: EventSeverity!
    active: Boolean!
    startedAt: String!
    endedAt: String
}

# Contribution / Projects
enum ProjectCategory { INFRASTRUCTURE RESEARCH ECOLOGY CULTURE GOVERNANCE }
enum ProjectStatus { PROPOSED ACTIVE COMPLETED CANCELLED }
type Project {
    id: ID!
    name: String!
    description: String!
    category: ProjectCategory!
    status: ProjectStatus!
    targetContribution: Float!
    currentContribution: Float!
    civilizationId: ID!
}

# Governance / Elections & Treaties
enum ElectionStatus { UPCOMING ACTIVE COMPLETED CANCELLED }
type Election {
    id: ID!
    title: String!
    civilizationId: ID!
    status: ElectionStatus!
    startedAt: String!
    endedAt: String
}

enum TreatyType { PEACE ALLIANCE TRADE DEFENSE NON_AGGRESSION }
enum TreatyStatus { PROPOSED ACTIVE EXPIRED TERMINATED }
type Treaty {
    id: ID!
    name: String!
    type: TreatyType!
    status: TreatyStatus!
    signatoryCivilizationIds: [ID!]!
    createdAt: String!
}

# Social / Incidents
enum IncidentType { RIOT PROTEST UNREST ESPIONAGE CORRUPTION DISASTER }
enum IncidentStatus { ACTIVE INVESTIGATING RESOLVED CONTAINED }
type Incident {
    id: ID!
    title: String!
    description: String!
    type: IncidentType!
    status: IncidentStatus!
    civilizationId: ID!
    createdAt: String!
}
```

- [ ] **Step 2: Run schema verification**

Run: `./mvnw test-compile`
Expected: `BUILD SUCCESS`

- [ ] **Step 3: Commit**

```bash
git add src/main/resources/graphql/schema.graphqls
git commit -m "feat(graphql): update schema.graphqls with nexus types and new domain modules"
```

---

### Task 3: Update `CivilizationGraphQLController.java` Mappings

**Files:**
- Modify: `src/main/java/io/github/opencivilizationplatform/graphql/CivilizationGraphQLController.java`
- Create: `src/test/java/io/github/opencivilizationplatform/graphql/CivilizationGraphQLControllerTest.java`

**Interfaces:**
- Consumes: `schema.graphqls` queries/mutations.
- Produces: `@QueryMapping` and `@MutationMapping` handlers for Spring GraphQL.

- [ ] **Step 1: Implement resolver methods in `CivilizationGraphQLController.java`**

Add mappings for `nexusNodes`, `nexusMessages`, `registerNexusNode`, `sendNexusMessage`, `shipments`, `globalEvents`, `projects`, `elections`, `incidents`, `treaties`.

```java
@QueryMapping
public List<?> nexusNodes(@Argument Long civilizationId) {
    return nexusService.getNodesForCivilization(civilizationId);
}

@QueryMapping
public List<?> nexusMessages(@Argument Long sourceNodeId, @Argument Long targetNodeId) {
    return nexusService.getMessages(sourceNodeId, targetNodeId);
}

@MutationMapping
public Object registerNexusNode(@Argument String name, @Argument NexusNodeType type,
                                @Argument String region, @Argument Long civilizationId,
                                @Argument String knowledgeBase) {
    return nexusService.registerNode(name, type, region, civilizationId, knowledgeBase);
}
```

- [ ] **Step 2: Write unit test in `CivilizationGraphQLControllerTest.java`**

Write test verifying that `civilizations`, `civilization`, and `nexusNodes` queries execute without throwing exceptions.

- [ ] **Step 3: Run backend unit tests**

Run: `./mvnw test`
Expected: `BUILD SUCCESS` with all tests passing.

- [ ] **Step 4: Commit**

```bash
git add src/main/java/io/github/opencivilizationplatform/graphql/ src/test/java/io/github/opencivilizationplatform/graphql/
git commit -m "feat(graphql): implement nexus resolvers and add GraphQL controller unit test"
```

---

### Task 4: Align Frontend GraphQL Queries and Verify E2E

**Files:**
- Modify: `frontend/src/` (any queries using `voxtex`)

- [ ] **Step 1: Search and replace `voxtex` in frontend code**

Search `frontend/src/` for any instances of `voxtexNodes`, `voxtexMessages`, etc., and replace with `nexusNodes`, `nexusMessages`.

- [ ] **Step 2: Run frontend tests**

Run: `cd frontend && npm test`
Expected: Tests pass cleanly.

- [ ] **Step 3: Run full backend build and tests**

Run: `./mvnw clean test`
Expected: `BUILD SUCCESS`

- [ ] **Step 4: Commit**

```bash
git add frontend/
git commit -m "refactor(frontend): update GraphQL queries from voxtex to nexus"
```
