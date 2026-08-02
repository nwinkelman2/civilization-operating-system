package io.github.opencivilizationplatform.modules.nexus.application;

import io.github.opencivilizationplatform.modules.nexus.domain.*;
import io.github.opencivilizationplatform.modules.nexus.infrastructure.*;
import io.github.opencivilizationplatform.modules.trade.application.TradeService;
import io.github.opencivilizationplatform.modules.civilization.infrastructure.CivilizationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
public class NexusAIService {

    private static final Logger log = LoggerFactory.getLogger(NexusAIService.class);

    private final NexusNodeRepository nodeRepository;
    private final NexusConnectionRepository connectionRepository;
    private final NexusMessageRepository messageRepository;
    private final CivilizationRepository civRepository;
    private final TradeService tradeService;
    private final Random random = new Random();

    public NexusAIService(NexusNodeRepository nodeRepository,
                           NexusConnectionRepository connectionRepository,
                           NexusMessageRepository messageRepository,
                           CivilizationRepository civRepository,
                           TradeService tradeService) {
        this.nodeRepository = nodeRepository;
        this.connectionRepository = connectionRepository;
        this.messageRepository = messageRepository;
        this.civRepository = civRepository;
        this.tradeService = tradeService;
    }

    @Transactional
    @Scheduled(fixedRate = 20000)
    public void aiTick() {
        var activeNodes = nodeRepository.findByStatus(NexusNodeStatus.ACTIVE);
        if (activeNodes.size() < 2) return;

        log.debug("Nexus AI: Processing {} active nodes", activeNodes.size());

        for (var node : activeNodes) {
            // 40% chance of AI action per node per tick
            if (random.nextDouble() > 0.4) continue;

            var connections = connectionRepository.findByNodeAOrNodeB(node, node);
            if (connections.isEmpty()) continue;

            // Pick a random connected node
            var conn = connections.get(random.nextInt(connections.size()));
            var targetNode = conn.getNodeA().equals(node) ? conn.getNodeB() : conn.getNodeA();

            // Decide what to do
            double decision = random.nextDouble();
            if (decision < 0.3) {
                // Share knowledge
                String knowledge = generateKnowledge(node);
                NexusMessage msg = new NexusMessage();
                msg.setSourceNode(node);
                msg.setTargetNode(targetNode);
                msg.setMessageType(NexusMessageType.KNOWLEDGE_TRANSFER);
                msg.setContent("AI: " + knowledge);
                msg.setDelivered(true);
                msg.setDeliveredAt(LocalDateTime.now());
                messageRepository.save(msg);

                // Update knowledge base
                String existingKb = node.getKnowledgeBase();
                String newKb = knowledge + " | " + (existingKb != null ? existingKb : "");
                if (newKb.length() > 1000) newKb = newKb.substring(0, 1000);
                node.setKnowledgeBase(newKb);
                nodeRepository.save(node);

                log.debug("Nexus AI: {} shared knowledge with {}", node.getName(), targetNode.getName());
            } else if (decision < 0.55) {
                // Propose trade between civilizations
                Long fromCiv = node.getCivilization().getId();
                Long toCiv = targetNode.getCivilization().getId();
                if (!fromCiv.equals(toCiv)) {
                    String[] resources = {"FOOD", "WATER", "MINERAL", "ENERGY", "MATERIAL"};
                    String resource = resources[random.nextInt(resources.length)];
                    double qty = 10 + random.nextDouble() * 100;
                    try {
                        tradeService.proposeTrade(fromCiv, toCiv, resource, Math.round(qty * 10.0) / 10.0);
                        log.debug("Nexus AI: {} proposed trade to {}", node.getName(), targetNode.getName());
                    } catch (Exception e) {
                        log.debug("Nexus AI: Trade proposal failed: {}", e.getMessage());
                    }
                }
            } else if (decision < 0.75) {
                // Send diplomatic message
                String[] diploMessages = {
                    "Greetings from " + node.getCivilization().getName() + ". We seek peaceful coexistence.",
                    "Our sensors indicate resource abundance in your region. Shall we cooperate?",
                    "The mesh network strengthens our connection. Let us share data.",
                    "We have observed your technological progress. Impressive.",
                    "Proposal: joint research initiative for mutual benefit."
                };
                String msg = diploMessages[random.nextInt(diploMessages.length)];
                NexusMessage vMsg = new NexusMessage();
                vMsg.setSourceNode(node);
                vMsg.setTargetNode(targetNode);
                vMsg.setMessageType(NexusMessageType.DIPLOMATIC_MESSAGE);
                vMsg.setContent("AI: " + msg);
                vMsg.setDelivered(true);
                vMsg.setDeliveredAt(LocalDateTime.now());
                messageRepository.save(vMsg);
                log.debug("Nexus AI: {} sent diplomatic message to {}", node.getName(), targetNode.getName());
            } else {
                // Neural sync - strengthen connection
                conn.setStrength(Math.min(1.0, conn.getStrength() + 0.1));
                conn.setLastActivityAt(LocalDateTime.now());
                connectionRepository.save(conn);

                NexusMessage vMsg = new NexusMessage();
                vMsg.setSourceNode(node);
                vMsg.setTargetNode(targetNode);
                vMsg.setMessageType(NexusMessageType.NEURAL_SYNC);
                vMsg.setContent("AI: Neural synchronization complete. Connection strength: " +
                    String.format("%.0f%%", conn.getStrength() * 100));
                vMsg.setDelivered(true);
                vMsg.setDeliveredAt(LocalDateTime.now());
                messageRepository.save(vMsg);
                log.debug("Nexus AI: {} synced with {} (strength: {})", node.getName(), targetNode.getName(), conn.getStrength());
            }
        }
    }

    private String generateKnowledge(NexusNode node) {
        String[] templates = {
            "Analyzed resource patterns in %s region. Efficiency gains possible.",
            "Detected anomaly in energy consumption. Recommending optimization.",
            "New algorithm for %s resource distribution developed.",
            "Biosphere data from %s indicates stable conditions.",
            "Population metrics from %s suggest growth trend."
        };
        String region = node.getRegion() != null ? node.getRegion() : "local";
        return String.format(templates[random.nextInt(templates.length)], region);
    }
}

