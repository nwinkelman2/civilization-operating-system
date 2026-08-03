package io.github.opencivilizationplatform.core.eventbus.events;

import io.github.opencivilizationplatform.core.eventbus.BaseDomainEvent;

public class IncidentResolvedEvent extends BaseDomainEvent {
    private final Long incidentId;
    private final Long civilizationId;
    private final String resolutionDetails;

    public IncidentResolvedEvent(String source, Long incidentId, Long civilizationId, String resolutionDetails) {
        super(source, "social", "incident_resolved");
        this.incidentId = incidentId;
        this.civilizationId = civilizationId;
        this.resolutionDetails = resolutionDetails;
    }

    public Long getIncidentId() { return incidentId; }
    public Long getCivilizationId() { return civilizationId; }
    public String getResolutionDetails() { return resolutionDetails; }
}
