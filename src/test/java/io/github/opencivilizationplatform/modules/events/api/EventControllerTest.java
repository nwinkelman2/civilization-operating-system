package io.github.opencivilizationplatform.modules.events.api;

import io.github.opencivilizationplatform.modules.events.application.EventService;
import io.github.opencivilizationplatform.modules.events.domain.EventSeverity;
import io.github.opencivilizationplatform.modules.events.domain.EventType;
import io.github.opencivilizationplatform.modules.events.domain.GameEvent;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import java.util.List;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EventControllerTest {

    private MockMvc mockMvc;
    private EventService eventService;

    @BeforeEach
    void setUp() {
        eventService = mock(EventService.class);
        mockMvc = standaloneSetup(new EventController(eventService)).build();
    }

    @Test
    void testGetEvents() throws Exception {
        GameEvent event = new GameEvent();
        event.setId(1L);
        event.setTitle("Earthquake");
        event.setDescription("A major earthquake struck the region");
        event.setType(EventType.NATURAL_DISASTER);
        event.setSeverity(EventSeverity.MAJOR);
        event.setTargetCivilizationId(1L);
        when(eventService.getEventsForCivilization(1L)).thenReturn(List.of(event));
        mockMvc.perform(get("/api/v1/events/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Earthquake"))
                .andExpect(jsonPath("$[0].type").value("NATURAL_DISASTER"))
                .andExpect(jsonPath("$[0].severity").value("MAJOR"));
    }
}