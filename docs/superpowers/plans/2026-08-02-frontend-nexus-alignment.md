# Frontend & Backend Nexus Alignment Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Adapt frontend routes, hooks, navigation, and remaining backend saga/CDC components from legacy Voxtex naming to Nexus Mesh (Issue #3).

**Architecture:** Update frontend routes in `App.tsx`, WebSocket hooks in `useWebSocket.ts` and `useRealtimeUpdates.ts`, Spring `WebSocketConfig.java` to support `/ws/nexus` and `/ws/voxtex`, align backend Saga steps to `deployNexusNode`, and update Kafka CDC configuration in `debezium/connector.json`.

**Tech Stack:** React 19, TypeScript, Vite, Java 25, Spring Boot 4, Spring Kafka.

## Global Constraints
- Main route: `/nexus`, fallback redirect from `/voxtex`
- Main WebSocket endpoint: `/ws/nexus`, backward compatible alias `/ws/voxtex`
- Kafka CDC topic format: `civos.cdc.public.nexus_nodes`

---

### Task 1: Update Frontend Routes, Sidebar, and WebSocket Hooks

**Files:**
- Modify: `frontend/src/App.tsx`
- Modify: `frontend/src/hooks/useWebSocket.ts`
- Modify: `frontend/src/hooks/useRealtimeUpdates.ts`

- [ ] **Step 1: Update `App.tsx`**
  - Change route `/voxtex` to `/nexus`.
  - Add `<Route path="/voxtex" element={<Navigate to="/nexus" replace />} />`.
  - Update sidebar navigation menu items to link to `/nexus` with label `"Nexus Mesh"`.

- [ ] **Step 2: Update `useWebSocket.ts`**
  - Update default WebSocket URL from `/ws/voxtex` to `/ws/nexus`.

- [ ] **Step 3: Update `useRealtimeUpdates.ts`**
  - Update message type handling to handle `NEXUS_MESSAGE` / `nexus_message` as well as legacy `VOXTEX_MESSAGE`.

- [ ] **Step 4: Verify frontend TypeScript build**
  - Run `npm run build` or `npx tsc --noEmit` inside `frontend/`.

- [ ] **Step 5: Commit frontend changes**
  - `git add frontend/`
  - `git commit -m "feat(frontend): update routes, sidebar, and WebSocket hooks from voxtex to nexus"`

---

### Task 2: Update Backend WebSocket Alias, Saga Steps, and CDC Config

**Files:**
- Modify: `src/main/java/io/github/opencivilizationplatform/config/WebSocketConfig.java`
- Modify: `src/main/java/io/github/opencivilizationplatform/config/KafkaConfig.java`
- Modify: `src/main/java/io/github/opencivilizationplatform/config/KafkaConsumerConfig.java`
- Modify: `src/main/java/io/github/opencivilizationplatform/saga/FoundCivilizationContext.java`
- Modify: `src/main/java/io/github/opencivilizationplatform/saga/FoundCivilizationSagaSteps.java`
- Modify: `src/main/java/io/github/opencivilizationplatform/saga/SagaController.java`
- Modify: `debezium/connector.json`

- [ ] **Step 1: Update `WebSocketConfig.java`**
  - Register both `"/ws/nexus"` and `"/ws/voxtex"` endpoints on `NexusWebSocketHandler`.

- [ ] **Step 2: Update `KafkaConfig.java` & `KafkaConsumerConfig.java`**
  - Change `TOPIC_VOXTEX_MESSAGE_SENT` to `TOPIC_NEXUS_MESSAGE_SENT = "civos.nexus.message_sent"`.
  - Update `@KafkaListener(topics = "civos.cdc.public.nexus_nodes")`.

- [ ] **Step 3: Update Saga classes**
  - Rename `voxtexNodeDeployed` to `nexusNodeDeployed` in `FoundCivilizationContext.java`.
  - Rename `deployVoxtexNode()` to `deployNexusNode()` in `FoundCivilizationSagaSteps.java`.
  - Update invocation in `SagaController.java`.

- [ ] **Step 4: Update `debezium/connector.json`**
  - Change `public.voxtex_nodes,public.voxtex_messages` to `public.nexus_nodes,public.nexus_messages`.

- [ ] **Step 5: Verify Java compilation & tests**
  - Run `./mvnw test-compile` and `./mvnw test`.

- [ ] **Step 6: Commit backend saga and CDC changes**
  - `git add src/ debezium/`
  - `git commit -m "refactor(backend): update WebSocket routes, Saga steps, and CDC topics from voxtex to nexus"`

---

### Task 3: Full Verification & PR Creation

- [ ] **Step 1: Run full test suite**
  - `./mvnw clean verify -B -Dspring.profiles.active=test`

- [ ] **Step 2: Create feature branch, push, and open PR via `gh`**
  - `git checkout -b feat/frontend-nexus-alignment-issue-3`
  - `git push -u origin feat/frontend-nexus-alignment-issue-3`
  - `gh pr create --title "feat(frontend): adapt Voxtex pages, WebSocket hooks, and CDC to Nexus (#3)" --body "Closes #3" --head feat/frontend-nexus-alignment-issue-3 --base main`
