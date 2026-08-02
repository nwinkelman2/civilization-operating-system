package io.github.opencivilizationplatform.modules.logistics.application;

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

    public ShipmentService(ShipmentRepository shipmentRepository) {
        this.shipmentRepository = shipmentRepository;
    }

    public Page<Shipment> getAllShipments(Pageable pageable) {
        return shipmentRepository.findAll(pageable);
    }

    public List<Shipment> getActiveShipments() {
        return shipmentRepository.findByStatus(ShipmentStatus.IN_TRANSIT);
    }

    public Shipment saveShipment(Shipment shipment) {
        return shipmentRepository.save(shipment);
    }
}
