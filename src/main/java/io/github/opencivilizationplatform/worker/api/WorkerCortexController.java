package io.github.opencivilizationplatform.worker.api;

import io.github.opencivilizationplatform.modules.cortex.cortex.CortexEngineService;
import io.github.opencivilizationplatform.modules.simulation.application.SimulationEngineService;
import io.github.opencivilizationplatform.modules.simulation.api.dto.SimulationStatusResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/worker")
@Tag(name = "Cortex Worker", description = "Dedicated worker endpoints for the Cortex simulation engine")
public class WorkerCortexController {

    private final CortexEngineService cortexEngine;
    private final SimulationEngineService simulationEngine;

    public WorkerCortexController(CortexEngineService cortexEngine, SimulationEngineService simulationEngine) {
        this.cortexEngine = cortexEngine;
        this.simulationEngine = simulationEngine;
    }

    @PostMapping("/tick")
    @Operation(summary = "Execute a single cortex tick for all civilizations")
    public Map<String, Object> executeTick() {
        cortexEngine.tick();
        return Map.of("status", "ok", "timestamp", LocalDateTime.now().toString());
    }

    @PostMapping("/tick/{civilizationId}")
    @Operation(summary = "Execute a cortex tick for a specific civilization")
    public Map<String, Object> executeTickForCiv(@PathVariable Long civilizationId) {
        cortexEngine.tickForCivilization(civilizationId);
        return Map.of("status", "ok", "civilizationId", civilizationId);
    }

    @GetMapping("/health")
    @Operation(summary = "Worker health check")
    public Map<String, Object> health() {
        LocalDateTime lastTick = cortexEngine.getLastTickTime();
        Duration sinceLastTick = Duration.between(lastTick, LocalDateTime.now());
        return Map.of(
            "status", "UP",
            "lastCortexTick", lastTick.toString(),
            "secondsSinceLastTick", sinceLastTick.toSeconds()
        );
    }

    @GetMapping("/status")
    @Operation(summary = "Worker status and metrics")
    public SimulationStatusResponse status() {
        return simulationEngine.getStatus();
    }
}
