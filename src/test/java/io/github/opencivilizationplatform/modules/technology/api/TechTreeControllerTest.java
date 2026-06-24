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
}