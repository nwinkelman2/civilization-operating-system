package io.github.opencivilizationplatform.modules.production.api;

import io.github.opencivilizationplatform.modules.production.domain.Facility;
import io.github.opencivilizationplatform.modules.production.infrastructure.FacilityRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/facilities")
public class FacilityController {

    private final FacilityRepository facilityRepository;

    public FacilityController(FacilityRepository facilityRepository) {
        this.facilityRepository = facilityRepository;
    }

    @GetMapping
    public java.util.List<Facility> getAllFacilities() {
        return facilityRepository.findAll();
    }

    @PostMapping
    public Facility saveFacility(@RequestBody Facility facility) {
        return facilityRepository.save(facility);
    }
}
