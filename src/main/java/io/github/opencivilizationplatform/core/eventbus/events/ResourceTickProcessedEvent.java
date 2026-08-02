package io.github.opencivilizationplatform.core.eventbus.events;

import io.github.opencivilizationplatform.core.eventbus.BaseDomainEvent;

public class ResourceTickProcessedEvent extends BaseDomainEvent {
    private final Long civilizationId;
    private final double foodDelta;
    private final double waterDelta;
    private final double mineralsDelta;
    private final double energyDelta;
    private final double housingDelta;
    private final double populationDelta;
    private final double reputationDelta;

    public ResourceTickProcessedEvent(String source, Long civilizationId,
                                       double foodDelta, double waterDelta, double mineralsDelta,
                                       double energyDelta, double housingDelta,
                                       double populationDelta, double reputationDelta) {
        super(source);
        this.civilizationId = civilizationId;
        this.foodDelta = foodDelta;
        this.waterDelta = waterDelta;
        this.mineralsDelta = mineralsDelta;
        this.energyDelta = energyDelta;
        this.housingDelta = housingDelta;
        this.populationDelta = populationDelta;
        this.reputationDelta = reputationDelta;
    }

    @Override
    public String getType() { return "RESOURCE_TICK_PROCESSED"; }

    public Long getCivilizationId() { return civilizationId; }
    public double getFoodDelta() { return foodDelta; }
    public double getWaterDelta() { return waterDelta; }
    public double getMineralsDelta() { return mineralsDelta; }
    public double getEnergyDelta() { return energyDelta; }
    public double getHousingDelta() { return housingDelta; }
    public double getPopulationDelta() { return populationDelta; }
    public double getReputationDelta() { return reputationDelta; }
}
