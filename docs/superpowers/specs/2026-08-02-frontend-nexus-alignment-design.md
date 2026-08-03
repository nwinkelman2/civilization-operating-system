# Frontend & Backend Nexus Alignment Design Specification (Issue #3)

## Objective
Fully align the frontend application routes, sidebar navigation, WebSocket hooks, and remaining backend saga/CDC components from legacy Voxtex naming to **Nexus Mesh**. Maintain backward compatibility for WebSocket connections (`/ws/voxtex`) and route redirection (`/voxtex` -> `/nexus`).

## Proposed Changes

### 1. Frontend Updates (`frontend/src/`)
- **`App.tsx`**:
  - Add route `/nexus` rendering `<NexusMesh />`.
  - Add fallback route redirecting `/voxtex` to `/nexus`.
  - Ensure sidebar navigation links to `/nexus` with label `"Nexus Mesh"`.
- **`useWebSocket.ts`**:
  - Update default WebSocket URL to `/ws/nexus`.
- **`useRealtimeUpdates.ts`**:
  - Listen for `NEXUS_MESSAGE` / `nexus_message` events in addition to legacy `VOXTEX_MESSAGE`.

### 2. Backend & Infrastructure Updates (`src/main/`)
- **`WebSocketConfig.java`**:
  - Register `/ws/nexus` and `/ws/voxtex` endpoints on `NexusWebSocketHandler`.
- **`KafkaConfig.java` & `KafkaConsumerConfig.java`**:
  - Update constant `TOPIC_NEXUS_MESSAGE_SENT = "civos.nexus.message_sent"`.
  - Update CDC topic listeners to `civos.cdc.public.nexus_nodes` and `civos.nexus.message_sent`.
- **Saga Flow (`FoundCivilizationContext.java`, `FoundCivilizationSagaSteps.java`, `SagaController.java`)**:
  - Rename `voxtexNodeDeployed` to `nexusNodeDeployed`.
  - Rename `deployVoxtexNode()` to `deployNexusNode()`.
- **`debezium/connector.json`**:
  - Update table list from `public.voxtex_nodes,public.voxtex_messages` to `public.nexus_nodes,public.nexus_messages`.

## Verification Plan
1. Run `./mvnw test-compile` to verify Java backend compilation.
2. Check frontend compilation with `npm run build` or TypeScript check inside `frontend/`.
3. Run `./mvnw test` to ensure all unit and integration tests pass.
