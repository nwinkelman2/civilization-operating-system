package io.github.opencivilizationplatform.modules.technology.api;

import io.github.opencivilizationplatform.modules.technology.application.TechnologyService;
import io.github.opencivilizationplatform.modules.technology.domain.Technology;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/tech-tree")
@Tag(name = "Tech Tree", description = "Technology tree endpoints")
public class TechTreeController {

    private final TechnologyService service;

    public TechTreeController(TechnologyService service) {
        this.service = service;
    }

    @GetMapping("/{civilizationId}")
    @Operation(summary = "Get tech tree for a civilization")
    public List<Technology> getTechTree(@PathVariable Long civilizationId) {
        return service.getTechTree(civilizationId);
    }

    @PostMapping
    @Operation(summary = "Add a technology to the tree")
    public Technology addTech(@Valid @RequestBody Technology tech) {
        return service.addTechnology(tech);
    }

    @PostMapping("/{techId}/research")
    @Operation(summary = "Start researching a technology")
    public Technology startResearch(@PathVariable Long techId) {
        return service.startResearch(techId);
    }

    @PostMapping("/{techId}/advance")
    @Operation(summary = "Advance research progress")
    public Technology advanceResearch(@PathVariable Long techId, @RequestBody Map<String, Integer> body) {
        return service.advanceResearch(techId, body.getOrDefault("amount", 1));
    }
}
