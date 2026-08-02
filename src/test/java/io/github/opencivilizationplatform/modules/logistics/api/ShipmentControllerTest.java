package io.github.opencivilizationplatform.modules.logistics.api;

import io.github.opencivilizationplatform.modules.logistics.application.ShipmentService;
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
class ShipmentControllerTest {

    private MockMvc mockMvc;
    private ShipmentService shipmentService;

    @BeforeEach
    void setUp() {
        shipmentService = mock(ShipmentService.class);
        mockMvc = standaloneSetup(new ShipmentController(shipmentService))
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
    }


    @Test
    void testGetAllShipments() throws Exception {
        mockMvc.perform(get("/api/v1/shipments"))
                .andExpect(status().isOk());
    }

    @Test
    void testSaveShipment() throws Exception {
        mockMvc.perform(post("/api/v1/shipments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "cargo": "Food supplies",
                                    "origin": "Warehouse-A",
                                    "destination": "Sector-7",
                                    "quantity": 100.0,
                                    "unit": "kg",
                                    "status": "PENDING"
                                }
                                """))
                .andExpect(status().isOk());
    }
}