package io.github.opencivilizationplatform.modules.monitoring.application;

import io.github.opencivilizationplatform.modules.monitoring.domain.BiosphereMetric;
import io.github.opencivilizationplatform.modules.monitoring.domain.BiosphereMetricStatus;
import io.github.opencivilizationplatform.modules.monitoring.infrastructure.BiosphereMetricRepository;
import io.github.opencivilizationplatform.core.event.BiosphereCriticalEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class BiosphereMetricService {
    private final BiosphereMetricRepository metricRepository;
    private final ApplicationEventPublisher eventPublisher;

    public BiosphereMetricService(BiosphereMetricRepository metricRepository, ApplicationEventPublisher eventPublisher) {
        this.metricRepository = metricRepository;
        this.eventPublisher = eventPublisher;
    }

    public Page<BiosphereMetric> getAllMetrics(Pageable pageable) {
        return metricRepository.findAll(pageable);
    }

    public BiosphereMetric saveMetric(BiosphereMetric metric) {
        BiosphereMetric saved = metricRepository.save(metric);
        if (BiosphereMetricStatus.CRITICAL.equals(saved.getStatus())) {
            eventPublisher.publishEvent(new BiosphereCriticalEvent(this, saved));
        }
        return saved;
    }
}
