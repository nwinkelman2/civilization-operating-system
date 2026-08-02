package io.github.opencivilizationplatform.modules.needs.infrastructure;

import io.github.opencivilizationplatform.modules.needs.domain.Need;
import io.github.opencivilizationplatform.modules.needs.domain.NeedCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NeedRepository extends JpaRepository<Need, Long> {
    List<Need> findByRegion(String region);
    List<Need> findByCategory(NeedCategory category);
}
