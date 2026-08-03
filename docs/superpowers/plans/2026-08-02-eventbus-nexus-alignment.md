# EventBus Nexus Alignment Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Align the platform EventBus domain events and Kafka topic naming with current Nexus modules and publish events across all business lifecycle operations.

**Architecture:** Update `DomainEvent` and `BaseDomainEvent` to include explicit `module` and `eventName` fields, calculate Kafka topics dynamically as `civos.<module>.<event>`, rename `VoxtexMessageSentEvent` to `NexusMessageSentEvent`, introduce 5 new domain events, and integrate publishing into domain services.

**Tech Stack:** Java 25, Spring Boot 4, Spring Kafka, JUnit 5, AssertJ.

## Global Constraints
- Target Java package: `io.github.opencivilizationplatform.core.eventbus`
- Kafka topic format: `civos.<module>.<event>`
- Backward compatibility: `DomainEvent.getType()` returns `(getModule() + "_" + getEventName()).toUpperCase()`

---

### Task 1: Update Core Event Interfaces & Base Domain Event

**Files:**
- Modify: `src/main/java/io/github/opencivilizationplatform/core/eventbus/DomainEvent.java`
- Modify: `src/main/java/io/github/opencivilizationplatform/core/eventbus/BaseDomainEvent.java`

**Interfaces:**
- Consumes: None
- Produces: `DomainEvent.getModule()`, `DomainEvent.getEventName()`, `BaseDomainEvent(source, module, eventName)`

- [ ] **Step 1: Write updated `DomainEvent.java`**

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
        if (getModule() == null || getEventName() == null) return "UNKNOWN";
        return (getModule() + "_" + getEventName()).toUpperCase();
    }
}
```

- [ ] **Step 2: Write updated `BaseDomainEvent.java`**

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

- [ ] **Step 3: Compile to verify interface updates**

Run: `./mvnw test-compile`

- [ ] **Step 4: Commit core event interface changes**

```bash
git add src/main/java/io/github/opencivilizationplatform/core/eventbus/DomainEvent.java src/main/java/io/github/opencivilizationplatform/core/eventbus/BaseDomainEvent.java
git commit -m "refactor(eventbus): update DomainEvent interface and BaseDomainEvent with module and eventName fields"
```

---

### Task 2: Refactor Existing Events & Implement New Domain Events

**Files:**
- Modify: `src/main/java/io/github/opencivilizationplatform/core/eventbus/events/CivilizationCreatedEvent.java`
- Modify: `src/main/java/io/github/opencivilizationplatform/core/eventbus/events/ResourceTickProcessedEvent.java`
- Modify: `src/main/java/io/github/opencivilizationplatform/core/eventbus/events/TradeAgreementCreatedEvent.java`
- Create: `src/main/java/io/github/opencivilizationplatform/core/eventbus/events/NexusMessageSentEvent.java`
- Delete: `src/main/java/io/github/opencivilizationplatform/core/eventbus/events/VoxtexMessageSentEvent.java`
- Create: `src/main/java/io/github/opencivilizationplatform/core/eventbus/events/ContributionSubmittedEvent.java`
- Create: `src/main/java/io/github/opencivilizationplatform/core/eventbus/events/ElectionCompletedEvent.java`
- Create: `src/main/java/io/github/opencivilizationplatform/core/eventbus/events/IncidentResolvedEvent.java`
- Create: `src/main/java/io/github/opencivilizationplatform/core/eventbus/events/ShipmentDeliveredEvent.java`
- Create: `src/main/java/io/github/opencivilizationplatform/core/eventbus/events/GlobalEventOccurredEvent.java`

- [ ] **Step 1: Refactor `CivilizationCreatedEvent`**

```java
package io.github.opencivilizationplatform.core.eventbus.events;

import io.github.opencivilizationplatform.core.eventbus.BaseDomainEvent;
import io.github.opencivilizationplatform.config.seed.CivilizationScale;

public class CivilizationCreatedEvent extends BaseDomainEvent {
    private final Long civilizationId;
    private final String name;
    private final String region;
    private final CivilizationScale scale;
    private final String ownerToken;

    public CivilizationCreatedEvent(String source, Long civilizationId, String name,
                                     String region, CivilizationScale scale, String ownerToken) {
        super(source, "civilization", "created");
        this.civilizationId = civilizationId;
        this.name = name;
        this.region = region;
        this.scale = scale;
        this.ownerToken = ownerToken;
    }

    public Long getCivilizationId() { return civilizationId; }
    public String getName() { return name; }
    public String getRegion() { return region; }
    public CivilizationScale getScale() { return scale; }
    public String getOwnerToken() { return ownerToken; }
}
```

- [ ] **Step 2: Refactor `ResourceTickProcessedEvent` and `TradeAgreementCreatedEvent`**

In `ResourceTickProcessedEvent.java`: `super(source, "resources", "tick_processed");`
In `TradeAgreementCreatedEvent.java`: `super(source, "trade", "agreement_created");`

- [ ] **Step 3: Create `NexusMessageSentEvent` and remove `VoxtexMessageSentEvent`**

`NexusMessageSentEvent.java`:
```java
package io.github.opencivilizationplatform.core.eventbus.events;

import io.github.opencivilizationplatform.core.eventbus.BaseDomainEvent;

public class NexusMessageSentEvent extends BaseDomainEvent {
    private final Long messageId;
    private final Long sourceNodeId;
    private final Long targetNodeId;
    private final String messageType;
    private final String content;

    public NexusMessageSentEvent(String source, Long messageId, Long sourceNodeId,
                                  Long targetNodeId, String messageType, String content) {
        super(source, "nexus", "message_sent");
        this.messageId = messageId;
        this.sourceNodeId = sourceNodeId;
        this.targetNodeId = targetNodeId;
        this.messageType = messageType;
        this.content = content;
    }

    public Long getMessageId() { return messageId; }
    public Long getSourceNodeId() { return sourceNodeId; }
    public Long getTargetNodeId() { return targetNodeId; }
    public String getMessageType() { return messageType; }
    public String getContent() { return content; }
}
```

- [ ] **Step 4: Create new domain events (`ContributionSubmittedEvent`, `ElectionCompletedEvent`, `IncidentResolvedEvent`, `ShipmentDeliveredEvent`, `GlobalEventOccurredEvent`)**

`ContributionSubmittedEvent.java`: (`module: "contribution"`, `eventName: "submitted"`)
`ElectionCompletedEvent.java`: (`module: "governance"`, `eventName: "election_completed"`)
`IncidentResolvedEvent.java`: (`module: "social"`, `eventName: "incident_resolved"`)
`ShipmentDeliveredEvent.java`: (`module: "logistics"`, `eventName: "shipment_delivered"`)
`GlobalEventOccurredEvent.java`: (`module: "events"`, `eventName: "global_occurred"`)

- [ ] **Step 5: Verify event compilation**

Run: `./mvnw test-compile`

- [ ] **Step 6: Commit all domain events**

```bash
git add src/main/java/io/github/opencivilizationplatform/core/eventbus/events/
git rm src/main/java/io/github/opencivilizationplatform/core/eventbus/events/VoxtexMessageSentEvent.java
git commit -m "feat(eventbus): add new Nexus domain events and replace VoxtexMessageSentEvent"
```

---

### Task 3: Update `KafkaEventBus` and EventBus Unit Tests

**Files:**
- Modify: `src/main/java/io/github/opencivilizationplatform/core/eventbus/KafkaEventBus.java`
- Create: `src/test/java/io/github/opencivilizationplatform/core/eventbus/KafkaEventBusTest.java`

- [ ] **Step 1: Update `KafkaEventBus.java` topic generation**

Change line 47 in `KafkaEventBus.java`:
```java
String topic = "civos." + event.getModule() + "." + event.getEventName();
```

- [ ] **Step 2: Create unit test `KafkaEventBusTest.java`**

```java
package io.github.opencivilizationplatform.core.eventbus;

import io.github.opencivilizationplatform.core.eventbus.events.CivilizationCreatedEvent;
import io.github.opencivilizationplatform.core.eventbus.events.NexusMessageSentEvent;
import io.github.opencivilizationplatform.config.seed.CivilizationScale;
import tools.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.kafka.core.KafkaTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class KafkaEventBusTest {

    @Test
    void shouldPublishToCorrectTopicName() {
        KafkaTemplate<String, String> kafkaTemplate = mock(KafkaTemplate.class);
        ObjectMapper objectMapper = new ObjectMapper();
        KafkaEventBus eventBus = new KafkaEventBus(kafkaTemplate, objectMapper);

        CivilizationCreatedEvent event = new CivilizationCreatedEvent("test", 1L, "Civ", "REG1", CivilizationScale.LOCAL, "tok");
        eventBus.publish(event);

        ArgumentCaptor<ProducerRecord<String, String>> captor = ArgumentCaptor.forClass(ProducerRecord.class);
        verify(kafkaTemplate).send(captor.capture());

        assertThat(captor.getValue().topic()).isEqualTo("civos.civilization.created");
    }
}
```

- [ ] **Step 3: Run `KafkaEventBusTest`**

Run: `./mvnw test -Dtest=KafkaEventBusTest`
Expected: PASS

- [ ] **Step 4: Commit `KafkaEventBus` updates and unit tests**

```bash
git add src/main/java/io/github/opencivilizationplatform/core/eventbus/KafkaEventBus.java src/test/java/io/github/opencivilizationplatform/core/eventbus/KafkaEventBusTest.java
git commit -m "feat(eventbus): update KafkaEventBus topic formatting to civos.<module>.<event>"
```

---

### Task 4: Integrate Event Publishing in Domain Services

**Files:**
- Modify: `src/main/java/io/github/opencivilizationplatform/modules/nexus/application/NexusMeshService.java`
- Modify: `src/main/java/io/github/opencivilizationplatform/modules/contribution/application/ContributionService.java`
- Modify: `src/main/java/io/github/opencivilizationplatform/modules/contribution/application/DelegateElectionService.java`
- Modify: `src/main/java/io/github/opencivilizationplatform/modules/social/application/SocialService.java`
- Modify: `src/main/java/io/github/opencivilizationplatform/modules/logistics/application/ShipmentService.java`
- Modify: `src/main/java/io/github/opencivilizationplatform/modules/events/application/GlobalEventService.java`

- [ ] **Step 1: Update `NexusMeshService` to publish `NexusMessageSentEvent`**

Replace `VoxtexMessageSentEvent` with `NexusMessageSentEvent` in `NexusMeshService.java`.

- [ ] **Step 2: Inject `EventBus` into `ContributionService` and publish `ContributionSubmittedEvent`**

In `ContributionService.java`: publish `ContributionSubmittedEvent` when contribution project is funded/created.

- [ ] **Step 3: Inject `EventBus` into `DelegateElectionService` and publish `ElectionCompletedEvent`**

In `DelegateElectionService.java`: publish `ElectionCompletedEvent` when election finishes.

- [ ] **Step 4: Inject `EventBus` into `SocialService` and publish `IncidentResolvedEvent`**

In `SocialService.java`: publish `IncidentResolvedEvent` when social incident status becomes `RESOLVED`.

- [ ] **Step 5: Inject `EventBus` into `ShipmentService` and publish `ShipmentDeliveredEvent`**

In `ShipmentService.java`: publish `ShipmentDeliveredEvent` when shipment status becomes `DELIVERED`.

- [ ] **Step 6: Inject `EventBus` into `GlobalEventService` and publish `GlobalEventOccurredEvent`**

In `GlobalEventService.java`: publish `GlobalEventOccurredEvent` when global event is triggered.

- [ ] **Step 7: Run test compilation**

Run: `./mvnw test-compile`

- [ ] **Step 8: Commit service event publication integration**

```bash
git add src/main/java/io/github/opencivilizationplatform/modules/
git commit -m "feat(services): integrate domain event publishing across all Nexus backend services"
```

---

### Task 5: Documentation & Full Verification

**Files:**
- Modify: `docs/ARCHITECTURE.md`

- [ ] **Step 1: Update `docs/ARCHITECTURE.md` Event-Driven Architecture section**

Document Kafka topic naming scheme `civos.<module>.<event>` and list of all 9 domain events in `docs/ARCHITECTURE.md`.

- [ ] **Step 2: Run full build and test suite**

Run: `./mvnw clean verify -B -Dspring.profiles.active=test`
Expected: BUILD SUCCESS with 0 test failures.

- [ ] **Step 3: Commit documentation and final verification**

```bash
git add docs/ARCHITECTURE.md
git commit -m "docs: update architecture documentation with civos.<module>.<event> Kafka topics"
```
