package io.github.opencivilizationplatform.modules.voxtex.application;

import io.github.opencivilizationplatform.modules.voxtex.domain.*;
import io.github.opencivilizationplatform.modules.voxtex.infrastructure.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

@Service
public class VoxtexMeshService {

    private static final Logger log = LoggerFactory.getLogger(VoxtexMeshService.class);

    private final VoxtexNodeRepository nodeRepository;
    private final VoxtexMessageRepository messageRepository;
    private final VoxtexConnectionRepository connectionRepository;

    // SSE emitters for real-time streaming
    private final List<Consumer<VoxtexMessage>> messageListeners = new CopyOnWriteArrayList<>();

    public VoxtexMeshService(VoxtexNodeRepository nodeRepository,
                              VoxtexMessageRepository messageRepository,
                              VoxtexConnectionRepository connectionRepository) {
        this.nodeRepository = nodeRepository;
        this.messageRepository = messageRepository;
        this.connectionRepository = connectionRepository;
    }

    // --- Node Management ---

    @Transactional
    public VoxtexNode registerNode(String name, VoxtexNodeType type, String region,
                                    Long civilizationId, String knowledgeBase) {
        VoxtexNode node = new VoxtexNode();
        node.setName(name);
        node.setType(type);
        node.setRegion(region);
        node.setStatus(VoxtexNodeStatus.BOOTING);
        node.setKnowledgeBase(knowledgeBase);

        var civ = new io.github.opencivilizationplatform.modules.civilization.domain.Civilization();
        civ.setId(civilizationId);
        node.setCivilization(civ);

        node = nodeRepository.save(node);

        // Auto-connect to other nodes from same civ + random nearby civs
        connectToNeighbors(node);

        return node;
    }

    @Transactional
    public VoxtexNode updateNodeStatus(Long nodeId, VoxtexNodeStatus status) {
        VoxtexNode node = nodeRepository.findById(nodeId).orElseThrow();
        node.setStatus(status);
        node.setLastActiveAt(LocalDateTime.now());
        return nodeRepository.save(node);
    }

    @Transactional(readOnly = true)
    public List<VoxtexNode> getNodesForCivilization(Long civilizationId) {
        return nodeRepository.findByCivilizationId(civilizationId);
    }

    @Transactional(readOnly = true)
    public List<VoxtexNode> getAllNodes() {
        return nodeRepository.findAll();
    }

    // --- Message Passing ---

    @Transactional
    public VoxtexMessage sendMessage(Long sourceNodeId, Long targetNodeId,
                                      VoxtexMessageType messageType, String content) {
        VoxtexNode source = nodeRepository.findById(sourceNodeId).orElseThrow();
        VoxtexNode target = nodeRepository.findById(targetNodeId).orElseThrow();

        VoxtexMessage msg = new VoxtexMessage();
        msg.setSourceNode(source);
        msg.setTargetNode(target);
        msg.setMessageType(messageType);
        msg.setContent(content);
        msg = messageRepository.save(msg);

        // Notify SSE listeners
        notifyListeners(msg);

        log.info("VOXTEX MESH: {} -> {} [{}]", source.getName(), target.getName(), messageType);
        return msg;
    }

    @Transactional(readOnly = true)
    public List<VoxtexMessage> getConversation(Long nodeAId, Long nodeBId) {
        return messageRepository.findBySourceNodeIdOrTargetNodeIdOrderBySentAtDesc(nodeAId, nodeBId);
    }

    @Transactional(readOnly = true)
    public List<VoxtexMessage> getPendingMessages(Long nodeId) {
        return messageRepository.findByTargetNodeIdAndDeliveredFalse(nodeId);
    }

    @Transactional(readOnly = true)
    public long getPendingCount(Long nodeId) {
        return messageRepository.countByTargetNodeIdAndDeliveredFalse(nodeId);
    }

    // --- Connection Management ---

    @Transactional(readOnly = true)
    public List<VoxtexConnection> getConnectionsForNode(Long nodeId) {
        VoxtexNode node = nodeRepository.findById(nodeId).orElseThrow();
        return connectionRepository.findByNodeAOrNodeB(node, node);
    }

    @Transactional(readOnly = true)
    public List<VoxtexConnection> getAllConnections() {
        return connectionRepository.findAll();
    }

    @Transactional(readOnly = true)
    public java.util.Map<String, Object> getNetworkStatus() {
        var nodes = getAllNodes();
        var conns = getAllConnections();
        long activeNodes = nodes.stream().filter(n -> n.getStatus() == VoxtexNodeStatus.ACTIVE).count();
        double avgStrength = conns.stream().mapToDouble(VoxtexConnection::getStrength).average().orElse(0);
        return java.util.Map.of(
            "totalNodes", nodes.size(),
            "activeNodes", activeNodes,
            "totalConnections", conns.size(),
            "averageConnectionStrength", Math.round(avgStrength * 100.0) / 100.0,
            "networkStatus", activeNodes > 0 ? "ONLINE" : "OFFLINE"
        );
    }

    // --- Neural Network Simulation ---

    @Transactional
    @Scheduled(fixedRate = 15000)
    public void processMeshTick() {
        log.debug("VOXTEX MESH TICK: Processing messages and updating network");

        // 1. Deliver pending messages (update hop count, mark delivered)
        List<VoxtexMessage> pending = messageRepository.findByDeliveredFalseOrderBySentAtAsc();
        for (VoxtexMessage msg : pending) {
            if (msg.getHopCount() >= 5) {
                // Max hops reached - mark as delivered anyway
                msg.setDelivered(true);
                msg.setDeliveredAt(LocalDateTime.now());
                messageRepository.save(msg);
                continue;
            }

            // Find connections and try to route
            var sourceConns = connectionRepository.findByNodeAOrNodeB(
                msg.getSourceNode(), msg.getSourceNode());

            for (var conn : sourceConns) {
                VoxtexNode nextHop = conn.getNodeA().equals(msg.getSourceNode())
                    ? conn.getNodeB() : conn.getNodeA();

                if (nextHop.equals(msg.getTargetNode())) {
                    // Direct delivery!
                    msg.setDelivered(true);
                    msg.setDeliveredAt(LocalDateTime.now());
                    msg.setHopCount(msg.getHopCount() + 1);
                    conn.setMessagesExchanged(conn.getMessagesExchanged() + 1);
                    conn.setStrength(Math.min(1.0, conn.getStrength() + 0.05));
                    conn.setLastActivityAt(LocalDateTime.now());
                    connectionRepository.save(conn);
                    messageRepository.save(msg);
                    notifyListeners(msg);
                    log.debug("VOXTEX: Message {} delivered ({} hops)", msg.getId(), msg.getHopCount());
                    break;
                }
            }
        }

        // 2. Strengthen connections used recently, decay unused ones
        List<VoxtexConnection> allConns = connectionRepository.findAll();
        for (var conn : allConns) {
            if (conn.getLastActivityAt().isAfter(LocalDateTime.now().minusMinutes(5))) {
                conn.setStrength(Math.min(1.0, conn.getStrength() + 0.02));
            } else {
                conn.setStrength(Math.max(0.1, conn.getStrength() - 0.01));
            }
            connectionRepository.save(conn);
        }

        // 3. Randomly generate messages for organic network feel
        if (!allConns.isEmpty() && Math.random() < 0.3) {
            var conn = allConns.get((int)(Math.random() * allConns.size()));
            VoxtexMessageType[] types = VoxtexMessageType.values();
            String[] sampleMessages = {
                "Neural sync pulse: network stable",
                "Knowledge base updated with new patterns",
                "Resource allocation optimized via mesh consensus",
                "Innovation discovered: efficiency +2%",
                "Mesh integrity check: all routes operational"
            };
            String msg = sampleMessages[(int)(Math.random() * sampleMessages.length)];
            VoxtexMessage autoMsg = new VoxtexMessage();
            autoMsg.setSourceNode(conn.getNodeA());
            autoMsg.setTargetNode(conn.getNodeB());
            autoMsg.setMessageType(types[(int)(Math.random() * types.length)]);
            autoMsg.setContent(msg);
            autoMsg.setDelivered(true);
            autoMsg.setDeliveredAt(LocalDateTime.now());
            messageRepository.save(autoMsg);
            notifyListeners(autoMsg);
            log.debug("VOXTEX: Auto-message generated: {}", msg);
        }
    }

    // --- SSE Support ---

    public void addMessageListener(Consumer<VoxtexMessage> listener) {
        messageListeners.add(listener);
    }

    public void removeMessageListener(Consumer<VoxtexMessage> listener) {
        messageListeners.remove(listener);
    }

    private void notifyListeners(VoxtexMessage msg) {
        for (var listener : messageListeners) {
            try {
                listener.accept(msg);
            } catch (Exception e) {
                messageListeners.remove(listener);
            }
        }
    }

    // --- Internal ---

    private void connectToNeighbors(VoxtexNode node) {
        // Connect to other nodes from the same civilization
        var sameCiv = nodeRepository.findByCivilizationId(
            node.getCivilization().getId());
        for (var neighbor : sameCiv) {
            if (!neighbor.getId().equals(node.getId())) {
                VoxtexConnection conn = new VoxtexConnection();
                conn.setNodeA(node);
                conn.setNodeB(neighbor);
                connectionRepository.save(conn);
            }
        }

        // Random connections to nodes from other civs
        var allNodes = nodeRepository.findAll();
        int connectionsToMake = Math.min(3, allNodes.size() / 2);
        for (int i = 0; i < connectionsToMake; i++) {
            var target = allNodes.get((int)(Math.random() * allNodes.size()));
            if (!target.getId().equals(node.getId()) &&
                !target.getCivilization().getId().equals(node.getCivilization().getId())) {
                VoxtexConnection conn = new VoxtexConnection();
                conn.setNodeA(node);
                conn.setNodeB(target);
                conn.setStrength(0.2);
                connectionRepository.save(conn);
            }
        }
    }
}
