# Welcome to Civilization Operating System (CivOS) Wiki

Welcome to the official Wiki for **Civilization Operating System (CivOS)** — an open, decentralized platform for building, simulating, and orchestrating autonomous AI civilizations, agent societies, resource stewardship, and real-world physical integration.

---

## 🌟 Vision & Platform Overview

```mermaid
graph TD
    CivOS["🏛️ Civilization OS Platform"]
    
    CivOS --> AI["🤖 Autonomous AI Societies<br/>(LLM Agents, Toolforge, Schools, Memory)"]
    CivOS --> Bio["🌾 Ecological Stewardship<br/>(Biosphere Health, Water, Energy, Gaia Score)"]
    CivOS --> Gov["📜 Dynamic Governance<br/>(DAO Engine, Law Creation, Config Matrices)"]
    CivOS --> Physical["⚙️ Physical World Integration<br/>(IoT Sensors, Autonomous Robotics, Digital Twins)"]

    classDef platform fill:#0f172a,stroke:#3b82f6,color:#fff,stroke-width:2px;
    classDef comp fill:#1e1b4b,stroke:#818cf8,color:#fff;
    class CivOS platform;
    class AI,Bio,Gov,Physical comp;
```

---

## 📚 Wiki Table of Contents

| Section | Description |
|---------|-------------|
| [📖 Home](Home) | Overview, Vision, and Quick Start |
| [🏗️ System Architecture](Architecture) | Modular monolith, event-driven design, GraphQL & CDC pipelines |
| [🎯 Milestones & Roadmap](Milestones-and-Roadmap) | Complete release milestones (v0.5.0 to v3.0.0) and issue mappings |
| [🧩 Modules & Services](Modules-and-Services) | In-depth guide to Civilization, Trade, Nexus, Biosphere & Cortex modules |
| [📡 API & Event Specifications](API-and-Events) | REST endpoints, GraphQL schema, WebSockets, and Kafka event channels |
| [💻 Developer Guide](Developer-Guide) | Local setup, Docker Compose, testing pipelines, and CI/CD |

---

## 🚀 Quick Overview of the Tech Stack

- **Backend**: Java 21 / Spring Boot 3 (Modular Monolith with Microservices extensions)
- **Database**: PostgreSQL 16 + PostGIS (Geospatial resource mapping)
- **Event Bus & Messaging**: Apache Kafka, Debezium CDC, Spring Application Events
- **API Layer**: GraphQL (Apollo Client integration) + REST APIs + WebSockets
- **Frontend**: React, Vite, TypeScript, TailwindCSS/Vanilla CSS, Nginx Proxy
- **DevOps & Infra**: Docker Compose, GitHub Actions CI/CD

---

## 🛣️ Strategic Roadmap Highlights

```mermaid
timeline
    title CivOS Milestone Release Timeline
    v0.5.0 : Core Platform & Integration : Auth, GraphQL, Multi-Tenancy & CDC
    v1.0.0 : Autonomous AI Engine : LLM Cortex, Toolforge & Pluggable Runtime
    v1.5.0 : Society Simulation : Colonies, Metabolism, Territory & Ecosystem
    v2.0.0 : Living Civilization : Cognition, Lifecycle, Memory & 2D Map
    v2.5.0 : Human Experience : Laws, Health, Climate, Culture & Observatory
    v3.0.0 : Open Ecosystem : IoT, Robotics, DAO Governance & Digital Twins
```

---

*Civilization OS is maintained by the Open Civilization Platform community.*
