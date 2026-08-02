package io.github.opencivilizationplatform.modules.region.application;

import io.github.opencivilizationplatform.config.seed.CivilizationScale;
import io.github.opencivilizationplatform.modules.region.domain.ResourceRegion;
import io.github.opencivilizationplatform.modules.region.infrastructure.ResourceRegionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class ResourceRegionService {

    private final ResourceRegionRepository repository;

    public ResourceRegionService(ResourceRegionRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<ResourceRegion> getAllRegions() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public List<ResourceRegion> getAvailableRegions() {
        return repository.findByClaimedFalse();
    }

    @Transactional(readOnly = true)
    public List<ResourceRegion> getRegionsByScale(CivilizationScale scale) {
        return repository.findByScale(scale);
    }

    @Transactional(readOnly = true)
    public ResourceRegion getRegion(Long id) {
        return repository.findById(id).orElseThrow();
    }

    @Transactional
    public ResourceRegion claimRegion(Long regionId, Long civilizationId) {
        ResourceRegion region = repository.findById(regionId).orElseThrow();
        region.setClaimed(true);
        region.setClaimedByCivilizationId(civilizationId);
        return repository.save(region);
    }

    @Transactional
    public ResourceRegion unclaimRegion(Long regionId) {
        ResourceRegion region = repository.findById(regionId).orElseThrow();
        region.setClaimed(false);
        region.setClaimedByCivilizationId(null);
        return repository.save(region);
    }

    @Transactional(readOnly = true)
    public long countAvailable() {
        return repository.countByClaimed(false);
    }

    @Transactional(readOnly = true)
    public long countTotal() {
        return repository.count();
    }
}
