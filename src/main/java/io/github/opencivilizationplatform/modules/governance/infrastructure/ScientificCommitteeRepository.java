package io.github.opencivilizationplatform.modules.governance.infrastructure;

import io.github.opencivilizationplatform.modules.governance.domain.ScientificCommittee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScientificCommitteeRepository extends JpaRepository<ScientificCommittee, Long> {
}
