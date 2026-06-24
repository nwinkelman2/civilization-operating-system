package io.github.opencivilizationplatform.modules.voxtex.api;

import io.github.opencivilizationplatform.modules.voxtex.application.VoxtexMeshService;
import io.github.opencivilizationplatform.modules.voxtex.domain.VoxtexConnection;
import io.github.opencivilizationplatform.modules.voxtex.domain.VoxtexNode;
import io.github.opencivilizationplatform.modules.voxtex.domain.VoxtexNodeStatus;
import io.github.opencivilizationplatform.modules.voxtex.domain.VoxtexNodeType;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import java.util.List;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class VoxtexControllerTest {

    private MockMvc mockMvc;
    private VoxtexMeshService voxtexMeshService;

    @BeforeEach
    void setUp() {
        voxtexMeshService = mock(VoxtexMeshService.class);
        mockMvc = standaloneSetup(new VoxtexController(voxtexMeshService)).build();
    }

    @Test
    void testGetAllNodes() throws Exception {
        VoxtexNode node = new VoxtexNode();
        node.setId(1L);
        node.setName("Primary Node");
        node.setType(VoxtexNodeType.PRIMARY);
        node.setStatus(VoxtexNodeStatus.ACTIVE);
        node.setRegion("Test Region");
        when(voxtexMeshService.getAllNodes()).thenReturn(List.of(node));
        mockMvc.perform(get("/api/v1/voxtex/nodes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Primary Node"))
                .andExpect(jsonPath("$[0].type").value("PRIMARY"));
    }
    @Test
    void testGetAllConnections() throws Exception {
        VoxtexConnection connection = new VoxtexConnection();
        connection.setId(1L);
        connection.setStrength(0.8);
        connection.setLatencyMs(50L);
        connection.setMessagesExchanged(100);
        when(voxtexMeshService.getAllConnections()).thenReturn(List.of(connection));
        mockMvc.perform(get("/api/v1/voxtex/connections"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].strength").value(0.8))
                .andExpect(jsonPath("$[0].latencyMs").value(50));
    }
}