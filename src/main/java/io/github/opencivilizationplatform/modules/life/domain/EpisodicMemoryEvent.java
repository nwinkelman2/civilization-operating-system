package io.github.opencivilizationplatform.modules.life.domain;

import java.time.Instant;

public record EpisodicMemoryEvent(
    String eventId,
    Instant timestamp,
    String eventType,
    String description,
    double impactScore
) {
    public EpisodicMemoryEvent {
        if (eventId == null || eventId.isBlank()) {
            throw new IllegalArgumentException("eventId cannot be null or blank");
        }
        if (timestamp == null) {
            timestamp = Instant.now();
        }
        if (eventType == null) {
            eventType = "GENERAL";
        }
        if (description == null) {
            description = "";
        }
    }
}
