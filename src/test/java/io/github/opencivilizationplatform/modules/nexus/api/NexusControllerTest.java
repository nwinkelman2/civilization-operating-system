package io.github.opencivilizationplatform.modules.nexus.api;

import io.github.opencivilizationplatform.modules.nexus.application.NexusMeshService;
import io.github.opencivilizationplatform.modules.nexus.domain.NexusConnection;
import io.github.opencivilizationplatform.modules.nexus.domain.NexusNode;
import io.github.opencivilizationplatform.modules.nexus.domain.NexusNodeStatus;
import io.github.opencivilizationplatform.modules.nexus.domain.NexusNodeType;
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
class NexusControllerTest {

    private MockMvc mockMvc;
    private NexusMeshService NexusMeshService;

    @BeforeEach
    void setUp() {
        NexusMeshService = mock(NexusMeshService.class);
        var meshTradeRepository = mock(io.github.opencivilizationplatform.modules.nexus.infrastructure.MeshTradeRepository.class);
        var migrationRequestRepository = mock(io.github.opencivilizationplatform.modules.nexus.infrastructure.MigrationRequestRepository.class);
        var civilizationRepository = mock(io.github.opencivilizationplatform.modules.civilization.infrastructure.CivilizationRepository.class);
        mockMvc = standaloneSetup(new NexusController(NexusMeshService, meshTradeRepository, migrationRequestRepository, civilizationRepository)).build();
    }

    @Test
    void testGetAllNodes() throws Exception {
        NexusNode node = new NexusNode();
        node.setId(1L);
        node.setName("Primary Node");
        node.setType(NexusNodeType.PRIMARY);
        node.setStatus(NexusNodeStatus.ACTIVE);
        node.setRegion("Test Region");
        when(NexusMeshService.getAllNodes()).thenReturn(List.of(node));
        mockMvc.perform(get("/api/v1/nexus/nodes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Primary Node"))
                .andExpect(jsonPath("$[0].type").value("PRIMARY"));
    }
    @Test
    void testGetAllConnections() throws Exception {
        NexusConnection connection = new NexusConnection();
        connection.setId(1L);
        connection.setStrength(0.8);
        connection.setLatencyMs(50L);
        connection.setMessagesExchanged(100);
        when(NexusMeshService.getAllConnections()).thenReturn(List.of(connection));
        mockMvc.perform(get("/api/v1/nexus/connections"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].strength").value(0.8))
                .andExpect(jsonPath("$[0].latencyMs").value(50));
    }
}
