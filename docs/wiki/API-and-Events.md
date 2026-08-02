# 📡 API & Event Specifications

Civilization OS provides **GraphQL**, **REST**, **WebSocket**, and **Kafka Event Streams** to interact with the platform.

---

## 🔍 GraphQL API (`/graphql`)

The GraphQL API is the primary interface for the frontend application.

### Key Queries
```graphql
query GetSimulationStatus {
  simulationStatus {
    tick
    status
    activeAgents
    biosphereHealth
  }
}

query GetLeaderboard {
  civilizations {
    id
    name
    population
    score
    sustainabilityIndex
  }
}

query GetNexusNodes {
  nexusNodes {
    id
    name
    status
    connectedPeers
  }
}
```

---

## 🌐 REST API Endpoints (`/api/v1`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/v1/auth/login` | Authenticates user and returns JWT token |
| `POST` | `/api/v1/auth/register` | Registers new user account |
| `GET` | `/api/v1/civilizations` | Lists all active civilizations |
| `GET` | `/api/v1/trade/agreements` | Fetches active trade barter agreements |
| `POST` | `/api/v1/trade/agreements` | Creates a new trade agreement |
| `GET` | `/api/v1/rules` | Retrieves active governance rules |
| `GET` | `/api/v1/health` | System health check endpoint |

---

## ⚡ WebSockets

### 1. Nexus Mesh Real-Time Stream
- **URL**: `ws://<host>/ws/nexus?token=<JWT>`
- **Payload**: Broadcasts real-time agent positions, node statuses, and resource movement.

### 2. System Notifications
- **URL**: `ws://<host>/ws/notifications?token=<JWT>`
- **Payload**: Broadcasts system alerts, ecological critical events, and trade completion push notifications.

---

## 🔄 Kafka Topics & EventBus

CDC (Change Data Capture) and domain events stream over Apache Kafka:

| Topic Name | Producer | Description |
|------------|----------|-------------|
| `civos.public.civilization` | Debezium CDC | Database change events for civilizations |
| `civos.public.nexus_node` | Debezium CDC | Real-time state changes on Nexus nodes |
| `civos.public.trade_agreement` | Debezium CDC | Real-time updates on trade agreements |
| `civos.events.biosphere` | EventBus | Critical ecological alert triggers |
