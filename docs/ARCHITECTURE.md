# Architecture

## Overview

Civilization OS follows a **modular monolith** architecture — all modules live in a single deployable unit but are organized by bounded contexts using a clear package structure.

```
io.github.opencivilizationplatform
├── web/                    # MVC layer (controllers, views)
├── config/                 # Application configuration
├── core/                   # Shared domain events
├── dto/                    # Shared DTOs
└── modules/
    └── {module}/
        ├── api/            # REST controllers (@RestController)
        ├── application/    # Service layer (@Service)
        ├── domain/         # Entities (@Entity)
        └── infrastructure/ # Repositories (JpaRepository)
```

## Event-Driven Architecture & Kafka Topic Conventions

The platform uses `EventBus` (`SpringEventBus` for local Spring events and `KafkaEventBus` for distributed messaging) to publish domain events. All Kafka topics strictly follow the naming pattern:

`civos.<module>.<event>`

| Event Class | Module | Event Name | Kafka Topic |
|---|---|---|---|
| `CivilizationCreatedEvent` | `civilization` | `created` | `civos.civilization.created` |
| `NexusMessageSentEvent` | `nexus` | `message_sent` | `civos.nexus.message_sent` |
| `ResourceTickProcessedEvent` | `resources` | `tick_processed` | `civos.resources.tick_processed` |
| `TradeAgreementCreatedEvent` | `trade` | `agreement_created` | `civos.trade.agreement_created` |
| `ContributionSubmittedEvent` | `contribution` | `submitted` | `civos.contribution.submitted` |
| `ElectionCompletedEvent` | `governance` | `election_completed` | `civos.governance.election_completed` |
| `IncidentResolvedEvent` | `social` | `incident_resolved` | `civos.social.incident_resolved` |
| `ShipmentDeliveredEvent` | `logistics` | `shipment_delivered` | `civos.logistics.shipment_delivered` |
| `GlobalEventOccurredEvent` | `events` | `global_occurred` | `civos.events.global_occurred` |

## Data Flow

```
Browser → PageController (Thymeleaf SSR)
Client  → RestController (JSON API)
            ↓
        Service Layer
            ↓
        JPA Repository
            ↓
        PostgreSQL / PostGIS
```

## Cortex Engine

The Cortex simulation engine is a `@Scheduled` service that runs every 30 seconds:

```
RuleService.getValidatedRules()
      ↓
BalanceService.getBalanceReport()
      ↓
Rule evaluation (JSON logic)
      ↓
Priority-Weighted Robot Fabrication (Agri, Aqua, Explore, Utility)
      ↓
Autonomous Mesh Trading (Deficit barters & Personnel transits)
      ↓
Automated decisions & System remediation
```

It also listens for `BiosphereCriticalEvent` via `@EventListener` for real-time reactions.

## Technology Choices

| Decision | Choice | Rationale |
|----------|--------|-----------|
| Architecture | Monolith | Simpler deployment, single DB, all modules co-located |
| Rendering | Thymeleaf (SSR) | No separate frontend build, SEO-friendly |
| Database | PostgreSQL + PostGIS | Geospatial queries for resource locations |
| Reactive → Sync | Spring MVC (Servlet) | Compatible with Thymeleaf and JPA blocking I/O |
| Package | `io.github.opencivilizationplatform` | Matches GitHub org for Maven Central publishing |
