package io.github.opencivilizationplatform.modules.resources.application;

import io.github.opencivilizationplatform.modules.resources.domain.Resource;
import io.github.opencivilizationplatform.modules.resources.infrastructure.ResourceRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ResourceService {
    private final ResourceRepository resourceRepository;

    public ResourceService(ResourceRepository resourceRepository) {
        this.resourceRepository = resourceRepository;
    }

    @Cacheable(value = "resources", key = "#pageable.isPaged() ? #pageable.pageNumber + '-' + #pageable.pageSize : 'unpaged'")
    public Page<Resource> getAllResources(Pageable pageable) {
        Page<Resource> page = resourceRepository.findAll(pageable);
        if (!pageable.isPaged()) {
            return new PageImpl<>(
                page.getContent(),
                PageRequest.of(0, Math.max(page.getContent().size(), 1)),
                page.getTotalElements()
            );
        }
        return page;
    }

    @CacheEvict(value = {"resources", "balance"}, allEntries = true)
    public Resource saveResource(Resource resource) {
        return resourceRepository.save(resource);
    }
}
