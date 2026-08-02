package io.github.opencivilizationplatform.modules.social.infrastructure;

import io.github.opencivilizationplatform.modules.social.domain.BehaviorAssessment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BehaviorAssessmentRepository extends JpaRepository<BehaviorAssessment, Long> {
    List<BehaviorAssessment> findByCitizenId(String citizenId);
}
