package io.github.opencivilizationplatform.modules.contribution.infrastructure;

import io.github.opencivilizationplatform.modules.contribution.domain.Project;
import io.github.opencivilizationplatform.modules.contribution.domain.ProjectStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByStatus(ProjectStatus status);
    List<Project> findByCivilizationId(Long civilizationId);
    List<Project> findByCivilizationIdAndStatus(Long civilizationId, ProjectStatus status);
}
