package io.github.opencivilizationplatform.modules.nexus.infrastructure;

import io.github.opencivilizationplatform.modules.nexus.domain.Election;
import io.github.opencivilizationplatform.modules.nexus.domain.ElectionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ElectionRepository extends JpaRepository<Election, Long> {
    List<Election> findByCivilizationId(Long civilizationId);
    Optional<Election> findByCivilizationIdAndStatus(Long civilizationId, ElectionStatus status);
}
