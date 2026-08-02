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
        super(source);
        this.civilizationId = civilizationId;
        this.name = name;
        this.region = region;
        this.scale = scale;
        this.ownerToken = ownerToken;
    }

    @Override
    public String getType() { return "CIVILIZATION_CREATED"; }

    public Long getCivilizationId() { return civilizationId; }
    public String getName() { return name; }
    public String getRegion() { return region; }
    public CivilizationScale getScale() { return scale; }
    public String getOwnerToken() { return ownerToken; }
}
