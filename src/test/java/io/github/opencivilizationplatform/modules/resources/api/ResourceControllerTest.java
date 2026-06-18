package io.github.opencivilizationplatform.modules.resources.api;

import io.github.opencivilizationplatform.modules.resources.domain.Resource;
import io.github.opencivilizationplatform.modules.resources.domain.ResourceType;
import io.github.opencivilizationplatform.modules.resources.infrastructure.ResourceRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ResourceController.class)
class ResourceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ResourceRepository resourceRepository;

    @Test
    void testGetAllResources() throws Exception {
        Resource resource = new Resource();
        resource.setId(1L);
        resource.setName("Iron");
        resource.setType(ResourceType.MINERAL);
        resource.setDescription("Iron deposit");
        resource.setQuantity(1000.0);
        resource.setUnit("Tons");

        when(resourceRepository.findAll()).thenReturn(List.of(resource));

        mockMvc.perform(get("/api/v1/resources"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Iron"))
                .andExpect(jsonPath("$[0].type").value("MINERAL"))
                .andExpect(jsonPath("$[0].quantity").value(1000.0));
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
