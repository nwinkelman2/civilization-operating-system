package io.github.opencivilizationplatform.modules.civilization.api;

import io.github.opencivilizationplatform.config.seed.CivilizationScale;
import io.github.opencivilizationplatform.modules.civilization.application.CivilizationService;
import io.github.opencivilizationplatform.modules.civilization.domain.Civilization;
import io.github.opencivilizationplatform.modules.civilization.domain.CivilizationStatus;
import io.github.opencivilizationplatform.modules.region.application.ResourceRegionService;
import io.github.opencivilizationplatform.modules.voxtex.application.VoxtexMeshService;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;
import io.github.opencivilizationplatform.config.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CivilizationControllerTest {

    private MockMvc mockMvc;
    private CivilizationService civilizationService;
    private ResourceRegionService resourceRegionService;
    private VoxtexMeshService voxtexMeshService;
    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        civilizationService = mock(CivilizationService.class);
        resourceRegionService = mock(ResourceRegionService.class);
        voxtexMeshService = mock(VoxtexMeshService.class);
        jwtService = mock(JwtService.class);
        mockMvc = standaloneSetup(new CivilizationController(civilizationService, resourceRegionService, voxtexMeshService, jwtService))
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
    }

    @Test
    void testGetAllCivilizations() throws Exception {
        Civilization civ = new Civilization();
        civ.setId(1L);
        civ.setName("Test Civilization");
        civ.setScale(CivilizationScale.LOCAL);
        civ.setRegion("Test Region");
        civ.setStatus(CivilizationStatus.EMERGING);
        civ.setOwnerToken("test-token");
        civ.setReputationScore(50.0);
        civ.setPopulation(100);
        Page<Civilization> page = new PageImpl<>(List.of(civ), PageRequest.of(0, 10), 1);
        when(civilizationService.getAllCivilizations(any())).thenReturn(page);
        mockMvc.perform(get("/api/v1/civilizations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Test Civilization"))
                .andExpect(jsonPath("$.content[0].region").value("Test Region"));
    }
    @Test
    void testCreateCivilization() throws Exception {
        Civilization civ = new Civilization();
        civ.setId(1L);
        civ.setName("New Civilization");
        civ.setScale(CivilizationScale.LOCAL);
        civ.setRegion("New Region");
        civ.setStatus(CivilizationStatus.EMERGING);
        civ.setOwnerToken("test-token");
        when(civilizationService.createCivilization(eq("New Civilization"), eq(CivilizationScale.LOCAL), eq("New Region"), any())).thenReturn(civ);
        mockMvc.perform(post("/api/v1/civilizations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Client-Token", "test-token")
                        .content("""
                                {
                                    "name": "New Civilization",
                                    "scale": "LOCAL",
                                    "region": "New Region"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("New Civilization"))
                .andExpect(jsonPath("$.region").value("New Region"));
    }
}