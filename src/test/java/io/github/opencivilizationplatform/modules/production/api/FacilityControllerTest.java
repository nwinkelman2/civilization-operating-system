package io.github.opencivilizationplatform.modules.production.api;

import io.github.opencivilizationplatform.modules.production.application.FacilityService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FacilityController.class)
class FacilityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FacilityService facilityService;

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
                                    "type": "PRODUCTION",
                                    "capacity": 1000.0
                                }
                                """))
                .andExpect(status().isOk());
    }
}
