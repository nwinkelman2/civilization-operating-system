package io.github.opencivilizationplatform.modules.production.api;

import io.github.opencivilizationplatform.modules.production.application.FacilityService;
import io.github.opencivilizationplatform.modules.production.domain.Facility;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping({"/api/v1/facilities", "/api/v1/production/facilities"})
@Tag(name = "Facilities", description = "Facility management endpoints")
public class FacilityController {

    private final FacilityService facilityService;

    public FacilityController(FacilityService facilityService) {
        this.facilityService = facilityService;
    }

    @GetMapping
    @Operation(summary = "List all facilities", description = "Returns a paginated list of facilities")
    public Page<Facility> getAllFacilities(Pageable pageable) {
        return facilityService.getAllFacilities(pageable);
    }

    @PostMapping
    @Operation(summary = "Create a facility", description = "Creates a new facility record")
    public Facility saveFacility(@Valid @RequestBody Facility facility) {
        return facilityService.saveFacility(facility);
    }
}
