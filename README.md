# Civilization Operating System

A resource-based economy platform where players found, manage, and grow civilizations on an interactive world map.

## Features

- **Interactive World Map** — Choose regions based on resource availability (food, water, minerals, energy, housing)
- **Multi-Civilization** — Each client can found and manage their own civilization
- **nexus Neural Mesh** — Autonomous AI agents (nexus nodes) form a mesh network between civilizations
- **Tech Tree** — Research technologies to unlock bonuses and advance your civilization
- **Trade System** — Propose and manage trade agreements with other civilizations
- **Game Events** — Random events (discoveries, disasters, breakthroughs) affect gameplay
- **Cortex Engine** — Real-time simulation engine governing automated decision-making
- **Real-Time Communication** — WebSocket + SSE for live mesh updates
- **PWA Support** — Install as a standalone app on mobile/desktop

## Tech Stack

- **Backend**: Java 25, Spring Boot 4.0.3, Spring Framework 7
- **Frontend**: Thymeleaf, HTMX, Leaflet Maps, Canvas API
- **Database**: PostgreSQL 17 + PostGIS, Flyway migrations
- **Messaging**: WebSocket, Server-Sent Events (SSE)
- **Auth**: JWT + HTTP Basic
- **Cache**: Caffeine (5min TTL)
- **Rate Limiting**: Bucket4j (100 req/min per IP)
- **API Docs**: SpringDoc OpenAPI 2.8.5

## Quick Start

### Prerequisites
- Docker & Docker Compose
- Java 25 (for development)

### Run with Docker
```bash
make docker-up
# or
docker-compose up -d --build
```

### Run in dev mode
```bash
make run
# or
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

### Run tests
```bash
make test
# or
./mvnw test
```

## Configuration

### Environment Variables

| Variable | Default | Description |
|----------|---------|-------------|
| `CIVILIZATION_SCALE` | `LOCAL` | Seed data scale (LOCAL, REGIONAL, CONTINENTAL, GLOBAL) |
| `CIVILIZATION_ADMIN_USER` | `admin` | Admin username for HTTP Basic auth |
| `CIVILIZATION_ADMIN_PASSWORD` | `admin` | Admin password |
| `JWT_SECRET` | (dev secret) | Secret key for JWT tokens |
| `SPRING_PROFILES_ACTIVE` | `prod` | Spring profile (dev, prod, test) |
| `POSTGRES_USER` | `postgres` | Database username |
| `POSTGRES_PASSWORD` | `postgres` | Database password |

### Civilization Scales

| Scale | Regions | Description |
|-------|---------|-------------|
| LOCAL | 3 | Small settlement, local resources |
| REGIONAL | 4 | Multi-community, regional logistics |
| CONTINENTAL | 5 | Cross-continent infrastructure |
| GLOBAL | 6 | Planetary-scale civilization |

## API Documentation

With the app running, visit:
- Swagger UI: `http://localhost:8080/swagger-ui`
- OpenAPI JSON: `http://localhost:8080/api-docs`
- Health: `http://localhost:8080/actuator/health`

### Key Endpoints

| Method | Path | Description |
|--------|------|-------------|
| POST | `/api/v1/auth/connect` | Get JWT token |
| GET | `/api/v1/regions` | List resource regions |
| POST | `/api/v1/civilizations/found` | Found a civilization |
| GET | `/api/v1/civilizations/{id}` | Get civilization details |
| GET | `/api/v1/nexus/nodes` | List nexus nodes |
| GET | `/api/v1/nexus/stream` | SSE stream for mesh messages |
| GET | `/api/v1/tech-tree/{civId}` | Get tech tree |
| POST | `/api/v1/trade` | Propose trade |
| GET | `/api/v1/events/{civId}` | Get game events |
| GET | `/api/v1/leaderboard` | Civilization leaderboard |

## Architecture

```
┌─────────────────────────────────────────────┐
│               Web Clients                    │
│  (Browser / PWA / Mobile)                   │
└──────────────┬──────────────────────────────┘
               │ HTTP / WS / SSE
┌──────────────▼──────────────────────────────┐
│         Nginx Load Balancer                  │
└──────────────┬──────────────────────────────┘
               │
┌──────────────▼──────────────────────────────┐
│         Spring Boot Application(s)           │
│  ┌──────┬──────┬──────┬──────┬──────┬──────┐ │
│  │Civ   │nexus│Trade │Events│Tech  │Cortex│ │
│  │Module│Module│Module│Module│Tree  │Engine│ │
│  └──────┴──────┴──────┴──────┴──────┴──────┘ │
└──────────────┬──────────────────────────────┘
               │
┌──────────────▼──────────────────────────────┐
│         PostgreSQL + PostGIS                 │
└─────────────────────────────────────────────┘
```

## License

MIT

