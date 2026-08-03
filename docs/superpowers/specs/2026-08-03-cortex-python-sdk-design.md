# Cortex Worker Python SDK Design Specification (Issue #70)

## Objective
Provide a native, lightweight Python SDK (`cortex-sdk`) allowing autonomous AI agents to connect to CivOS via Kafka event streams and GraphQL/REST APIs to execute decision loops, submit trade agreements, propose rules, and interact with Nexus nodes.

## Component Architecture

### 1. `cortex_sdk.client.CivOSClient`
- GraphQL & REST HTTP client for querying CivOS state:
  - `get_regions()`, `get_resources()`, `get_nexus_nodes()`, `get_technologies()`
  - `propose_trade(target_civ_id, resource_type, quantity)`
  - `propose_rule(title, description, category)`

### 2. `cortex_sdk.event_bus.CivOSEventListener`
- Kafka consumer/listener for real-time domain events:
  - `civos.nexus.message_sent`
  - `civos.civilization.created`
  - `civos.events.global_occurred`
  - `civos.resources.tick_processed`

### 3. `cortex_sdk.agent.BaseCortexAgent`
- Abstract base class for autonomous AI agents implementing an execution loop:
  - `on_tick(tick_event)`
  - `on_nexus_message(message_event)`
  - `act(action)`

### 4. `sdk/python/examples/autonomous_trade_agent.py`
- Demonstration Python agent that autonomously monitors resource scarcity and submits trade proposals.

## Verification Plan
1. Python unit tests using `pytest` inside `sdk/python/`.
2. Execution of `autonomous_trade_agent.py` against live running backend.
