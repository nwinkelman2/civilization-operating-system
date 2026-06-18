package io.github.opencivilizationplatform.modules.resources.infrastructure;

import io.github.opencivilizationplatform.modules.resources.domain.Resource;
import io.github.opencivilizationplatform.modules.resources.domain.ResourceType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ResourceRepositoryTest {

    @Autowired
    private ResourceRepository resourceRepository;

    @Test
    void testSaveAndFindAll() {
        Resource resource = new Resource();
        resource.setName("Test Resource");
        resource.setType(ResourceType.FOOD);
        resource.setDescription("A test resource");
        resource.setQuantity(100.0);
        resource.setUnit("kg");

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

        Resource saved = resourceRepository.save(resource);

        Resource found = resourceRepository.findById(saved.getId()).orElseThrow();
        assertEquals("Findable", found.getName());
        assertEquals(ResourceType.WATER, found.getType());
    }
}
