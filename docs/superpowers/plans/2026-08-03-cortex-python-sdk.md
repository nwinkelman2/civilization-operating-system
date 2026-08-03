# Cortex Worker Python SDK Implementation Plan (Issue #70)

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Create a native Python SDK package (`cortex-sdk`) under `sdk/python/` with GraphQL API client, Kafka event listener, base agent abstraction, unit tests, and example autonomous agent.

**Architecture:** Pure Python package with `urllib`/`requests` API client, Kafka consumer helper, and structured agent callbacks.

---

### Task 1: Package Structure & GraphQL/REST Client (`cortex_sdk/client.py`)

**Files:**
- Create: `sdk/python/pyproject.toml`
- Create: `sdk/python/cortex_sdk/__init__.py`
- Create: `sdk/python/cortex_sdk/client.py`
- Create: `sdk/python/tests/test_client.py`

- [ ] **Step 1: Create `pyproject.toml` and `cortex_sdk/__init__.py`**
- [ ] **Step 2: Create `cortex_sdk/client.py` with `CivOSClient`**
  - Implement `get_civilizations()`, `get_regions()`, `get_nexus_nodes()`, `propose_trade()`, `propose_rule()`.
- [ ] **Step 3: Create `tests/test_client.py` and run tests**
- [ ] **Step 4: Commit Task 1**
  - `git add sdk/python/`
  - `git commit -m "feat(sdk): create Python SDK package and CivOSClient API wrapper"`

---

### Task 2: Event Listener & Base Cortex Agent (`event_bus.py`, `agent.py`)

**Files:**
- Create: `sdk/python/cortex_sdk/event_bus.py`
- Create: `sdk/python/cortex_sdk/agent.py`
- Create: `sdk/python/examples/autonomous_trade_agent.py`
- Create: `sdk/python/tests/test_agent.py`

- [ ] **Step 1: Create `cortex_sdk/event_bus.py`**
  - Event listener parsing Kafka JSON events or HTTP SSE tick events.
- [ ] **Step 2: Create `cortex_sdk/agent.py` with `BaseCortexAgent`**
  - Callback loop for `on_resource_tick` and `on_nexus_message`.
- [ ] **Step 3: Create `examples/autonomous_trade_agent.py`**
  - Example autonomous agent logic.
- [ ] **Step 4: Create `tests/test_agent.py` and run unit tests**
- [ ] **Step 5: Commit Task 2**
  - `git add sdk/python/`
  - `git commit -m "feat(sdk): add event listener, BaseCortexAgent, and autonomous trade agent example"`

---

### Task 3: Feature Branch, PR Creation, and Merging for Issue #70

- [ ] **Step 1: Create branch `feat/cortex-python-sdk` and push**
  - `git checkout -b feat/cortex-python-sdk`
  - `git push -u origin feat/cortex-python-sdk`
- [ ] **Step 2: Open PR #73 and watch CI**
  - `gh pr create --title "feat(sdk): implement AI Cortex Worker Python SDK (Closes #70)" ...`
  - `gh run watch`
  - `gh pr merge --squash --delete-branch`
