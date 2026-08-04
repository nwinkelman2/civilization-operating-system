package io.github.opencivilizationplatform.modules.social.application;

import io.github.opencivilizationplatform.modules.social.domain.AgentRelationship;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SocialGraphService {

    private static final Logger log = LoggerFactory.getLogger(SocialGraphService.class);
    private final Map<String, AgentRelationship> relationships = new ConcurrentHashMap<>();

    private String buildKey(String sourceAgentId, String targetAgentId) {
        return sourceAgentId + "->" + targetAgentId;
    }

    public AgentRelationship setRelationship(String sourceAgentId, String targetAgentId, double trustScore, double rivalryScore) {
        if (sourceAgentId == null || targetAgentId == null) {
            return null;
        }
        AgentRelationship rel = new AgentRelationship(sourceAgentId, targetAgentId, trustScore, rivalryScore);
        relationships.put(buildKey(sourceAgentId, targetAgentId), rel);
        return rel;
    }

    public AgentRelationship updateRelationship(String sourceAgentId, String targetAgentId, double trustDelta, double rivalryDelta) {
        if (sourceAgentId == null || targetAgentId == null) {
            return null;
        }
        String key = buildKey(sourceAgentId, targetAgentId);
        AgentRelationship existing = relationships.get(key);
        double newTrust = (existing != null ? existing.trustScore() : 50.0) + trustDelta;
        double newRivalry = (existing != null ? existing.rivalryScore() : 0.0) + rivalryDelta;

        AgentRelationship updated = new AgentRelationship(sourceAgentId, targetAgentId, newTrust, newRivalry);
        relationships.put(key, updated);
        return updated;
    }

    public Optional<AgentRelationship> getRelationship(String sourceAgentId, String targetAgentId) {
        if (sourceAgentId == null || targetAgentId == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(relationships.get(buildKey(sourceAgentId, targetAgentId)));
    }

    public List<AgentRelationship> getRelationshipsForAgent(String agentId) {
        if (agentId == null) {
            return Collections.emptyList();
        }
        List<AgentRelationship> result = new ArrayList<>();
        for (AgentRelationship rel : relationships.values()) {
            if (rel.sourceAgentId().equals(agentId) || rel.targetAgentId().equals(agentId)) {
                result.add(rel);
            }
        }
        return result;
    }

    public List<AgentRelationship> getAllRelationships() {
        return new ArrayList<>(relationships.values());
    }

    public double computeNetworkCohesion() {
        if (relationships.isEmpty()) {
            return 0.0;
        }
        double totalTrust = 0.0;
        double totalRivalry = 0.0;
        for (AgentRelationship rel : relationships.values()) {
            totalTrust += rel.trustScore();
            totalRivalry += rel.rivalryScore();
        }
        double avgTrust = totalTrust / relationships.size();
        double avgRivalry = totalRivalry / relationships.size();

        double cohesion = avgTrust - (avgRivalry * 0.5);
        return Math.max(0.0, Math.min(100.0, cohesion));
    }

    public void processSocialGraphTick() {
        double cohesion = computeNetworkCohesion();
        log.info("[SOCIAL GRAPH TICK] Active edges: {}, Network Cohesion: {}", relationships.size(), cohesion);
    }
}
