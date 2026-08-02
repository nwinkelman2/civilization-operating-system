package io.github.opencivilizationplatform.modules.logistics.api;

import io.github.opencivilizationplatform.modules.logistics.application.ShipmentService;
import io.github.opencivilizationplatform.modules.logistics.domain.Shipment;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/shipments")
@Tag(name = "Shipments", description = "Shipment management endpoints")
public class ShipmentController {

    private final ShipmentService shipmentService;

    public ShipmentController(ShipmentService shipmentService) {
        this.shipmentService = shipmentService;
    }

    @GetMapping
    @Operation(summary = "List all shipments", description = "Returns a paginated list of shipments")
    public Page<Shipment> getAllShipments(Pageable pageable) {
        return shipmentService.getAllShipments(pageable);
    }

    @PostMapping
    @Operation(summary = "Create a shipment", description = "Creates a new shipment record")
    public Shipment saveShipment(@Valid @RequestBody Shipment shipment) {
        return shipmentService.saveShipment(shipment);
    }

    @GetMapping("/active")
    @Operation(summary = "List active shipments", description = "Returns shipments currently IN_TRANSIT for map rendering")
    public java.util.List<Shipment> getActiveShipments() {
        return shipmentService.getActiveShipments();
    }
}
