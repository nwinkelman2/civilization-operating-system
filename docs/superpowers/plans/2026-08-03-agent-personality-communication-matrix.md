# Agent Personality, Communication & Config Matrix Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Implement Agent Personality & Memory (Issue #39), Agent Communication Protocol (Issue #44), and Civilization Configuration Matrix (Issue #52).

---

### Task 1: AI Personality & Episodic Memory (Issue #39)

**Files:**
- Create: `src/main/java/io/github/opencivilizationplatform/modules/life/domain/AgentPersonality.java`
- Create: `src/main/java/io/github/opencivilizationplatform/modules/life/domain/EpisodicMemoryEvent.java`
- Create: `src/main/java/io/github/opencivilizationplatform/modules/life/application/AgentPersonalityService.java`
- Create: `src/test/java/io/github/opencivilizationplatform/modules/life/AgentPersonalityServiceTest.java`

- [ ] **Step 1: Create `AgentPersonality.java` record and `EpisodicMemoryEvent.java`**
- [ ] **Step 2: Create `AgentPersonalityService.java` with trait evaluation and memory log methods**
- [ ] **Step 3: Create `AgentPersonalityServiceTest.java` and verify with `./mvnw test`**
- [ ] **Step 4: Commit Task 1**

---

### Task 2: Agent Communication Protocol & Config Matrix (Issues #44 & #52)

**Files:**
- Create: `src/main/java/io/github/opencivilizationplatform/modules/nexus/application/AgentCommunicationService.java`
- Create: `src/test/java/io/github/opencivilizationplatform/modules/nexus/AgentCommunicationServiceTest.java`
- Create: `src/main/java/io/github/opencivilizationplatform/modules/civilization/domain/CivilizationConfigMatrix.java`
- Create: `src/main/java/io/github/opencivilizationplatform/modules/civilization/application/CivilizationConfigService.java`
- Create: `src/test/java/io/github/opencivilizationplatform/modules/civilization/CivilizationConfigServiceTest.java`

- [ ] **Step 1: Create `AgentCommunicationService.java` and unit tests**
- [ ] **Step 2: Create `CivilizationConfigMatrix.java` and `CivilizationConfigService.java` and unit tests**
- [ ] **Step 3: Run `./mvnw test` to verify 100% green pass**
- [ ] **Step 4: Push branch `feat/agent-personality-communication-matrix`, open PR #77, watch CI, and merge**
