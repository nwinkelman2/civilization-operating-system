package io.github.opencivilizationplatform.modules.monitoring.infrastructure;

import io.github.opencivilizationplatform.modules.monitoring.domain.BiosphereMetric;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BiosphereMetricRepository extends JpaRepository<BiosphereMetric, Long> {
}
