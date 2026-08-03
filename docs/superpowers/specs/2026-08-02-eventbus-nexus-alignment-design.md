# Design Specification: Align EventBus Events with Nexus Backend (Issue #10)

## Overview
This design aligns the platform's EventBus system (`EventBus`, `SpringEventBus`, `KafkaEventBus`) with the current Nexus domain modules (`civilization`, `nexus`, `resources`, `trade`, `contribution`, `governance`, `social`, `logistics`, `events`). It introduces new domain events for missing business actions, replaces legacy `VoxtexMessageSentEvent` with `NexusMessageSentEvent`, updates Kafka topic naming to follow `civos.<module>.<event>`, and integrates event publishing into domain services.

## Core Interfaces & Base Class

### `io.github.opencivilizationplatform.core.eventbus.DomainEvent`
```java
package io.github.opencivilizationplatform.core.eventbus;

import java.time.LocalDateTime;
import java.util.UUID;

public interface DomainEvent {
    UUID getEventId();
    LocalDateTime getOccurredOn();
    String getSource();
    String getModule();
    String getEventName();
    
    default String getType() {
        return (getModule() + "_" + getEventName()).toUpperCase();
    }
}
```

### `io.github.opencivilizationplatform.core.eventbus.BaseDomainEvent`
```java
package io.github.opencivilizationplatform.core.eventbus;

import java.time.LocalDateTime;
import java.util.UUID;

public abstract class BaseDomainEvent implements DomainEvent {
    private final UUID eventId = UUID.randomUUID();
    private final LocalDateTime occurredOn = LocalDateTime.now();
    private final String source;
    private final String module;
    private final String eventName;

    protected BaseDomainEvent(String source, String module, String eventName) {
        this.source = source;
        this.module = module;
        this.eventName = eventName;
    }

    @Override public UUID getEventId() { return eventId; }
    @Override public LocalDateTime getOccurredOn() { return occurredOn; }
    @Override public String getSource() { return source; }
    @Override public String getModule() { return module; }
    @Override public String getEventName() { return eventName; }
}
```

## Domain Event Hierarchy (`io.github.opencivilizationplatform.core.eventbus.events`)

| Event Class | Module (`getModule()`) | Event Name (`getEventName()`) | Kafka Topic (`civos.<module>.<event>`) | Key Payload Fields |
|---|---|---|---|---|
| `CivilizationCreatedEvent` | `civilization` | `created` | `civos.civilization.created` | `civilizationId`, `name`, `region`, `scale`, `ownerToken` |
| `NexusMessageSentEvent` | `nexus` | `message_sent` | `civos.nexus.message_sent` | `messageId`, `sourceNodeId`, `targetNodeId`, `messageType`, `content` |
| `ResourceTickProcessedEvent` | `resources` | `tick_processed` | `civos.resources.tick_processed` | `tickCount`, `processedAt` |
| `TradeAgreementCreatedEvent` | `trade` | `agreement_created` | `civos.trade.agreement_created` | `tradeId`, `fromCivId`, `toCivId`, `resourceType`, `quantity` |
| `ContributionSubmittedEvent` | `contribution` | `submitted` | `civos.contribution.submitted` | `contributionId`, `projectId`, `citizenId`, `amount` |
| `ElectionCompletedEvent` | `governance` | `election_completed` | `civos.governance.election_completed` | `electionId`, `civilizationId`, `winnerCitizenId` |
| `IncidentResolvedEvent` | `social` | `incident_resolved` | `civos.social.incident_resolved` | `incidentId`, `civilizationId`, `resolutionDetails` |
| `ShipmentDeliveredEvent` | `logistics` | `shipment_delivered` | `civos.logistics.shipment_delivered` | `shipmentId`, `originRegion`, `destinationRegion`, `quantity` |
| `GlobalEventOccurredEvent` | `events` | `global_occurred` | `civos.events.global_occurred` | `eventId`, `title`, `type`, `severity` |

## Kafka Topic Naming & Bus Logic

In `KafkaEventBus`:
```java
String topic = "civos." + event.getModule() + "." + event.getEventName();
```

## Service Integration Points
- `CivilizationService`: publishes `CivilizationCreatedEvent` on civilization creation/founding.
- `NexusMeshService`: publishes `NexusMessageSentEvent` on sending Nexus mesh messages.
- `CortexEngineService`: publishes `ResourceTickProcessedEvent` on simulation tick cycle.
- `TradeService`: publishes `TradeAgreementCreatedEvent` on trade proposal/creation.
- `ContributionService`: publishes `ContributionSubmittedEvent` on contribution record creation.
- `DelegateElectionService`: publishes `ElectionCompletedEvent` when election status changes to `COMPLETED`.
- `SocialService`: publishes `IncidentResolvedEvent` when incident status changes to `RESOLVED`.
- `ShipmentService`: publishes `ShipmentDeliveredEvent` when shipment status changes to `DELIVERED`.
- `GlobalEventService`: publishes `GlobalEventOccurredEvent` on trigger/creation of a global event.

## Verification Plan
1. **Unit Tests:** `SpringEventBusTest` and `KafkaEventBusTest` verifying that all 9 event types publish and calculate topics correctly (`civos.<module>.<event>`).
2. **Integration Test:** Service unit/integration tests confirming event publication during domain lifecycle operations.
3. **Documentation:** Update `docs/ARCHITECTURE.md` section on Event Driven Architecture and Kafka Topic Conventions.
