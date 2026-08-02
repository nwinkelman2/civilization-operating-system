package io.github.opencivilizationplatform.modules.region.api;

import io.github.opencivilizationplatform.config.seed.CivilizationScale;
import io.github.opencivilizationplatform.modules.region.application.ResourceRegionService;
import io.github.opencivilizationplatform.modules.region.domain.ResourceRegion;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import java.util.List;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RegionControllerTest {

    private MockMvc mockMvc;
    private ResourceRegionService resourceRegionService;

    @BeforeEach
    void setUp() {
        resourceRegionService = mock(ResourceRegionService.class);
        mockMvc = standaloneSetup(new RegionController(resourceRegionService)).build();
    }

    @Test
    void testGetAllRegions() throws Exception {
        ResourceRegion region = new ResourceRegion();
        region.setId(1L);
        region.setName("Test Region");
        region.setScale(CivilizationScale.LOCAL);
        region.setClaimed(false);
        when(resourceRegionService.getAllRegions()).thenReturn(List.of(region));
        mockMvc.perform(get("/api/v1/regions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Test Region"))
                .andExpect(jsonPath("$[0].claimed").value(false));
    }
}