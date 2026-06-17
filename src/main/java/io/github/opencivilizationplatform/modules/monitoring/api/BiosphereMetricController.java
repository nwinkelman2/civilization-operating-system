package io.github.opencivilizationplatform.modules.monitoring.api;

import io.github.opencivilizationplatform.modules.monitoring.domain.BiosphereMetric;
import io.github.opencivilizationplatform.modules.monitoring.infrastructure.BiosphereMetricRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/biosphere")
public class BiosphereMetricController {

    private final BiosphereMetricRepository metricRepository;

    public BiosphereMetricController(BiosphereMetricRepository metricRepository) {
        this.metricRepository = metricRepository;
    }

    @GetMapping
    public java.util.List<BiosphereMetric> getAllMetrics() {
        return metricRepository.findAll();
    }

    @PostMapping
    public BiosphereMetric saveMetric(@RequestBody BiosphereMetric metric) {
        return metricRepository.save(metric);
    }
}
