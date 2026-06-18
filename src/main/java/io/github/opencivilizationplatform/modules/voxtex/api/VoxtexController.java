package io.github.opencivilizationplatform.modules.voxtex.api;

import io.github.opencivilizationplatform.modules.voxtex.application.VoxtexMeshService;
import io.github.opencivilizationplatform.modules.voxtex.domain.*;
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
@RequestMapping("/api/v1/voxtex")
@Tag(name = "Voxtex Mesh", description = "Voxtex neural mesh network endpoints")
public class VoxtexController {

    private final VoxtexMeshService meshService;
    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    public VoxtexController(VoxtexMeshService meshService) {
        this.meshService = meshService;
    }

    // --- Nodes ---

    @GetMapping("/nodes")
    @Operation(summary = "List all voxtex nodes")
    public List<VoxtexNode> getAllNodes() {
        return meshService.getAllNodes();
    }

    @GetMapping("/nodes/civilization/{civId}")
    @Operation(summary = "Get nodes for a civilization")
    public List<VoxtexNode> getNodesByCivilization(@PathVariable Long civId) {
        return meshService.getNodesForCivilization(civId);
    }

    @PostMapping("/nodes")
    @Operation(summary = "Register a new voxtex node")
    public VoxtexNode registerNode(@Valid @RequestBody RegisterNodeRequest request) {
        return meshService.registerNode(
            request.name(), request.type(), request.region(),
            request.civilizationId(), request.knowledgeBase()
        );
    }

    @PatchMapping("/nodes/{id}/status")
    @Operation(summary = "Update node status")
    public VoxtexNode updateNodeStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return meshService.updateNodeStatus(id, VoxtexNodeStatus.valueOf(body.get("status")));
    }

    // --- Messages ---

    @PostMapping("/messages")
    @Operation(summary = "Send a voxtex message")
    public VoxtexMessage sendMessage(@Valid @RequestBody SendMessageRequest request) {
        return meshService.sendMessage(
            request.sourceNodeId(), request.targetNodeId(),
            request.messageType(), request.content()
        );
    }

    @GetMapping("/messages/conversation/{nodeA}/{nodeB}")
    @Operation(summary = "Get conversation between two nodes")
    public List<VoxtexMessage> getConversation(@PathVariable Long nodeA, @PathVariable Long nodeB) {
        return meshService.getConversation(nodeA, nodeB);
    }

    @GetMapping("/messages/pending/{nodeId}")
    @Operation(summary = "Get pending messages for a node")
    public List<VoxtexMessage> getPending(@PathVariable Long nodeId) {
        return meshService.getPendingMessages(nodeId);
    }

    // --- Connections ---

    @GetMapping("/connections")
    @Operation(summary = "List all mesh connections")
    public List<VoxtexConnection> getAllConnections() {
        return meshService.getAllConnections();
    }

    @GetMapping("/connections/node/{nodeId}")
    @Operation(summary = "Get connections for a node")
    public List<VoxtexConnection> getNodeConnections(@PathVariable Long nodeId) {
        return meshService.getConnectionsForNode(nodeId);
    }

    // --- SSE Stream ---

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "SSE stream for real-time voxtex messages")
    public SseEmitter streamMessages() {
        SseEmitter emitter = new SseEmitter(0L); // no timeout
        emitters.add(emitter);

        meshService.addMessageListener(msg -> {
            try {
                emitter.send(SseEmitter.event()
                    .name("voxtex-message")
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

    // --- Network Status ---

    @GetMapping("/status")
    @Operation(summary = "Get mesh network status summary")
    public Map<String, Object> getNetworkStatus() {
        return meshService.getNetworkStatus();
    }
}

record RegisterNodeRequest(
    @NotBlank String name,
    @NotNull VoxtexNodeType type,
    String region,
    @NotNull Long civilizationId,
    String knowledgeBase
) {}

record SendMessageRequest(
    @NotNull Long sourceNodeId,
    @NotNull Long targetNodeId,
    @NotNull VoxtexMessageType messageType,
    @NotBlank String content
) {}
