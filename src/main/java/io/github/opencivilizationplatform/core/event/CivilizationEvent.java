package io.github.opencivilizationplatform.core.event;

import org.springframework.context.ApplicationEvent;
import java.time.LocalDateTime;

public abstract class CivilizationEvent extends ApplicationEvent {
    private final LocalDateTime timestamp;
    private final String sourceContext;

    public CivilizationEvent(Object source, String sourceContext) {
        super(source);
        this.timestamp = LocalDateTime.now();
        this.sourceContext = sourceContext;
    }

    public LocalDateTime getCivilizationTimestamp() {
        return timestamp;
    }

    public String getSourceContext() {
        return sourceContext;
    }
}
