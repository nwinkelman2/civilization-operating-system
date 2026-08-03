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
