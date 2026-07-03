package io.github.opencivilizationplatform.modules.events.api;

import io.github.opencivilizationplatform.modules.events.application.GlobalEventService;
import io.github.opencivilizationplatform.modules.events.domain.GlobalEvent;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/global-events")
public class GlobalEventsController {

    private final GlobalEventService globalEventService;

    public GlobalEventsController(GlobalEventService globalEventService) {
        this.globalEventService = globalEventService;
    }

    @GetMapping
    @Operation(summary = "Get all active global events")
    public List<GlobalEvent> getActiveEvents() {
        return globalEventService.getActiveEvents();
    }
}
