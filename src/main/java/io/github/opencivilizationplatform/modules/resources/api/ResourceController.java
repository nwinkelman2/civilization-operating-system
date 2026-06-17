package io.github.opencivilizationplatform.modules.resources.api;

import io.github.opencivilizationplatform.modules.resources.domain.Resource;
import io.github.opencivilizationplatform.modules.resources.infrastructure.ResourceRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/resources")
public class ResourceController {

    private final ResourceRepository resourceRepository;

    public ResourceController(ResourceRepository resourceRepository) {
        this.resourceRepository = resourceRepository;
    }

    @GetMapping
    public java.util.List<Resource> getAllResources() {
        return resourceRepository.findAll();
    }

    @PostMapping
    public Resource saveResource(@RequestBody Resource resource) {
        return resourceRepository.save(resource);
    }
}
