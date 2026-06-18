package io.github.opencivilizationplatform.modules.production.api;

import io.github.opencivilizationplatform.modules.production.application.FacilityService;
import io.github.opencivilizationplatform.modules.production.domain.Facility;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/facilities")
public class FacilityController {

    private final FacilityService facilityService;

    public FacilityController(FacilityService facilityService) {
        this.facilityService = facilityService;
    }

    @GetMapping
    public Page<Facility> getAllFacilities(Pageable pageable) {
        return facilityService.getAllFacilities(pageable);
    }

    @PostMapping
    public Facility saveFacility(@Valid @RequestBody Facility facility) {
        return facilityService.saveFacility(facility);
    }
}
