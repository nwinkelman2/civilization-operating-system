package io.github.opencivilizationplatform.modules.monitoring.application;

import io.github.opencivilizationplatform.modules.monitoring.domain.BiosphereMetric;
import io.github.opencivilizationplatform.modules.monitoring.domain.BiosphereMetricStatus;
import io.github.opencivilizationplatform.modules.monitoring.infrastructure.BiosphereMetricRepository;
import io.github.opencivilizationplatform.core.event.BiosphereCriticalEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BiosphereMetricServiceTest {

    @Mock
    private BiosphereMetricRepository metricRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private BiosphereMetricService biosphereMetricService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllMetrics() {
        BiosphereMetric metric1 = new BiosphereMetric();
        BiosphereMetric metric2 = new BiosphereMetric();
        when(metricRepository.findAll()).thenReturn(Arrays.asList(metric1, metric2));

        List<BiosphereMetric> result = biosphereMetricService.getAllMetrics();

        assertEquals(2, result.size());
        verify(metricRepository, times(1)).findAll();
    }

    @Test
    void testSaveNormalMetric() {
        BiosphereMetric metric = new BiosphereMetric();
        metric.setStatus(BiosphereMetricStatus.NORMAL);
        when(metricRepository.save(any(BiosphereMetric.class))).thenReturn(metric);

        BiosphereMetric saved = biosphereMetricService.saveMetric(metric);

        assertNotNull(saved);
        assertEquals(BiosphereMetricStatus.NORMAL, saved.getStatus());
        verify(eventPublisher, never()).publishEvent(any(BiosphereCriticalEvent.class));
    }

    @Test
    void testSaveCriticalMetricPublishesEvent() {
        BiosphereMetric metric = new BiosphereMetric();
        metric.setStatus(BiosphereMetricStatus.CRITICAL);
        when(metricRepository.save(any(BiosphereMetric.class))).thenReturn(metric);

        BiosphereMetric saved = biosphereMetricService.saveMetric(metric);

        assertNotNull(saved);
        assertEquals(BiosphereMetricStatus.CRITICAL, saved.getStatus());
        verify(eventPublisher, times(1)).publishEvent(any(BiosphereCriticalEvent.class));
    }
}
