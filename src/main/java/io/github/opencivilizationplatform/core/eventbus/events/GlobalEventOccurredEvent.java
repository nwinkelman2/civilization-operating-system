package io.github.opencivilizationplatform.core.eventbus.events;

import io.github.opencivilizationplatform.core.eventbus.BaseDomainEvent;

public class GlobalEventOccurredEvent extends BaseDomainEvent {
    private final Long globalEventId;
    private final String title;
    private final String type;
    private final String severity;

    public GlobalEventOccurredEvent(String source, Long globalEventId, String title, String type, String severity) {
        super(source, "events", "global_occurred");
        this.globalEventId = globalEventId;
        this.title = title;
        this.type = type;
        this.severity = severity;
    }

    public Long getGlobalEventId() { return globalEventId; }
    public String getTitle() { return title; }
    public String getType() { return type; }
    public String getSeverity() { return severity; }
}
