package io.github.opencivilizationplatform.modules.resources.application;

import io.github.opencivilizationplatform.modules.resources.domain.Resource;
import io.github.opencivilizationplatform.modules.resources.infrastructure.ResourceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ResourceServiceTest {

    @Mock
    private ResourceRepository resourceRepository;

    @InjectMocks
    private ResourceService resourceService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllResources() {
        Resource res1 = new Resource();
        Resource res2 = new Resource();
        when(resourceRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(Arrays.asList(res1, res2)));

        Page<Resource> result = resourceService.getAllResources(Pageable.unpaged());

        assertEquals(2, result.getContent().size());
        verify(resourceRepository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    void testSaveResource() {
        Resource res = new Resource();
        res.setName("Iron Ore");
        when(resourceRepository.save(any(Resource.class))).thenReturn(res);

        Resource saved = resourceService.saveResource(res);

        assertNotNull(saved);
        assertEquals("Iron Ore", saved.getName());
        verify(resourceRepository, times(1)).save(res);
    }
}
