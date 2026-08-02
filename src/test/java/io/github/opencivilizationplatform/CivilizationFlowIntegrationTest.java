package io.github.opencivilizationplatform;

import io.github.opencivilizationplatform.config.JwtService;
import io.github.opencivilizationplatform.modules.civilization.domain.Civilization;
import io.github.opencivilizationplatform.modules.civilization.infrastructure.CivilizationRepository;
import io.github.opencivilizationplatform.modules.events.domain.GameEvent;
import io.github.opencivilizationplatform.modules.events.infrastructure.GameEventRepository;
import io.github.opencivilizationplatform.modules.trade.domain.TradeAgreement;
import io.github.opencivilizationplatform.modules.trade.infrastructure.TradeRepository;
import io.github.opencivilizationplatform.modules.nexus.domain.NexusMessage;
import io.github.opencivilizationplatform.modules.nexus.domain.NexusMessageType;
import io.github.opencivilizationplatform.modules.nexus.domain.NexusNode;
import io.github.opencivilizationplatform.modules.nexus.infrastructure.NexusNodeRepository;
import io.github.opencivilizationplatform.modules.nexus.infrastructure.NexusMessageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class CivilizationFlowIntegrationTest {

    @LocalServerPort
    private int port;

    private RestTemplate rest;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private CivilizationRepository civilizationRepository;

    @Autowired
    private NexusNodeRepository nexusNodeRepository;

    @Autowired
    private io.github.opencivilizationplatform.modules.nexus.infrastructure.NexusConnectionRepository connectionRepository;

    @Autowired
    private NexusMessageRepository nexusMessageRepository;

    @Autowired
    private GameEventRepository gameEventRepository;

    @Autowired
    private TradeRepository tradeRepository;

    @Autowired
    private io.github.opencivilizationplatform.modules.contribution.infrastructure.CitizenRepository citizenRepository;

    private String authToken;
    private String clientId;

    @BeforeEach
    void setUp() {
        rest = new RestTemplate();
        rest.setUriTemplateHandler(new DefaultUriBuilderFactory("http://localhost:" + port));

        nexusMessageRepository.deleteAll();
        connectionRepository.deleteAll();
        nexusNodeRepository.deleteAll();
        gameEventRepository.deleteAll();
        tradeRepository.deleteAll();
        citizenRepository.deleteAll();
        civilizationRepository.deleteAll();

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
            "/api/v1/civilizations?page=0&size=20", HttpMethod.GET, authRequest(), Map.class
        );
        assertEquals(HttpStatus.OK, allCivs.getStatusCode());
        Map<?, ?> body = allCivs.getBody();
        assertNotNull(body);
        Object contentObj = body.get("content");
        if (contentObj == null) {
            contentObj = body.get("items");
        }
        if (contentObj == null && body.get("page") != null) {
            contentObj = ((Map<?, ?>) body.get("page")).get("content");
        }
        if (contentObj == null) {
            contentObj = body.values().stream().filter(v -> v instanceof List).findFirst().orElse(null);
        }
        assertNotNull(contentObj, "Could not find content list in response body: " + body);
        assertTrue(((List<?>) contentObj).size() >= 1);

        // 4. Get civilization by ID
        ResponseEntity<Civilization> byId = rest.exchange(
            "/api/v1/civilizations/" + civId, HttpMethod.GET, authRequest(), Civilization.class
        );
        assertEquals(HttpStatus.OK, byId.getStatusCode());
        assertEquals("TestCiv", byId.getBody().getName().substring(0, 7));

        // 5. Create a nexus node
        var nodeRequest = Map.of(
            "name", "Primary-Node",
            "type", "PRIMARY",
            "region", "Test Region",
            "civilizationId", Long.parseLong(civId),
            "knowledgeBase", "Test knowledge base"
        );
        ResponseEntity<Map> node = rest.exchange(
            "/api/v1/nexus/nodes", HttpMethod.POST, jsonRequest(nodeRequest), Map.class
        );
        assertEquals(HttpStatus.OK, node.getStatusCode());
        assertNotNull(node.getBody().get("id"));
        String nodeId = node.getBody().get("id").toString();

        // 6. Get nodes for civilization
        ResponseEntity<List> civNodes = rest.exchange(
            "/api/v1/nexus/nodes/civilization/" + civId, HttpMethod.GET, authRequest(), List.class
        );
        assertEquals(HttpStatus.OK, civNodes.getStatusCode());
        assertTrue(civNodes.getBody().size() >= 1);

        // 7. Create a second node for messaging
        var node2Request = Map.of(
            "name", "Secondary-Node",
            "type", "CITY",
            "region", "Test Region",
            "civilizationId", Long.parseLong(civId),
            "knowledgeBase", "Secondary knowledge base"
        );
        ResponseEntity<Map> node2 = rest.exchange(
            "/api/v1/nexus/nodes", HttpMethod.POST, jsonRequest(node2Request), Map.class
        );
        assertEquals(HttpStatus.OK, node2.getStatusCode());
        assertNotNull(node2.getBody(), "node2 response body should not be null");
        assertNotNull(node2.getBody().get("id"), "node2 ID should not be null: " + node2.getBody());
        String node2Id = node2.getBody().get("id").toString();

        // 8. Send a nexus message between nodes
        var msgRequest = Map.of(
            "sourceNodeId", Long.parseLong(nodeId),
            "targetNodeId", Long.parseLong(node2Id),
            "messageType", "KNOWLEDGE_TRANSFER",
            "content", "Hello from integration test!"
        );
        ResponseEntity<Map> msg = rest.exchange(
            "/api/v1/nexus/messages", HttpMethod.POST, jsonRequest(msgRequest), Map.class
        );
        assertEquals(HttpStatus.OK, msg.getStatusCode());
        assertNotNull(msg.getBody().get("id"));

        // 9. Get pending messages for target node
        ResponseEntity<Object> pending = rest.exchange(
            "/api/v1/nexus/messages/pending/" + node2Id, HttpMethod.GET, authRequest(), Object.class
        );
        assertEquals(HttpStatus.OK, pending.getStatusCode());
        assertNotNull(pending.getBody(), "Pending response body should not be null: " + pending);
        assertTrue(pending.getBody() instanceof List, "Pending response body should be a List but was: " + pending.getBody());
        assertTrue(((List<?>) pending.getBody()).size() >= 1);

        // 10. Get conversation between nodes
        ResponseEntity<List> conversation = rest.exchange(
            "/api/v1/nexus/messages/conversation/" + nodeId + "/" + node2Id,
            HttpMethod.GET, authRequest(), List.class
        );
        assertEquals(HttpStatus.OK, conversation.getStatusCode());
        assertTrue(conversation.getBody().size() >= 1);

        // 11. Get nexus network status
        ResponseEntity<Map> status = rest.exchange(
            "/api/v1/nexus/status", HttpMethod.GET, authRequest(), Map.class
        );
        assertEquals(HttpStatus.OK, status.getStatusCode());
        assertNotNull(status.getBody().get("networkStatus"));
    }

    @Test
    void testUnauthenticatedAccess() {
        // Should fail without auth
        try {
            rest.postForEntity(
                "/api/v1/civilizations",
                Map.of("name", "EvilCiv", "scale", "LOCAL", "region", "Nowhere"),
                Map.class
            );
            fail("Expected 401 Unauthorized");
        } catch (org.springframework.web.client.HttpClientErrorException e) {
            assertEquals(HttpStatus.UNAUTHORIZED, e.getStatusCode());
        }
    }

    @Test
    void testPublicEndpointsAreAccessible() {
        // Public GET endpoints should work without auth
        ResponseEntity<List> regions = rest.exchange(
            "/api/v1/nexus/nodes", HttpMethod.GET, null, List.class
        );
        assertEquals(HttpStatus.OK, regions.getStatusCode());

        ResponseEntity<List> leaderboard = rest.exchange(
            "/api/v1/leaderboard", HttpMethod.GET, null, List.class
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
        assertNotNull(simStatus.getBody().get("tick"));
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
        ResponseEntity<List> balance = rest.exchange(
            "/api/v1/strategy/balance", HttpMethod.GET, authRequest(), List.class
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
            "/api/v1/nexus/status", HttpMethod.GET, entity, Map.class
        );
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
