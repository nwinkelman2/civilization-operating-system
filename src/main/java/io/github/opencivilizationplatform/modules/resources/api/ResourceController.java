package io.github.opencivilizationplatform.modules.resources.api;

import io.github.opencivilizationplatform.modules.resources.application.ResourceService;
import io.github.opencivilizationplatform.modules.resources.domain.Resource;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/resources")
@Tag(name = "Resources", description = "Resource management endpoints")
public class ResourceController {

    private final ResourceService resourceService;

    public ResourceController(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    @GetMapping
    @Operation(summary = "List all resources", description = "Returns a paginated list of resources")
    public Page<Resource> getAllResources(Pageable pageable) {
        return resourceService.getAllResources(pageable);
    }

    @PostMapping
    @Operation(summary = "Create a resource", description = "Creates a new resource entry")
    public Resource saveResource(@Valid @RequestBody Resource resource) {
        return resourceService.saveResource(resource);
    }
}
