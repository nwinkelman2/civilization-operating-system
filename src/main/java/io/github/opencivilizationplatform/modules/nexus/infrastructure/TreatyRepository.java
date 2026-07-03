package io.github.opencivilizationplatform.modules.nexus.infrastructure;

import io.github.opencivilizationplatform.modules.nexus.domain.Treaty;
import io.github.opencivilizationplatform.modules.nexus.domain.TreatyStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TreatyRepository extends JpaRepository<Treaty, Long> {
    List<Treaty> findByStatus(TreatyStatus status);
    List<Treaty> findByProposerCivIdOrInvitedCivIdsContaining(Long proposerCivId, String civIdStr);
}
