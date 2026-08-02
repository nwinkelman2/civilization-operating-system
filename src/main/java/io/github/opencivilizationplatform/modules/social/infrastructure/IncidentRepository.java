package io.github.opencivilizationplatform.modules.social.infrastructure;

import io.github.opencivilizationplatform.modules.social.domain.Incident;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IncidentRepository extends JpaRepository<Incident, Long> {
    List<Incident> findByCivilizationId(Long civilizationId);
    List<Incident> findByCivilizationIdAndStatus(Long civilizationId, io.github.opencivilizationplatform.modules.social.domain.IncidentStatus status);
}
