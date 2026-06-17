package io.github.opencivilizationplatform.modules.monitoring.application;

import io.github.opencivilizationplatform.modules.monitoring.domain.BiosphereMetric;
import io.github.opencivilizationplatform.modules.monitoring.domain.BiosphereMetricStatus;
import io.github.opencivilizationplatform.modules.monitoring.infrastructure.BiosphereMetricRepository;
import io.github.opencivilizationplatform.core.event.BiosphereCriticalEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BiosphereMetricService {
    private final BiosphereMetricRepository metricRepository;
    private final ApplicationEventPublisher eventPublisher;

    public BiosphereMetricService(BiosphereMetricRepository metricRepository, ApplicationEventPublisher eventPublisher) {
        this.metricRepository = metricRepository;
        this.eventPublisher = eventPublisher;
    }

    public List<BiosphereMetric> getAllMetrics() {
        return metricRepository.findAll();
    }

    public BiosphereMetric saveMetric(BiosphereMetric metric) {
        BiosphereMetric saved = metricRepository.save(metric);
        if (BiosphereMetricStatus.CRITICAL.equals(saved.getStatus())) {
            eventPublisher.publishEvent(new BiosphereCriticalEvent(this, saved));
        }
        return saved;
    }
}
