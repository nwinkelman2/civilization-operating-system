# 🏗️ System Architecture

Civilization OS is engineered around a **hybrid event-driven architecture** combining a **Modular Monolith core** for state consistency with **event-driven microservices** for decoupled scaling and real-time streaming.

---

## 🏛️ High-Level System Diagram

```mermaid
graph TD
    Client["📱 React Frontend / UI Admin"] -->|HTTP / WS / GraphQL| Proxy["🛡️ Nginx Reverse Proxy"]
    
    subgraph Communication ["API & Transport Layer"]
        Proxy --> GraphQL["🔍 GraphQL Endpoint"]
        Proxy --> REST["🌐 REST v1 Controllers"]
        Proxy --> WS["⚡ WebSocket Handler"]
    end
    
    GraphQL --> App["🧠 Spring Boot Core Application<br/>(Modular Monolith / Cortex Loop)"]
    REST --> App
    WS --> App
    
    subgraph DataAndEvents ["Data & Messaging Layer"]
        App --> DB[("🐘 PostgreSQL / PostGIS")]
        App --> SpringBus["⚡ Spring Internal Events"]
        App --> Kafka["📡 Apache Kafka (EventBus)"]
        DB -->|WAL Log Streaming| Debezium["🔄 Debezium CDC"]
        Debezium -->|Stream CDC Events| Kafka
    end

    classDef client fill:#1f2937,stroke:#60a5fa,color:#fff,stroke-width:2px;
    classDef proxy fill:#111827,stroke:#f59e0b,color:#fff,stroke-width:2px;
    classDef core fill:#1e1b4b,stroke:#818cf8,color:#fff,stroke-width:2px;
    classDef data fill:#064e3b,stroke:#34d399,color:#fff,stroke-width:2px;
    class Client client;
    class Proxy proxy;
    class App core;
    class DB,SpringBus,Kafka,Debezium data;
```

---

## 🧩 Architectural Layers

### 1. Backend Core (Spring Boot 3)
Organized into domain-driven modules with explicit bounded contexts:

```mermaid
graph LR
    subgraph Modules ["Spring Boot Bounded Contexts"]
        Civ["🏙️ Civilization Module"]
        Nexus["⚡ Nexus Mesh (Voxtex)"]
        Trade["🤝 Autonomous Trade Mesh"]
        Bio["🌿 Biosphere Module"]
        Cortex["🧠 Cortex Simulation Engine"]
    end
    
    Cortex -->|Rules & Ticks| Civ
    Cortex -->|Matches Trade| Trade
    Trade -->|Node Transits| Nexus
    Bio -->|Ecological Alerts| Cortex
```

### 2. Cortex Engine & Simulation Loop
The Cortex Engine acts as the central brain of CivOS, running scheduled simulation ticks:

```mermaid
flowchart LR
    A["1️⃣ Rule Validation<br/>(JSON Logic)"] --> B["2️⃣ Balance Check<br/>(State Metrics)"]
    B --> C["3️⃣ Priority Fabrication<br/>(Robotics/Resource)"]
    C --> D["4️⃣ Autonomous Barter<br/>(Trade Mesh)"]
    D --> E["5️⃣ System Remediation<br/>(Auto Fixes)"]
    
    classDef step fill:#0f172a,stroke:#38bdf8,color:#fff;
    class A,B,C,D,E step;
```

- Listens to `BiosphereCriticalEvent` for instant reactive adjustments.
- Evaluates JSON logic rules against current state metrics.

---

## 🔄 Change Data Capture (Debezium CDC)

```mermaid
sequenceDiagram
    autonumber
    actor Client
    participant App as Spring Boot App
    participant DB as PostgreSQL
    participant CDC as Debezium CDC
    participant Kafka as Apache Kafka
    participant WS as WebSocket Push

    Client->>App: POST /api/v1/trade/agreements
    App->>DB: INSERT INTO trade_agreement
    DB-->>CDC: WAL Change Event
    CDC->>Kafka: Publish to 'civos.public.trade_agreement'
    Kafka-->>WS: Consume Event
    WS-->>Client: Real-Time UI Update Push
```

---

## 🔒 Security & Multi-Tenancy Architecture

```mermaid
graph TD
    Request["📥 Incoming Request"] --> Auth["🔑 JWT Auth Interceptor"]
    Auth -->|Valid Token| Tenant["🏢 Tenant Context Resolver"]
    Tenant -->|Sets X-Tenant-Id| ScopedRepo["🗄️ TenantAware JPA Repository"]
    ScopedRepo -->|Filter Query| DB[("🐘 Tenant-Isolated Data")]
```
