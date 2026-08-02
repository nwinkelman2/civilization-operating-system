package io.github.opencivilizationplatform.modules.strategy.api;

import io.github.opencivilizationplatform.modules.strategy.application.BalanceService;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;

@ExtendWith(MockitoExtension.class)
class BalanceControllerTest {

    private MockMvc mockMvc;
    private BalanceService balanceService;

    @BeforeEach
    void setUp() {
        balanceService = mock(BalanceService.class);
        mockMvc = standaloneSetup(new BalanceController(balanceService)).build();
    }


    @Test
    void testGetBalance() throws Exception {
        mockMvc.perform(get("/api/v1/strategy/balance"))
                .andExpect(status().isOk());
    }
}