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
