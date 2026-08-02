package io.github.opencivilizationplatform.modules.participation.api;

import io.github.opencivilizationplatform.modules.participation.application.RuleService;
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
class RuleControllerTest {

    private MockMvc mockMvc;
    private RuleService ruleService;

    @BeforeEach
    void setUp() {
        ruleService = mock(RuleService.class);
        mockMvc = standaloneSetup(new RuleController(ruleService))
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
    }


    @Test
    void testGetAllRules() throws Exception {
        mockMvc.perform(get("/api/v1/rules"))
                .andExpect(status().isOk());
    }

    @Test
    void testProposeRule() throws Exception {
        mockMvc.perform(post("/api/v1/rules")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "title": "Limit resource usage",
                                    "description": "Cap water usage at 500L/day",
                                    "logicCode": "water_cap",
                                    "status": "PROPOSED",
                                    "validationStatus": "PENDING"
                                }
                                """))
                .andExpect(status().isOk());
    }
}