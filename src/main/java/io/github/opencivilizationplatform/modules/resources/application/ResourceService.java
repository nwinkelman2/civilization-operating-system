package io.github.opencivilizationplatform.modules.resources.application;

import io.github.opencivilizationplatform.modules.resources.domain.Resource;
import io.github.opencivilizationplatform.modules.resources.infrastructure.ResourceRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ResourceService {
    private final ResourceRepository resourceRepository;

    public ResourceService(ResourceRepository resourceRepository) {
        this.resourceRepository = resourceRepository;
    }

    public Page<Resource> getAllResources(Pageable pageable) {
        return resourceRepository.findAll(pageable);
    }

    public Resource saveResource(Resource resource) {
        return resourceRepository.save(resource);
    }
}
