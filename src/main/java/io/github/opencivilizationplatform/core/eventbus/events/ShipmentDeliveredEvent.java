package io.github.opencivilizationplatform.core.eventbus.events;

import io.github.opencivilizationplatform.core.eventbus.BaseDomainEvent;

public class ShipmentDeliveredEvent extends BaseDomainEvent {
    private final Long shipmentId;
    private final String originRegion;
    private final String destinationRegion;
    private final Double quantity;

    public ShipmentDeliveredEvent(String source, Long shipmentId, String originRegion,
                                   String destinationRegion, Double quantity) {
        super(source, "logistics", "shipment_delivered");
        this.shipmentId = shipmentId;
        this.originRegion = originRegion;
        this.destinationRegion = destinationRegion;
        this.quantity = quantity;
    }

    public Long getShipmentId() { return shipmentId; }
    public String getOriginRegion() { return originRegion; }
    public String getDestinationRegion() { return destinationRegion; }
    public Double getQuantity() { return quantity; }
}
