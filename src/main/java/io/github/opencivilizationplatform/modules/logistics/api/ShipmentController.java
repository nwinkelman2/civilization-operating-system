package io.github.opencivilizationplatform.modules.logistics.api;

import io.github.opencivilizationplatform.modules.logistics.application.ShipmentService;
import io.github.opencivilizationplatform.modules.logistics.domain.Shipment;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/shipments")
public class ShipmentController {

    private final ShipmentService shipmentService;

    public ShipmentController(ShipmentService shipmentService) {
        this.shipmentService = shipmentService;
    }

    @GetMapping
    public Page<Shipment> getAllShipments(Pageable pageable) {
        return shipmentService.getAllShipments(pageable);
    }

    @PostMapping
    public Shipment saveShipment(@Valid @RequestBody Shipment shipment) {
        return shipmentService.saveShipment(shipment);
    }
}
