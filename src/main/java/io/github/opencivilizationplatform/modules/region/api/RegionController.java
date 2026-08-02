package io.github.opencivilizationplatform.modules.region.api;

import io.github.opencivilizationplatform.modules.region.application.ResourceRegionService;
import io.github.opencivilizationplatform.modules.region.domain.ResourceRegion;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/regions")
@Tag(name = "Resource Regions", description = "Map regions with resource data")
public class RegionController {

    private final ResourceRegionService service;

    public RegionController(ResourceRegionService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "List all resource regions")
    public List<ResourceRegion> getAll() {
        return service.getAllRegions();
    }

    @GetMapping("/available")
    @Operation(summary = "List unclaimed regions")
    public List<ResourceRegion> getAvailable() {
        return service.getAvailableRegions();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get region details")
    public ResourceRegion getById(@PathVariable Long id) {
        return service.getRegion(id);
    }

    @PostMapping("/{id}/claim/{civilizationId}")
    @Operation(summary = "Claim a region for a civilization")
    public ResourceRegion claim(@PathVariable Long id, @PathVariable Long civilizationId) {
        return service.claimRegion(id, civilizationId);
    }
}
