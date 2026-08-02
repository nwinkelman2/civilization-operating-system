package io.github.opencivilizationplatform.modules.production.application;

import io.github.opencivilizationplatform.modules.production.domain.Facility;
import io.github.opencivilizationplatform.modules.production.infrastructure.FacilityRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class FacilityService {
    private final FacilityRepository facilityRepository;

    public FacilityService(FacilityRepository facilityRepository) {
        this.facilityRepository = facilityRepository;
    }

    public Page<Facility> getAllFacilities(Pageable pageable) {
        return facilityRepository.findAll(pageable);
    }

    public Facility saveFacility(Facility facility) {
        return facilityRepository.save(facility);
    }
}
