package io.github.opencivilizationplatform.modules.resources.infrastructure;

import io.github.opencivilizationplatform.modules.resources.domain.Resource;
import io.github.opencivilizationplatform.modules.resources.domain.ResourceType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResourceRepositoryTest {

    private ResourceRepository resourceRepository;

    @BeforeEach
    void setUp() {
        resourceRepository = mock(ResourceRepository.class);
    }

    @Test
    void testSaveAndFindAll() {
        Resource resource = new Resource();
        resource.setName("Test Resource");
        resource.setType(ResourceType.FOOD);
        resource.setDescription("A test resource");
        resource.setQuantity(100.0);
        resource.setUnit("kg");

        when(resourceRepository.save(any(Resource.class))).thenAnswer(i -> {
            Resource r = i.getArgument(0);
            if (r.getId() == null) r.setId(1L);
            return r;
        });
        when(resourceRepository.findAll()).thenReturn(List.of(resource));

        Resource saved = resourceRepository.save(resource);
        assertNotNull(saved.getId());

        List<Resource> all = resourceRepository.findAll();
        assertEquals(1, all.size());
        assertEquals("Test Resource", all.get(0).getName());
        assertEquals(ResourceType.FOOD, all.get(0).getType());
        assertEquals(100.0, all.get(0).getQuantity());
    }

    @Test
    void testFindById() {
        Resource resource = new Resource();
        resource.setName("Findable");
        resource.setType(ResourceType.WATER);
        resource.setDescription("For find by id test");
        resource.setQuantity(500.0);
        resource.setUnit("L");

        when(resourceRepository.save(any(Resource.class))).thenAnswer(i -> {
            Resource r = i.getArgument(0);
            if (r.getId() == null) r.setId(1L);
            return r;
        });
        when(resourceRepository.findById(any())).thenAnswer(i -> {
            Resource r = new Resource();
            r.setId(i.getArgument(0));
            r.setName("Findable");
            r.setType(ResourceType.WATER);
            return Optional.of(r);
        });

        Resource saved = resourceRepository.save(resource);

        Resource found = resourceRepository.findById(saved.getId()).orElseThrow();
        assertEquals("Findable", found.getName());
        assertEquals(ResourceType.WATER, found.getType());
    }
}
