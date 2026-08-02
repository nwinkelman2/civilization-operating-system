package io.github.opencivilizationplatform.modules.monitoring.api;

import io.github.opencivilizationplatform.modules.monitoring.application.BiosphereMetricService;
import io.github.opencivilizationplatform.modules.monitoring.domain.BiosphereMetric;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/biosphere")
@Tag(name = "Biosphere Metrics", description = "Biosphere metric management endpoints")
public class BiosphereMetricController {

    private final BiosphereMetricService biosphereMetricService;

    public BiosphereMetricController(BiosphereMetricService biosphereMetricService) {
        this.biosphereMetricService = biosphereMetricService;
    }

    @GetMapping
    @Operation(summary = "List all biosphere metrics", description = "Returns a paginated list of biosphere metrics")
    public Page<BiosphereMetric> getAllMetrics(Pageable pageable) {
        return biosphereMetricService.getAllMetrics(pageable);
    }

    @PostMapping
    @Operation(summary = "Create a biosphere metric", description = "Creates a new biosphere metric record")
    public BiosphereMetric saveMetric(@Valid @RequestBody BiosphereMetric metric) {
        return biosphereMetricService.saveMetric(metric);
    }
}
