package io.github.opencivilizationplatform.modules.nexus.infrastructure;

import io.github.opencivilizationplatform.modules.nexus.domain.MigrationRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MigrationRequestRepository extends JpaRepository<MigrationRequest, Long> {
    List<MigrationRequest> findByToCivilizationIdAndStatus(Long toCivilizationId, String status);
    List<MigrationRequest> findAllByOrderByCreatedAtDesc();
}
