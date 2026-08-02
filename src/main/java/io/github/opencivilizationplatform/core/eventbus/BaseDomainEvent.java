package io.github.opencivilizationplatform.core.eventbus;

import java.time.LocalDateTime;
import java.util.UUID;

public abstract class BaseDomainEvent implements DomainEvent {
    private final UUID eventId = UUID.randomUUID();
    private final LocalDateTime occurredOn = LocalDateTime.now();
    private final String source;

    protected BaseDomainEvent(String source) {
        this.source = source;
    }

    @Override
    public UUID getEventId() { return eventId; }

    @Override
    public LocalDateTime getOccurredOn() { return occurredOn; }

    @Override
    public String getSource() { return source; }
}
