package io.github.opencivilizationplatform.modules.participation.api;

import io.github.opencivilizationplatform.modules.participation.application.InteractionService;
import io.github.opencivilizationplatform.modules.participation.domain.Interaction;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/interactions")
public class InteractionController {

    private final InteractionService interactionService;

    public InteractionController(InteractionService interactionService) {
        this.interactionService = interactionService;
    }

    @GetMapping
    public Page<Interaction> getAllInteractions(Pageable pageable) {
        return interactionService.getAllInteractions(pageable);
    }

    @PostMapping
    public Interaction saveInteraction(@Valid @RequestBody Interaction interaction) {
        return interactionService.saveInteraction(interaction);
    }
}
