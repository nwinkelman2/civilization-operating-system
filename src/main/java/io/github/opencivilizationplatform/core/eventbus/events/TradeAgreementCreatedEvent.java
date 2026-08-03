package io.github.opencivilizationplatform.core.eventbus.events;

import io.github.opencivilizationplatform.core.eventbus.BaseDomainEvent;

public class TradeAgreementCreatedEvent extends BaseDomainEvent {
    private final Long tradeId;
    private final Long fromCivilizationId;
    private final Long toCivilizationId;
    private final String resourceType;
    private final double quantity;

    public TradeAgreementCreatedEvent(String source, Long tradeId, Long fromCivilizationId,
                                       Long toCivilizationId, String resourceType, double quantity) {
        super(source, "trade", "agreement_created");
        this.tradeId = tradeId;
        this.fromCivilizationId = fromCivilizationId;
        this.toCivilizationId = toCivilizationId;
        this.resourceType = resourceType;
        this.quantity = quantity;
    }

    public Long getTradeId() { return tradeId; }
    public Long getFromCivilizationId() { return fromCivilizationId; }
    public Long getToCivilizationId() { return toCivilizationId; }
    public String getResourceType() { return resourceType; }
    public double getQuantity() { return quantity; }
}
