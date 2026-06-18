package io.github.opencivilizationplatform.modules.needs.api;

import io.github.opencivilizationplatform.modules.needs.application.NeedService;
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

@WebMvcTest(NeedController.class)
class NeedControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private NeedService needService;

    @Test
    void testGetAllNeeds() throws Exception {
        mockMvc.perform(get("/api/v1/needs"))
                .andExpect(status().isOk());
    }

    @Test
    void testSaveNeed() throws Exception {
        mockMvc.perform(post("/api/v1/needs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "region": "Sector-7",
                                    "resource": "Water",
                                    "priority": "HIGH"
                                }
                                """))
                .andExpect(status().isOk());
    }
}
