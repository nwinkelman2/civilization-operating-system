package io.github.opencivilizationplatform;

import io.github.opencivilizationplatform.config.JwtService;
import io.github.opencivilizationplatform.modules.civilization.domain.Civilization;
import io.github.opencivilizationplatform.modules.civilization.infrastructure.CivilizationRepository;
import io.github.opencivilizationplatform.modules.events.domain.GameEvent;
import io.github.opencivilizationplatform.modules.events.infrastructure.GameEventRepository;
import io.github.opencivilizationplatform.modules.trade.domain.TradeAgreement;
import io.github.opencivilizationplatform.modules.trade.infrastructure.TradeRepository;
import io.github.opencivilizationplatform.modules.voxtex.domain.VoxtexMessage;
import io.github.opencivilizationplatform.modules.voxtex.domain.VoxtexMessageType;
import io.github.opencivilizationplatform.modules.voxtex.domain.VoxtexNode;
import io.github.opencivilizationplatform.modules.voxtex.infrastructure.VoxtexNodeRepository;
import io.github.opencivilizationplatform.modules.voxtex.infrastructure.VoxtexMessageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class CivilizationFlowIntegrationTest {

    @Autowired
    private TestRestTemplate rest;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private CivilizationRepository civilizationRepository;

    @Autowired
    private VoxtexNodeRepository voxtexNodeRepository;

    @Autowired
    private VoxtexMessageRepository voxtexMessageRepository;

    @Autowired
    private GameEventRepository gameEventRepository;

    @Autowired
    private TradeRepository tradeRepository;

    private String authToken;
    private String clientId;

    @BeforeEach
    void setUp() {
        civilizationRepository.deleteAll();
        voxtexNodeRepository.deleteAll();
        voxtexMessageRepository.deleteAll();
        gameEventRepository.deleteAll();
        tradeRepository.deleteAll();

        clientId = "test-client-" + System.currentTimeMillis();
        authToken = jwtService.generateToken(clientId);
    }

    private HttpEntity<?> authRequest() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        return new HttpEntity<>(headers);
    }

    private HttpEntity<?> jsonRequest(Object body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(body, headers);
    }

    @Test
    void testFullCivilizationLifecycle() {
        // 1. Health endpoint
        ResponseEntity<Map> health = rest.getForEntity("/actuator/health", Map.class);
        assertEquals(HttpStatus.OK, health.getStatusCode());
        assertNotNull(health.getBody().get("status"));

        // 2. Create civilization
        var createRequest = Map.of(
            "name", "TestCiv-" + System.currentTimeMillis(),
            "scale", "LOCAL",
            "region", "Test Region"
        );
        ResponseEntity<Map> created = rest.exchange(
            "/api/v1/civilizations", HttpMethod.POST, jsonRequest(createRequest), Map.class
        );
        assertEquals(HttpStatus.CREATED, created.getStatusCode());
        assertNotNull(created.getBody().get("id"));
        String civId = created.getBody().get("id").toString();

        // 3. Get all civilizations
        ResponseEntity<Map> allCivs = rest.exchange(
            "/api/v1/civilizations", HttpMethod.GET, authRequest(), Map.class
        );
        assertEquals(HttpStatus.OK, allCivs.getStatusCode());
        assertTrue(((List) ((Map) allCivs.getBody().get("page")).get("content")).size() >= 1);

        // 4. Get civilization by ID
        ResponseEntity<Civilization> byId = rest.exchange(
            "/api/v1/civilizations/" + civId, HttpMethod.GET, authRequest(), Civilization.class
        );
        assertEquals(HttpStatus.OK, byId.getStatusCode());
        assertEquals("TestCiv", byId.getBody().getName().substring(0, 7));

        // 5. Create a voxtex node
        var nodeRequest = Map.of(
            "name", "Primary-Node",
            "type", "PRIMARY",
            "region", "Test Region",
            "civilizationId", Long.parseLong(civId),
            "knowledgeBase", "Test knowledge base"
        );
        ResponseEntity<Map> node = rest.exchange(
            "/api/v1/voxtex/nodes", HttpMethod.POST, jsonRequest(nodeRequest), Map.class
        );
        assertEquals(HttpStatus.OK, node.getStatusCode());
        assertNotNull(node.getBody().get("id"));
        String nodeId = node.getBody().get("id").toString();

        // 6. Get nodes for civilization
        ResponseEntity<List> civNodes = rest.exchange(
            "/api/v1/voxtex/nodes/civilization/" + civId, HttpMethod.GET, authRequest(), List.class
        );
        assertEquals(HttpStatus.OK, civNodes.getStatusCode());
        assertTrue(civNodes.getBody().size() >= 1);

        // 7. Create a second node for messaging
        var node2Request = Map.of(
            "name", "Secondary-Node",
            "type", "SECONDARY",
            "region", "Test Region",
            "civilizationId", Long.parseLong(civId),
            "knowledgeBase", "Secondary knowledge base"
        );
        ResponseEntity<Map> node2 = rest.exchange(
            "/api/v1/voxtex/nodes", HttpMethod.POST, jsonRequest(node2Request), Map.class
        );
        String node2Id = node2.getBody().get("id").toString();

        // 8. Send a voxtex message between nodes
        var msgRequest = Map.of(
            "sourceNodeId", Long.parseLong(nodeId),
            "targetNodeId", Long.parseLong(node2Id),
            "messageType", "DATA",
            "content", "Hello from integration test!"
        );
        ResponseEntity<Map> msg = rest.exchange(
            "/api/v1/voxtex/messages", HttpMethod.POST, jsonRequest(msgRequest), Map.class
        );
        assertEquals(HttpStatus.OK, msg.getStatusCode());
        assertNotNull(msg.getBody().get("id"));

        // 9. Get pending messages for target node
        ResponseEntity<List> pending = rest.exchange(
            "/api/v1/voxtex/messages/pending/" + node2Id, HttpMethod.GET, authRequest(), List.class
        );
        assertEquals(HttpStatus.OK, pending.getStatusCode());
        assertTrue(pending.getBody().size() >= 1);

        // 10. Get conversation between nodes
        ResponseEntity<List> conversation = rest.exchange(
            "/api/v1/voxtex/messages/conversation/" + nodeId + "/" + node2Id,
            HttpMethod.GET, authRequest(), List.class
        );
        assertEquals(HttpStatus.OK, conversation.getStatusCode());
        assertTrue(conversation.getBody().size() >= 1);

        // 11. Get voxtex network status
        ResponseEntity<Map> status = rest.exchange(
            "/api/v1/voxtex/status", HttpMethod.GET, authRequest(), Map.class
        );
        assertEquals(HttpStatus.OK, status.getStatusCode());
        assertNotNull(status.getBody().get("networkStatus"));
    }

    @Test
    void testUnauthenticatedAccess() {
        // Should fail without auth
        ResponseEntity<Map> response = rest.postForEntity(
            "/api/v1/civilizations",
            Map.of("name", "EvilCiv", "scale", "LOCAL", "region", "Nowhere"),
            Map.class
        );
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    void testPublicEndpointsAreAccessible() {
        // Public GET endpoints should work without auth
        ResponseEntity<List> regions = rest.exchange(
            "/api/v1/voxtex/nodes", HttpMethod.GET, null, List.class
        );
        assertEquals(HttpStatus.OK, regions.getStatusCode());

        ResponseEntity<Map> leaderboard = rest.exchange(
            "/api/v1/leaderboard", HttpMethod.GET, null, Map.class
        );
        assertEquals(HttpStatus.OK, leaderboard.getStatusCode());

        ResponseEntity<Map> simulationStatus = rest.exchange(
            "/api/v1/simulation/status", HttpMethod.GET, null, Map.class
        );
        assertEquals(HttpStatus.OK, simulationStatus.getStatusCode());
    }

    @Test
    void testAuthTokenGeneration() {
        ResponseEntity<Map> auth = rest.postForEntity("/api/v1/auth/connect", null, Map.class);
        assertEquals(HttpStatus.OK, auth.getStatusCode());
        assertNotNull(auth.getBody().get("token"));
        assertNotNull(auth.getBody().get("clientId"));
    }

    @Test
    void testCortexEngineTick() {
        // Create a civilization first so the engine has data to process
        var createRequest = Map.of(
            "name", "CortexTest-" + System.currentTimeMillis(),
            "scale", "LOCAL",
            "region", "Cortex Region"
        );
        ResponseEntity<Map> created = rest.exchange(
            "/api/v1/civilizations", HttpMethod.POST, jsonRequest(createRequest), Map.class
        );
        assertEquals(HttpStatus.CREATED, created.getStatusCode());

        // Simulation engine status should be accessible
        ResponseEntity<Map> simStatus = rest.exchange(
            "/api/v1/simulation/status", HttpMethod.GET, null, Map.class
        );
        assertEquals(HttpStatus.OK, simStatus.getStatusCode());
        assertNotNull(simStatus.getBody().get("tickCount"));
        assertNotNull(simStatus.getBody().get("lastDecision"));
    }

    @Test
    void testResourceBalance() {
        // Create a civ first
        var createRequest = Map.of(
            "name", "BalanceTest-" + System.currentTimeMillis(),
            "scale", "LOCAL",
            "region", "Balance Region"
        );
        ResponseEntity<Map> created = rest.exchange(
            "/api/v1/civilizations", HttpMethod.POST, jsonRequest(createRequest), Map.class
        );
        assertEquals(HttpStatus.CREATED, created.getStatusCode());

        // Get balance report
        ResponseEntity<Map> balance = rest.exchange(
            "/api/v1/strategy/balance", HttpMethod.GET, authRequest(), Map.class
        );
        assertEquals(HttpStatus.OK, balance.getStatusCode());
    }

    @Test
    void testJwtValidationWithWebSocketFormat() {
        // Verify JWT service can generate and validate tokens compatible with WS handshake
        String token = jwtService.generateToken("ws-test-client");
        assertTrue(jwtService.isTokenValid(token));
        assertEquals("ws-test-client", jwtService.extractClientId(token));

        // Token should pass through REST API
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        var entity = new HttpEntity<>(headers);

        ResponseEntity<Map> response = rest.exchange(
            "/api/v1/voxtex/status", HttpMethod.GET, entity, Map.class
        );
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
