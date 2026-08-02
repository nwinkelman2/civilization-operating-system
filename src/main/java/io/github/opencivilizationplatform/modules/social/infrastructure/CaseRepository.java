package io.github.opencivilizationplatform.modules.social.infrastructure;

import io.github.opencivilizationplatform.modules.social.domain.Case;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CaseRepository extends JpaRepository<Case, Long> {
}
