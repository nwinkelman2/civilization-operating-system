package io.github.opencivilizationplatform.modules.region.infrastructure;
import io.github.opencivilizationplatform.config.seed.CivilizationScale;
import io.github.opencivilizationplatform.modules.region.domain.ResourceRegion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ResourceRegionRepository extends JpaRepository<ResourceRegion, Long> {
    List<ResourceRegion> findByScale(CivilizationScale scale);
    List<ResourceRegion> findByClaimedFalse();
    long countByClaimed(boolean claimed);
}
