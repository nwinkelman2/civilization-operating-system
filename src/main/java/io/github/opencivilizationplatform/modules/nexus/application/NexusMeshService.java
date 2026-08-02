package io.github.opencivilizationplatform.modules.nexus.application;

import io.github.opencivilizationplatform.core.eventbus.EventBus;
import io.github.opencivilizationplatform.core.eventbus.events.VoxtexMessageSentEvent;
import io.github.opencivilizationplatform.modules.nexus.domain.*;
import io.github.opencivilizationplatform.modules.nexus.dto.NexusMessageSyncDTO;
import io.github.opencivilizationplatform.modules.nexus.infrastructure.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import org.springframework.data.redis.core.StringRedisTemplate;
import tools.jackson.databind.ObjectMapper;

@Service
public class NexusMeshService {

    private static final Logger log = LoggerFactory.getLogger(NexusMeshService.class);

    private final NexusNodeRepository nodeRepository;
    private final NexusMessageRepository messageRepository;
    private final NexusConnectionRepository connectionRepository;
    private final EventBus eventBus;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    // SSE emitters for real-time streaming
    private final List<Consumer<NexusMessage>> messageListeners = new CopyOnWriteArrayList<>();

    public NexusMeshService(NexusNodeRepository nodeRepository,
                              NexusMessageRepository messageRepository,
                              NexusConnectionRepository connectionRepository,
                              EventBus eventBus,
                              StringRedisTemplate redisTemplate,
                              ObjectMapper objectMapper) {
        this.nodeRepository = nodeRepository;
        this.messageRepository = messageRepository;
        this.connectionRepository = connectionRepository;
        this.eventBus = eventBus;
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    // --- Node Management ---

    @Transactional
    public NexusNode registerNode(String name, NexusNodeType type, String region,
                                    Long civilizationId, String knowledgeBase) {
        NexusNode node = new NexusNode();
        node.setName(name);
        node.setType(type);
        node.setRegion(region);
        node.setStatus(NexusNodeStatus.BOOTING);
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
    public NexusNode updateNodeStatus(Long nodeId, NexusNodeStatus status) {
        NexusNode node = nodeRepository.findById(nodeId).orElseThrow();
        node.setStatus(status);
        node.setLastActiveAt(LocalDateTime.now());
        return nodeRepository.save(node);
    }

    @Transactional(readOnly = true)
    public List<NexusNode> getNodesForCivilization(Long civilizationId) {
        return nodeRepository.findByCivilizationId(civilizationId);
    }

    @Transactional(readOnly = true)
    public List<NexusNode> getAllNodes() {
        return nodeRepository.findAll();
    }

    // --- Message Passing ---

    @Transactional
    public NexusMessage sendMessage(Long sourceNodeId, Long targetNodeId,
                                      NexusMessageType messageType, String content) {
        NexusNode source = nodeRepository.findById(sourceNodeId).orElseThrow();
        NexusNode target = nodeRepository.findById(targetNodeId).orElseThrow();

        NexusMessage msg = new NexusMessage();
        msg.setSourceNode(source);
        msg.setTargetNode(target);
        msg.setMessageType(messageType);
        msg.setContent(content);
        msg = messageRepository.save(msg);

        // Publish to Redis instead of notifying local listeners directly
        publishEventToRedis(msg);

        eventBus.publish(new VoxtexMessageSentEvent(
            "NexusMeshService", msg.getId(), msg.getSourceNode().getId(),
            msg.getTargetNode().getId(), msg.getMessageType().name(), msg.getContent()
        ));

        log.info("Nexus MESH: {} -> {} [{}]", source.getName(), target.getName(), messageType);
        return msg;
    }

    @Transactional(readOnly = true)
    public List<NexusMessage> getConversation(Long nodeAId, Long nodeBId) {
        return messageRepository.findBySourceNodeIdOrTargetNodeIdOrderBySentAtDesc(nodeAId, nodeBId);
    }

    @Transactional(readOnly = true)
    public List<NexusMessage> getPendingMessages(Long nodeId) {
        return messageRepository.findByTargetNodeIdAndDeliveredFalse(nodeId);
    }

    @Transactional(readOnly = true)
    public long getPendingCount(Long nodeId) {
        return messageRepository.countByTargetNodeIdAndDeliveredFalse(nodeId);
    }

    // --- Connection Management ---

    @Transactional(readOnly = true)
    public List<NexusConnection> getConnectionsForNode(Long nodeId) {
        NexusNode node = nodeRepository.findById(nodeId).orElseThrow();
        return connectionRepository.findByNodeAOrNodeB(node, node);
    }

    @Transactional(readOnly = true)
    public List<NexusConnection> getAllConnections() {
        return connectionRepository.findAll();
    }

    @Transactional(readOnly = true)
    public java.util.Map<String, Object> getNetworkStatus() {
        var nodes = getAllNodes();
        var conns = getAllConnections();
        long activeNodes = nodes.stream().filter(n -> n.getStatus() == NexusNodeStatus.ACTIVE).count();
        double avgStrength = conns.stream().mapToDouble(NexusConnection::getStrength).average().orElse(0);
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
    @SchedulerLock(name = "NexusMeshTick", lockAtMostFor = "12s", lockAtLeastFor = "5s")
    public void processMeshTick() {
        log.debug("Nexus MESH TICK: Processing messages and updating network");

        // 1. Deliver pending messages (update hop count, mark delivered)
        List<NexusMessage> pending = messageRepository.findByDeliveredFalseOrderBySentAtAsc();
        for (NexusMessage msg : pending) {
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
                NexusNode nextHop = conn.getNodeA().equals(msg.getSourceNode())
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
                    publishEventToRedis(msg);
                    log.debug("Nexus: Message {} delivered ({} hops)", msg.getId(), msg.getHopCount());
                    break;
                }
            }
        }

        // 2. Strengthen connections used recently, decay unused ones
        List<NexusConnection> allConns = connectionRepository.findAll();
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
            NexusMessageType[] types = NexusMessageType.values();
            String[] sampleMessages = {
                "Neural sync pulse: network stable",
                "Knowledge base updated with new patterns",
                "Resource allocation optimized via mesh consensus",
                "Innovation discovered: efficiency +2%",
                "Mesh integrity check: all routes operational"
            };
            String msg = sampleMessages[(int)(Math.random() * sampleMessages.length)];
            NexusMessage autoMsg = new NexusMessage();
            autoMsg.setSourceNode(conn.getNodeA());
            autoMsg.setTargetNode(conn.getNodeB());
            autoMsg.setMessageType(types[(int)(Math.random() * types.length)]);
            autoMsg.setContent(msg);
            autoMsg.setDelivered(true);
            autoMsg.setDeliveredAt(LocalDateTime.now());
            messageRepository.save(autoMsg);
            publishEventToRedis(autoMsg);
            log.debug("Nexus: Auto-message generated: {}", msg);
        }
    }

    // --- SSE Support ---

    public void addMessageListener(Consumer<NexusMessage> listener) {
        messageListeners.add(listener);
    }

    public void removeMessageListener(Consumer<NexusMessage> listener) {
        messageListeners.remove(listener);
    }

    private void publishEventToRedis(NexusMessage msg) {
        if (TransactionSynchronizationManager.isActualTransactionActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    sendEventToRedis(msg);
                }
            });
        } else {
            sendEventToRedis(msg);
        }
    }

    private void sendEventToRedis(NexusMessage msg) {
        try {
            Long sourceNodeId = msg.getSourceNode() != null ? msg.getSourceNode().getId() : null;
            String sourceNodeName = msg.getSourceNode() != null ? msg.getSourceNode().getName() : null;
            Long targetNodeId = msg.getTargetNode() != null ? msg.getTargetNode().getId() : null;
            String targetNodeName = msg.getTargetNode() != null ? msg.getTargetNode().getName() : null;

            NexusMessageSyncDTO dto = new NexusMessageSyncDTO(
                msg.getId(),
                sourceNodeId,
                sourceNodeName,
                targetNodeId,
                targetNodeName,
                msg.getMessageType(),
                msg.getContent(),
                msg.getHopCount()
            );
            String json = objectMapper.writeValueAsString(dto);
            redisTemplate.convertAndSend("Nexus-mesh-events", json);
        } catch (Exception e) {
            log.error("Failed to publish message event to Redis", e);
        }
    }

    public void notifyListenersLocally(NexusMessage msg) {
        for (var listener : messageListeners) {
            try {
                listener.accept(msg);
            } catch (Exception e) {
                messageListeners.remove(listener);
            }
        }
    }

    // --- Internal ---

    private void connectToNeighbors(NexusNode node) {
        // Connect to other nodes from the same civilization
        if (node.getCivilization() != null && node.getCivilization().getId() != null) {
            var sameCiv = nodeRepository.findByCivilizationId(
                node.getCivilization().getId());
            for (var neighbor : sameCiv) {
                if (neighbor.getId() != null && !neighbor.getId().equals(node.getId())) {
                    NexusConnection conn = new NexusConnection();
                    conn.setNodeA(node);
                    conn.setNodeB(neighbor);
                    connectionRepository.save(conn);
                }
            }
        }

        // Random connections to nodes from other civs
        var allNodes = nodeRepository.findAll();
        int connectionsToMake = Math.min(3, allNodes.size() / 2);
        for (int i = 0; i < connectionsToMake; i++) {
            var target = allNodes.get((int)(Math.random() * allNodes.size()));
            if (target.getId() != null && !target.getId().equals(node.getId()) &&
                target.getCivilization() != null && target.getCivilization().getId() != null &&
                node.getCivilization() != null && node.getCivilization().getId() != null &&
                !target.getCivilization().getId().equals(node.getCivilization().getId())) {
                NexusConnection conn = new NexusConnection();
                conn.setNodeA(node);
                conn.setNodeB(target);
                conn.setStrength(0.2);
                connectionRepository.save(conn);
            }
        }
    }
}
