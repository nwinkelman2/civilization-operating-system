package io.github.opencivilizationplatform.modules.monitoring.api;

import io.github.opencivilizationplatform.modules.monitoring.application.BiosphereMetricService;
import io.github.opencivilizationplatform.modules.monitoring.domain.BiosphereMetric;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/biosphere")
public class BiosphereMetricController {

    private final BiosphereMetricService biosphereMetricService;

    public BiosphereMetricController(BiosphereMetricService biosphereMetricService) {
        this.biosphereMetricService = biosphereMetricService;
    }

    @GetMapping
    public Page<BiosphereMetric> getAllMetrics(Pageable pageable) {
        return biosphereMetricService.getAllMetrics(pageable);
    }

    @PostMapping
    public BiosphereMetric saveMetric(@Valid @RequestBody BiosphereMetric metric) {
        return biosphereMetricService.saveMetric(metric);
    }
}
