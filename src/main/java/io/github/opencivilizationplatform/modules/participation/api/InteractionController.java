package io.github.opencivilizationplatform.modules.participation.api;

import io.github.opencivilizationplatform.modules.participation.domain.Interaction;
import io.github.opencivilizationplatform.modules.participation.infrastructure.InteractionRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/interactions")
public class InteractionController {

    private final InteractionRepository interactionRepository;

    public InteractionController(InteractionRepository interactionRepository) {
        this.interactionRepository = interactionRepository;
    }

    @GetMapping
    public java.util.List<Interaction> getAllInteractions() {
        return interactionRepository.findAll();
    }

    @PostMapping
    public Interaction saveInteraction(@RequestBody Interaction interaction) {
        return interactionRepository.save(interaction);
    }
}
