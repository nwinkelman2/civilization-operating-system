package io.github.opencivilizationplatform.modules.logistics.application;

import io.github.opencivilizationplatform.core.eventbus.EventBus;
import io.github.opencivilizationplatform.core.eventbus.events.ShipmentDeliveredEvent;
import io.github.opencivilizationplatform.modules.logistics.domain.Shipment;
import io.github.opencivilizationplatform.modules.logistics.domain.ShipmentStatus;
import io.github.opencivilizationplatform.modules.logistics.infrastructure.ShipmentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ShipmentService {
    private final ShipmentRepository shipmentRepository;
    private final EventBus eventBus;

    public ShipmentService(ShipmentRepository shipmentRepository, EventBus eventBus) {
        this.shipmentRepository = shipmentRepository;
        this.eventBus = eventBus;
    }

    public Page<Shipment> getAllShipments(Pageable pageable) {
        return shipmentRepository.findAll(pageable);
    }

    public List<Shipment> getActiveShipments() {
        return shipmentRepository.findByStatus(ShipmentStatus.IN_TRANSIT);
    }

    public Shipment saveShipment(Shipment shipment) {
        Shipment saved = shipmentRepository.save(shipment);
        if (saved.getStatus() == ShipmentStatus.DELIVERED) {
            eventBus.publish(new ShipmentDeliveredEvent(
                "ShipmentService",
                saved.getId(),
                saved.getOrigin(),
                saved.getDestination(),
                saved.getQuantity()
            ));
        }
        return saved;
    }
}
