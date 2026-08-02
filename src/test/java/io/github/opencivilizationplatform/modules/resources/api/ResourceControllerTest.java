package io.github.opencivilizationplatform.modules.resources.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.opencivilizationplatform.modules.resources.domain.Resource;
import io.github.opencivilizationplatform.modules.resources.domain.ResourceType;
import io.github.opencivilizationplatform.modules.resources.infrastructure.ResourceRepository;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode;
import org.springframework.data.web.config.SpringDataJacksonConfiguration.PageModule;
import org.springframework.data.web.config.SpringDataWebSettings;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import java.util.List;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import io.github.opencivilizationplatform.modules.resources.application.ResourceService;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;

@ExtendWith(MockitoExtension.class)
class ResourceControllerTest {

    private MockMvc mockMvc;
    private ResourceService resourceService;
    private ResourceRepository resourceRepository;

    @BeforeEach
    void setUp() {
        resourceRepository = mock(ResourceRepository.class);
        resourceService = new ResourceService(resourceRepository);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new PageModule(new SpringDataWebSettings(PageSerializationMode.DIRECT)));
        mockMvc = standaloneSetup(new ResourceController(resourceService))
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();
    }


    @Test
    void testGetAllResources() throws Exception {
        Resource resource = new Resource();
        resource.setId(1L);
        resource.setName("Iron");
        resource.setType(ResourceType.MINERAL);
        resource.setDescription("Iron deposit");
        resource.setQuantity(1000.0);
        resource.setUnit("Tons");

        when(resourceRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(List.of(resource)));

        mockMvc.perform(get("/api/v1/resources"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Iron"))
                .andExpect(jsonPath("$.content[0].type").value("MINERAL"))
                .andExpect(jsonPath("$.content[0].quantity").value(1000.0));
    }

    @Test
    void testSaveResource() throws Exception {
        Resource resource = new Resource();
        resource.setId(2L);
        resource.setName("Water");
        resource.setType(ResourceType.WATER);
        resource.setDescription("Freshwater reserve");
        resource.setQuantity(500.0);
        resource.setUnit("Million L");

        when(resourceRepository.save(org.mockito.ArgumentMatchers.any())).thenReturn(resource);

        mockMvc.perform(post("/api/v1/resources")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "Water",
                                    "type": "WATER",
                                    "description": "Freshwater reserve",
                                    "quantity": 500.0,
                                    "unit": "Million L"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.name").value("Water"))
                .andExpect(jsonPath("$.type").value("WATER"));
    }
}