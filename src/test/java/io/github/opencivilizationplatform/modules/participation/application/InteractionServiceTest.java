package io.github.opencivilizationplatform.modules.participation.application;

import io.github.opencivilizationplatform.modules.participation.domain.Interaction;
import io.github.opencivilizationplatform.modules.participation.infrastructure.InteractionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class InteractionServiceTest {

    @Mock
    private InteractionRepository interactionRepository;

    @InjectMocks
    private InteractionService interactionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllInteractions() {
        when(interactionRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(Arrays.asList(new Interaction())));
        Page<Interaction> result = interactionService.getAllInteractions(Pageable.unpaged());
        assertEquals(1, result.getContent().size());
    }

    @Test
    void testSaveInteraction() {
        Interaction i = new Interaction();
        when(interactionRepository.save(i)).thenReturn(i);
        assertNotNull(interactionService.saveInteraction(i));
    }
}
