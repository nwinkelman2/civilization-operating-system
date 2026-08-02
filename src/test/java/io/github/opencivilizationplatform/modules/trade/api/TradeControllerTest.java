package io.github.opencivilizationplatform.modules.trade.api;

import io.github.opencivilizationplatform.modules.trade.application.TradeService;
import io.github.opencivilizationplatform.modules.trade.domain.TradeAgreement;
import io.github.opencivilizationplatform.modules.trade.domain.TradeStatus;
import org.junit.jupiter.api.Test;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TradeControllerTest {

    private MockMvc mockMvc;
    private TradeService tradeService;
    private io.github.opencivilizationplatform.modules.trade.application.MarketPriceService marketPriceService;

    @BeforeEach
    void setUp() {
        tradeService = mock(TradeService.class);
        marketPriceService = mock(io.github.opencivilizationplatform.modules.trade.application.MarketPriceService.class);
        mockMvc = standaloneSetup(new TradeController(tradeService, marketPriceService)).build();
    }

    @Test
    void testGetTrades() throws Exception {
        TradeAgreement trade = new TradeAgreement();
        trade.setId(1L);
        trade.setFromCivilizationId(1L);
        trade.setToCivilizationId(2L);
        trade.setResourceType("FOOD");
        trade.setQuantity(100.0);
        when(tradeService.getTradesForCivilization(1L)).thenReturn(List.of(trade));
        mockMvc.perform(get("/api/v1/trade/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].resourceType").value("FOOD"))
                .andExpect(jsonPath("$[0].quantity").value(100.0));
    }
    @Test
    void testProposeTrade() throws Exception {
        TradeAgreement trade = new TradeAgreement();
        trade.setId(1L);
        trade.setFromCivilizationId(1L);
        trade.setToCivilizationId(2L);
        trade.setResourceType("FOOD");
        trade.setQuantity(100.0);
        trade.setStatus(TradeStatus.PROPOSED);
        when(tradeService.proposeTrade(eq(1L), eq(2L), eq("FOOD"), eq(100.0))).thenReturn(trade);
        mockMvc.perform(post("/api/v1/trade")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "fromCivId": 1,
                                    "toCivId": 2,
                                    "resourceType": "FOOD",
                                    "quantity": 100.0
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.resourceType").value("FOOD"))
                .andExpect(jsonPath("$.quantity").value(100.0));
    }
}