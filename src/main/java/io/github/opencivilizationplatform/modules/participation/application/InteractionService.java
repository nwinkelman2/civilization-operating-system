package io.github.opencivilizationplatform.modules.participation.application;

import io.github.opencivilizationplatform.modules.participation.domain.Interaction;
import io.github.opencivilizationplatform.modules.participation.infrastructure.InteractionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class InteractionService {
    private final InteractionRepository interactionRepository;

    public InteractionService(InteractionRepository interactionRepository) {
        this.interactionRepository = interactionRepository;
    }

    public Page<Interaction> getAllInteractions(Pageable pageable) {
        return interactionRepository.findAll(pageable);
    }

    public Interaction saveInteraction(Interaction interaction) {
        return interactionRepository.save(interaction);
    }
}
