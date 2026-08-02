package io.github.opencivilizationplatform.modules.needs.api;

import io.github.opencivilizationplatform.modules.needs.application.NeedService;
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
class NeedControllerTest {

    private MockMvc mockMvc;
    private NeedService needService;

    @BeforeEach
    void setUp() {
        needService = mock(NeedService.class);
        mockMvc = standaloneSetup(new NeedController(needService))
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
    }


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
                                    "category": "FOOD",
                                    "region": "Sector-7",
                                    "description": "Need clean water supply",
                                    "quantity": 500.0,
                                    "unit": "Liters",
                                    "priority": 5,
                                    "status": "UNMET"
                                }
                                """))
                .andExpect(status().isOk());
    }
}