package io.github.opencivilizationplatform.saga;

import io.github.opencivilizationplatform.config.seed.CivilizationScale;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/sagas")
@Tag(name = "Sagas", description = "Distributed transaction sagas for multi-step operations")
public class SagaController {

    private final SagaOrchestrator orchestrator;
    private final FoundCivilizationSagaSteps foundSteps;

    public SagaController(SagaOrchestrator orchestrator, FoundCivilizationSagaSteps foundSteps) {
        this.orchestrator = orchestrator;
        this.foundSteps = foundSteps;
    }

    @PostMapping("/found-civilization")
    @Operation(summary = "Execute FoundCivilization saga (create civ → claim region → deploy voxtex)")
    public Map<String, Object> foundCivilization(@RequestBody FoundCivilizationRequest request) {
        FoundCivilizationContext ctx = new FoundCivilizationContext();
        ctx.setName(request.name());
        ctx.setScale(request.scale() != null ? request.scale() : CivilizationScale.LOCAL);
        ctx.setRegionId(request.regionId());
        ctx.setOwnerToken("saga-user");

        orchestrator.execute("FoundCivilization", ctx, List.of(
            foundSteps.createCivilization(),
            foundSteps.claimRegion(),
            foundSteps.deployVoxtexNode()
        ));

        return Map.of(
            "status", "COMPLETED",
            "civilizationId", ctx.getCivilization().getId(),
            "name", ctx.getCivilization().getName()
        );
    }
}

record FoundCivilizationRequest(String name, CivilizationScale scale, Long regionId) {}
