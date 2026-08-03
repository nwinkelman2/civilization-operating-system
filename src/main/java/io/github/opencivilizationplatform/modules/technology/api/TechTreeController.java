package io.github.opencivilizationplatform.modules.technology.api;

import io.github.opencivilizationplatform.modules.technology.application.TechnologyService;
import io.github.opencivilizationplatform.modules.technology.domain.Technology;
import io.github.opencivilizationplatform.modules.technology.domain.LicensedTechnology;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping({"/api/v1/tech-tree", "/api/v1/technologies"})
@Tag(name = "Tech Tree", description = "Technology tree endpoints")
public class TechTreeController {

    private final TechnologyService service;

    public TechTreeController(TechnologyService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Get all technologies")
    public List<Technology> getAllTechnologies() {
        return service.getAllTechnologies();
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

    @PostMapping("/{techId}/contribute")
    @Operation(summary = "Contribute consensus coins to research progress")
    public Technology contributeCoins(@PathVariable Long techId, @RequestBody Map<String, Object> body) {
        Long civilizationId = Long.valueOf(body.get("civilizationId").toString());
        Double coins = Double.valueOf(body.get("coins").toString());
        return service.contributeCoins(techId, civilizationId, coins);
    }

    @PostMapping("/{techId}/license")
    @Operation(summary = "License a completed technology")
    public LicensedTechnology licenseTechnology(@PathVariable Long techId, @RequestBody Map<String, Object> body) {
        Long licenseeId = Long.valueOf(body.get("licenseeId").toString());
        Double feePerTick = Double.valueOf(body.get("feePerTick").toString());
        return service.licenseTechnology(techId, licenseeId, feePerTick);
    }

    @GetMapping("/licensed/{licenseeId}")
    @Operation(summary = "Get licensed technologies for a civilization")
    public List<LicensedTechnology> getLicensedTechnologies(@PathVariable Long licenseeId) {
        return service.getLicensedTechnologies(licenseeId);
    }
}
