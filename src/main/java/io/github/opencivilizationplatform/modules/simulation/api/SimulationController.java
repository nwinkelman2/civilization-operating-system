package io.github.opencivilizationplatform.modules.simulation.api;

import io.github.opencivilizationplatform.modules.simulation.api.dto.SimulationStatusResponse;
import io.github.opencivilizationplatform.modules.simulation.application.SimulationEngineService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/simulation")
@Tag(name = "Simulation", description = "Simulation management endpoints")
public class SimulationController {

    private final SimulationEngineService simulationEngineService;

    public SimulationController(SimulationEngineService simulationEngineService) {
        this.simulationEngineService = simulationEngineService;
    }

    @GetMapping("/status")
    @Operation(summary = "Get simulation status", description = "Returns the current simulation engine status")
    public SimulationStatusResponse getStatus() {
        return simulationEngineService.getStatus();
    }
}
