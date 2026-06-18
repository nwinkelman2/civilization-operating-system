package io.github.opencivilizationplatform.modules.resources.application;

import io.github.opencivilizationplatform.modules.resources.domain.Resource;
import io.github.opencivilizationplatform.modules.resources.infrastructure.ResourceRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ResourceService {
    private final ResourceRepository resourceRepository;

    public ResourceService(ResourceRepository resourceRepository) {
        this.resourceRepository = resourceRepository;
    }

    @Cacheable(value = "resources", key = "#pageable.pageNumber + '-' + #pageable.pageSize")
    public Page<Resource> getAllResources(Pageable pageable) {
        return resourceRepository.findAll(pageable);
    }

    @CacheEvict(value = {"resources", "balance"}, allEntries = true)
    public Resource saveResource(Resource resource) {
        return resourceRepository.save(resource);
    }
}
