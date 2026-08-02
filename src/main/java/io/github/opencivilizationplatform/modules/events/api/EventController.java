package io.github.opencivilizationplatform.modules.events.api;

import io.github.opencivilizationplatform.modules.events.application.EventService;
import io.github.opencivilizationplatform.modules.events.domain.EventSeverity;
import io.github.opencivilizationplatform.modules.events.domain.EventType;
import io.github.opencivilizationplatform.modules.events.domain.GameEvent;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/events")
@Tag(name = "Game Events", description = "Random game events and scenarios")
public class EventController {

    private final EventService service;

    public EventController(EventService service) {
        this.service = service;
    }

    @GetMapping("/{civId}")
    @Operation(summary = "Get events for a civilization")
    public List<GameEvent> getEvents(@PathVariable Long civId) {
        return service.getEventsForCivilization(civId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a game event (admin)")
    public GameEvent createEvent(@RequestBody Map<String, String> body) {
        return service.createEvent(
            body.get("title"), body.get("description"),
            Enum.valueOf(EventType.class, body.get("type")),
            Enum.valueOf(EventSeverity.class, body.get("severity")),
            Long.valueOf(body.get("targetCivilizationId")),
            body.getOrDefault("effectJson", "{}")
        );
    }

    @PostMapping("/{id}/resolve")
    @Operation(summary = "Resolve an event")
    public void resolve(@PathVariable Long id) {
        service.resolveEvent(id);
    }
}
