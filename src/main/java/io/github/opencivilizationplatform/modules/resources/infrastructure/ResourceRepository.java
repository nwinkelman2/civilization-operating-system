package io.github.opencivilizationplatform.modules.resources.infrastructure;

import io.github.opencivilizationplatform.modules.resources.domain.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, Long> {
}
