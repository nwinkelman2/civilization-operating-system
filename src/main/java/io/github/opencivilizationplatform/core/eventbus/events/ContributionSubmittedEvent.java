package io.github.opencivilizationplatform.core.eventbus.events;

import io.github.opencivilizationplatform.core.eventbus.BaseDomainEvent;

public class ContributionSubmittedEvent extends BaseDomainEvent {
    private final Long contributionId;
    private final Long projectId;
    private final Long citizenId;
    private final Double amount;

    public ContributionSubmittedEvent(String source, Long contributionId, Long projectId,
                                      Long citizenId, Double amount) {
        super(source, "contribution", "submitted");
        this.contributionId = contributionId;
        this.projectId = projectId;
        this.citizenId = citizenId;
        this.amount = amount;
    }

    public Long getContributionId() { return contributionId; }
    public Long getProjectId() { return projectId; }
    public Long getCitizenId() { return citizenId; }
    public Double getAmount() { return amount; }
}
