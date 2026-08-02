package io.github.opencivilizationplatform.modules.production.infrastructure;

import io.github.opencivilizationplatform.modules.production.domain.Facility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FacilityRepository extends JpaRepository<Facility, Long> {
}
