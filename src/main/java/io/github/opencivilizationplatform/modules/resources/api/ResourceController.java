package io.github.opencivilizationplatform.modules.resources.api;

import io.github.opencivilizationplatform.modules.resources.application.ResourceService;
import io.github.opencivilizationplatform.modules.resources.domain.Resource;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/resources")
public class ResourceController {

    private final ResourceService resourceService;

    public ResourceController(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    @GetMapping
    public Page<Resource> getAllResources(Pageable pageable) {
        return resourceService.getAllResources(pageable);
    }

    @PostMapping
    public Resource saveResource(@Valid @RequestBody Resource resource) {
        return resourceService.saveResource(resource);
    }
}
