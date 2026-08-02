package io.github.opencivilizationplatform.modules.participation.api;

import io.github.opencivilizationplatform.modules.participation.application.InteractionService;
import io.github.opencivilizationplatform.modules.participation.domain.Interaction;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/interactions")
@Tag(name = "Interactions", description = "Interaction management endpoints")
public class InteractionController {

    private final InteractionService interactionService;

    public InteractionController(InteractionService interactionService) {
        this.interactionService = interactionService;
    }

    @GetMapping
    @Operation(summary = "List all interactions", description = "Returns a paginated list of interactions")
    public Page<Interaction> getAllInteractions(Pageable pageable) {
        return interactionService.getAllInteractions(pageable);
    }

    @PostMapping
    @Operation(summary = "Create an interaction", description = "Creates a new interaction record")
    public Interaction saveInteraction(@Valid @RequestBody Interaction interaction) {
        return interactionService.saveInteraction(interaction);
    }
}
