package io.github.opencivilizationplatform.modules.logistics.api;

import io.github.opencivilizationplatform.modules.logistics.application.ShipmentService;
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

@WebMvcTest(ShipmentController.class)
class ShipmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ShipmentService shipmentService;

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
                                    "origin": "Warehouse-A",
                                    "destination": "Sector-7",
                                    "status": "PENDING"
                                }
                                """))
                .andExpect(status().isOk());
    }
}
