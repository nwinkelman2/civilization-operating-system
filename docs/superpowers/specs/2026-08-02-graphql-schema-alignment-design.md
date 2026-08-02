# Design Document: Align GraphQL Schema with Current Backend Entities (Issue #11)

## Overview
This design document details the refactoring and extension of the GraphQL schema (`schema.graphqls`) and its associated Java backend controllers/services to align with the renamed `Nexus` module (formerly `Voxtex`) and integrate newly added domain modules (`logistics`, `events`, `contribution`, `governance`/`nexus`, and `social`).

## Goals
1. Resolve compilation errors caused by legacy `voxtex` package imports in `CivilizationGraphQLController`, `FoundCivilizationSagaSteps`, `WebSocketConfig`, and `NexusMeshService`.
2. Fully migrate all GraphQL types, queries, and mutations from `Voxtex` to `Nexus`.
3. Add missing GraphQL types, queries, and mutations for new domain modules (`Shipment`, `GlobalEvent`, `Project`, `Election`, `Incident`, `Treaty`).
4. Update frontend GraphQL queries to use `Nexus` instead of `Voxtex`.

## Non-Goals
- Modifying underlying database schemas or Liquibase migrations (all JPA entities already exist in the codebase).
- Replacing REST controllers. GraphQL acts alongside REST as a complementary query engine.

## Architectural Changes

### 1. Schema Modifications (`src/main/resources/graphql/schema.graphqls`)

#### A. Rename Voxtex to Nexus
- Rename `type VoxtexNode` to `type NexusNode`
- Rename `enum VoxtexNodeType` to `enum NexusNodeType`
- Rename `enum VoxtexNodeStatus` to `enum NexusNodeStatus`
- Rename `type VoxtexMessage` to `type NexusMessage`
- Rename `enum VoxtexMessageType` to `enum NexusMessageType`
- Rename query `voxtexNodes` to `nexusNodes`
- Rename query `voxtexMessages` to `nexusMessages`
- Rename mutation `sendVoxtexMessage` to `sendNexusMessage`
- Rename mutation `registerVoxtexNode` to `registerNexusNode`

#### B. Add New Domain Types & Enums
```graphql
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

# Nexus / Governance (Elections & Treaties)
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

#### C. Add New Queries to `Query`
- `nexusNodes(civilizationId: ID): [NexusNode!]!`
- `nexusMessages(sourceNodeId: ID, targetNodeId: ID): [NexusMessage!]!`
- `shipments(civilizationId: ID, status: ShipmentStatus): [Shipment!]!`
- `globalEvents(activeOnly: Boolean): [GlobalEvent!]!`
- `projects(civilizationId: ID, category: ProjectCategory): [Project!]!`
- `elections(civilizationId: ID, status: ElectionStatus): [Election!]!`
- `incidents(civilizationId: ID): [Incident!]!`
- `treaties(civilizationId: ID): [Treaty!]!`

### 2. Backend Controller & Java Classes Fixes

#### A. Fix Legacy Voxtex Package Imports
- Update `CivilizationGraphQLController.java`:
  - Replace `io.github.opencivilizationplatform.modules.voxtex.*` imports with `io.github.opencivilizationplatform.modules.nexus.*` (`NexusMeshService`, `NexusNodeType`, etc.).
  - Inject services for new domain modules (`ShipmentService`, `GlobalEventService`, `ProjectService`, `ElectionService`, `IncidentService`, `TreatyService`).
  - Add `@QueryMapping` methods for `nexusNodes`, `nexusMessages`, `shipments`, `globalEvents`, `projects`, `elections`, `incidents`, `treaties`.
  - Add `@MutationMapping` methods for `sendNexusMessage`, `registerNexusNode`.
- Update `FoundCivilizationSagaSteps.java`:
  - Replace `VoxtexMeshService` and `VoxtexNodeType` with `NexusMeshService` and `NexusNodeType`.
- Update `WebSocketConfig.java`:
  - Replace `VoxtexWebSocketHandler` with `NexusWebSocketHandler`.
- Update `NexusMeshService.java`:
  - Replace remaining `voxtex` package imports with `nexus` imports.
- Update `CortexWorkerApplication.java`:
  - Add missing import `import org.springframework.boot.autoconfigure.domain.EntityScan;`.

### 3. Frontend Updates
- Audit frontend code (e.g. `frontend/src/App.tsx`, `frontend/src/test/App.test.tsx`, or any GraphQL query strings) to replace `voxtex` queries with `nexus` queries.

## Testing & Verification Plan
1. Run `./mvnw test-compile` to verify all Java compilation errors are resolved.
2. Run `./mvnw test` to ensure unit and integration tests pass cleanly.
3. Test GraphQL schema loading via Spring Boot Test / GraphQL Tester or `mvn test`.
