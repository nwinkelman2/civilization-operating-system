package io.github.opencivilizationplatform.modules.nexus.api;

import io.github.opencivilizationplatform.modules.nexus.application.NexusMeshService;
import io.github.opencivilizationplatform.modules.nexus.domain.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

@RestController
@RequestMapping("/api/v1/nexus")
@Tag(name = "Nexus Mesh", description = "Nexus neural mesh network endpoints")
public class NexusController {

    private final NexusMeshService meshService;
    private final io.github.opencivilizationplatform.modules.nexus.infrastructure.MeshTradeRepository meshTradeRepository;
    private final io.github.opencivilizationplatform.modules.nexus.infrastructure.MigrationRequestRepository migrationRequestRepository;
    private final io.github.opencivilizationplatform.modules.civilization.infrastructure.CivilizationRepository civilizationRepository;
    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    public NexusController(NexusMeshService meshService,
                           io.github.opencivilizationplatform.modules.nexus.infrastructure.MeshTradeRepository meshTradeRepository,
                           io.github.opencivilizationplatform.modules.nexus.infrastructure.MigrationRequestRepository migrationRequestRepository,
                           io.github.opencivilizationplatform.modules.civilization.infrastructure.CivilizationRepository civilizationRepository) {
        this.meshService = meshService;
        this.meshTradeRepository = meshTradeRepository;
        this.migrationRequestRepository = migrationRequestRepository;
        this.civilizationRepository = civilizationRepository;
    }

    // --- Nodes ---

    @GetMapping("/nodes")
    @Operation(summary = "List all Nexus nodes")
    public List<NexusNode> getAllNodes() {
        return meshService.getAllNodes();
    }

    @GetMapping("/nodes/civilization/{civId}")
    @Operation(summary = "Get nodes for a civilization")
    public List<NexusNode> getNodesByCivilization(@PathVariable Long civId) {
        return meshService.getNodesForCivilization(civId);
    }

    @PostMapping("/nodes")
    @Operation(summary = "Register a new Nexus node")
    public NexusNode registerNode(@Valid @RequestBody RegisterNodeRequest request) {
        return meshService.registerNode(
            request.name(), request.type(), request.region(),
            request.civilizationId(), request.knowledgeBase()
        );
    }

    @PatchMapping("/nodes/{id}/status")
    @Operation(summary = "Update node status")
    public NexusNode updateNodeStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return meshService.updateNodeStatus(id, NexusNodeStatus.valueOf(body.get("status")));
    }

    // --- Messages ---

    @PostMapping("/messages")
    @Operation(summary = "Send a Nexus message")
    public NexusMessage sendMessage(@Valid @RequestBody SendMessageRequest request) {
        return meshService.sendMessage(
            request.sourceNodeId(), request.targetNodeId(),
            request.messageType(), request.content()
        );
    }

    @GetMapping("/messages/conversation/{nodeA}/{nodeB}")
    @Operation(summary = "Get conversation between two nodes")
    public List<NexusMessage> getConversation(@PathVariable Long nodeA, @PathVariable Long nodeB) {
        return meshService.getConversation(nodeA, nodeB);
    }

    @GetMapping("/messages/pending/{nodeId}")
    @Operation(summary = "Get pending messages for a node")
    public List<NexusMessage> getPending(@PathVariable Long nodeId) {
        return meshService.getPendingMessages(nodeId);
    }

    // --- Connections ---

    @GetMapping("/connections")
    @Operation(summary = "List all mesh connections")
    public List<NexusConnection> getAllConnections() {
        return meshService.getAllConnections();
    }

    @GetMapping("/connections/node/{nodeId}")
    @Operation(summary = "Get connections for a node")
    public List<NexusConnection> getNodeConnections(@PathVariable Long nodeId) {
        return meshService.getConnectionsForNode(nodeId);
    }

    // --- SSE Stream ---

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "SSE stream for real-time Nexus messages")
    public SseEmitter streamMessages() {
        SseEmitter emitter = new SseEmitter(0L); // no timeout
        emitters.add(emitter);

        meshService.addMessageListener(msg -> {
            try {
                emitter.send(SseEmitter.event()
                    .name("Nexus-message")
                    .data(msg));
            } catch (Exception e) {
                emitters.remove(emitter);
                meshService.removeMessageListener(m -> {});
            }
        });

        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));

        return emitter;
    }

    @GetMapping("/trades")
    @Operation(summary = "Get list of all mesh trades")
    public List<io.github.opencivilizationplatform.modules.nexus.domain.MeshTrade> getTrades() {
        return meshTradeRepository.findAllByOrderByCreatedAtDesc();
    }

    @GetMapping("/migrations")
    @Operation(summary = "Get list of all migrations")
    public List<io.github.opencivilizationplatform.modules.nexus.domain.MigrationRequest> getMigrations() {
        return migrationRequestRepository.findAllByOrderByCreatedAtDesc();
    }

    @PostMapping("/civilizations/{id}/robot-priorities")
    @Operation(summary = "Update civilization robot priorities")
    public Map<String, Object> updateRobotPriorities(@PathVariable Long id, @RequestBody Map<String, Integer> priorities) {
        io.github.opencivilizationplatform.modules.civilization.domain.Civilization civ = civilizationRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Civilization not found"));
        
        civ.setAgriBotsPriority(priorities.getOrDefault("agri", 25));
        civ.setAquaBotsPriority(priorities.getOrDefault("aqua", 25));
        civ.setExploreBotsPriority(priorities.getOrDefault("explore", 25));
        civ.setUtilityBotsPriority(priorities.getOrDefault("utility", 25));
        civ.setEcoBotsPriority(priorities.getOrDefault("eco", 0));
        civ.setScienceBotsPriority(priorities.getOrDefault("science", 0));
        civ.setSecurityBotsPriority(priorities.getOrDefault("security", 0));
        civ.setSpyBotsPriority(priorities.getOrDefault("spy", 0));
        civilizationRepository.save(civ);
        return Map.of("success", true, "message", "Prioridades de robôs atualizadas com sucesso!");
    }

    // --- Network Status ---

    @GetMapping("/status")
    @Operation(summary = "Get mesh network status summary")
    public Map<String, Object> getNetworkStatus() {
        return meshService.getNetworkStatus();
    }
}

record RegisterNodeRequest(
    @NotBlank String name,
    @NotNull NexusNodeType type,
    String region,
    @NotNull Long civilizationId,
    String knowledgeBase
) {}

record SendMessageRequest(
    @NotNull Long sourceNodeId,
    @NotNull Long targetNodeId,
    @NotNull NexusMessageType messageType,
    @NotBlank String content
) {}

