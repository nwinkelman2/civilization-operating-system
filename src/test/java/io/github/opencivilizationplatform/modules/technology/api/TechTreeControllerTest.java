package io.github.opencivilizationplatform.modules.technology.api;

import io.github.opencivilizationplatform.modules.technology.application.TechnologyService;
import io.github.opencivilizationplatform.modules.technology.domain.Technology;
import io.github.opencivilizationplatform.modules.technology.domain.TechnologyCategory;
import io.github.opencivilizationplatform.modules.technology.domain.TechnologyStatus;
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
class TechTreeControllerTest {

    private MockMvc mockMvc;
    private TechnologyService technologyService;

    @BeforeEach
    void setUp() {
        technologyService = mock(TechnologyService.class);
        mockMvc = standaloneSetup(new TechTreeController(technologyService)).build();
    }

    @Test
    void testGetTechTree() throws Exception {
        Technology tech = new Technology();
        tech.setId(1L);
        tech.setName("Agriculture");
        tech.setCategory(TechnologyCategory.AGRICULTURE);
        tech.setStatus(TechnologyStatus.COMPLETED);
        tech.setResearchCost(50);
        tech.setTier(1);
        tech.setCivilizationId(1L);
        when(technologyService.getTechTree(1L)).thenReturn(List.of(tech));
        mockMvc.perform(get("/api/v1/tech-tree/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Agriculture"))
                .andExpect(jsonPath("$[0].category").value("AGRICULTURE"));
    }

    @Test
    void testContribute() throws Exception {
        Technology tech = new Technology();
        tech.setId(1L);
        tech.setName("Agriculture");
        tech.setStatus(TechnologyStatus.RESEARCHING);
        
        when(technologyService.contributeCoins(eq(1L), eq(1L), eq(10.0))).thenReturn(tech);
        
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/api/v1/tech-tree/1/contribute")
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .content("{\"civilizationId\": 1, \"coins\": 10.0}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Agriculture"))
                .andExpect(jsonPath("$.status").value("RESEARCHING"));
    }

    @Test
    void testLicense() throws Exception {
        io.github.opencivilizationplatform.modules.technology.domain.LicensedTechnology licensedTech = 
            new io.github.opencivilizationplatform.modules.technology.domain.LicensedTechnology();
        licensedTech.setId(1L);
        licensedTech.setTechName("Agriculture");
        licensedTech.setFeePerTick(5.0);
        
        when(technologyService.licenseTechnology(eq(1L), eq(1L), eq(5.0))).thenReturn(licensedTech);
        
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/api/v1/tech-tree/1/license")
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .content("{\"licenseeId\": 1, \"feePerTick\": 5.0}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.techName").value("Agriculture"))
                .andExpect(jsonPath("$.feePerTick").value(5.0));
    }
}