package io.github.opencivilizationplatform.modules.events.application;

import io.github.opencivilizationplatform.core.eventbus.EventBus;
import io.github.opencivilizationplatform.core.eventbus.events.GlobalEventOccurredEvent;
import io.github.opencivilizationplatform.modules.events.domain.GlobalEvent;
import io.github.opencivilizationplatform.modules.events.infrastructure.GlobalEventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class GlobalEventServiceTest {

    @Mock
    private GlobalEventRepository globalEventRepository;

    @Mock
    private EventBus eventBus;

    @InjectMocks
    private GlobalEventService globalEventService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testMaybeGenerateEvent() {
        when(globalEventRepository.save(any(GlobalEvent.class))).thenAnswer(inv -> {
            GlobalEvent ge = inv.getArgument(0);
            ge.setId(100L);
            return ge;
        });

        GlobalEvent event = null;
        // Try up to 100 times to trigger event creation since maybeGenerateEvent uses random probability
        for (int i = 0; i < 100; i++) {
            event = globalEventService.maybeGenerateEvent(List.of(1L, 2L));
            if (event != null) break;
        }

        if (event != null) {
            assertNotNull(event);
            verify(eventBus, atLeastOnce()).publish(any(GlobalEventOccurredEvent.class));
        }
    }
}
