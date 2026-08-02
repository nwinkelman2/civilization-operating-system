package io.github.opencivilizationplatform.modules.production.api;

import io.github.opencivilizationplatform.modules.production.application.FacilityService;
import org.junit.jupiter.api.Test;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;

@ExtendWith(MockitoExtension.class)
class FacilityControllerTest {

    private MockMvc mockMvc;
    private FacilityService facilityService;

    @BeforeEach
    void setUp() {
        facilityService = mock(FacilityService.class);
        mockMvc = standaloneSetup(new FacilityController(facilityService))
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
    }


    @Test
    void testGetAllFacilities() throws Exception {
        mockMvc.perform(get("/api/v1/facilities"))
                .andExpect(status().isOk());
    }

    @Test
    void testSaveFacility() throws Exception {
        mockMvc.perform(post("/api/v1/facilities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "Water Plant",
                                    "type": "MANUFACTURING",
                                    "region": "Sector-7",
                                    "efficiency": 85.0,
                                    "status": "ACTIVE"
                                }
                                """))
                .andExpect(status().isOk());
    }
}