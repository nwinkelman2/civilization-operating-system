package io.github.opencivilizationplatform.modules.execution.infrastructure;

import io.github.opencivilizationplatform.modules.execution.domain.AutomationUnit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AutomationUnitRepository extends JpaRepository<AutomationUnit, Long> {
}
