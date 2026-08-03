package io.github.opencivilizationplatform.core.eventbus.events;

import io.github.opencivilizationplatform.core.eventbus.BaseDomainEvent;

public class ElectionCompletedEvent extends BaseDomainEvent {
    private final Long electionId;
    private final Long civilizationId;
    private final Long winnerCitizenId;

    public ElectionCompletedEvent(String source, Long electionId, Long civilizationId, Long winnerCitizenId) {
        super(source, "governance", "election_completed");
        this.electionId = electionId;
        this.civilizationId = civilizationId;
        this.winnerCitizenId = winnerCitizenId;
    }

    public Long getElectionId() { return electionId; }
    public Long getCivilizationId() { return civilizationId; }
    public Long getWinnerCitizenId() { return winnerCitizenId; }
}
