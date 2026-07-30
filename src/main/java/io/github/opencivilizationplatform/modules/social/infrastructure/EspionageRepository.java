package io.github.opencivilizationplatform.modules.social.infrastructure;

import io.github.opencivilizationplatform.modules.social.domain.EspionageOperation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EspionageRepository extends JpaRepository<EspionageOperation, Long> {
    List<EspionageOperation> findByInitiatorId(Long initiatorId);
    List<EspionageOperation> findByTargetId(Long targetId);
    List<EspionageOperation> findByStatus(String status);
    List<EspionageOperation> findByInitiatorIdOrTargetId(Long initiatorId, Long targetId);
}
