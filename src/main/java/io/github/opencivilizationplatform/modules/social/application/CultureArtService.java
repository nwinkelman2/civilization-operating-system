package io.github.opencivilizationplatform.modules.social.application;

import io.github.opencivilizationplatform.modules.social.domain.CulturalArtifact;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CultureArtService {

    private static final Logger log = LoggerFactory.getLogger(CultureArtService.class);
    private final Map<String, CulturalArtifact> artifacts = new ConcurrentHashMap<>();

    public CulturalArtifact createArtifact(String artifactId, String creatorAgentId, String title, String era, double prestigeValue) {
        if (artifactId == null || title == null) {
            return null;
        }
        CulturalArtifact artifact = new CulturalArtifact(artifactId, creatorAgentId, title, era, prestigeValue);
        artifacts.put(artifactId, artifact);
        return artifact;
    }

    public Optional<CulturalArtifact> getArtifact(String artifactId) {
        if (artifactId == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(artifacts.get(artifactId));
    }

    public List<CulturalArtifact> getAllArtifacts() {
        return new ArrayList<>(artifacts.values());
    }

    public List<CulturalArtifact> getArtifactsByCreator(String creatorAgentId) {
        if (creatorAgentId == null) {
            return Collections.emptyList();
        }
        return artifacts.values().stream()
                .filter(a -> creatorAgentId.equals(a.creatorAgentId()))
                .toList();
    }

    public List<CulturalArtifact> getArtifactsByEra(String era) {
        if (era == null) {
            return Collections.emptyList();
        }
        return artifacts.values().stream()
                .filter(a -> era.equalsIgnoreCase(a.era()))
                .toList();
    }

    public double evaluateTotalPrestige() {
        return artifacts.values().stream()
                .mapToDouble(CulturalArtifact::prestigeValue)
                .sum();
    }

    public double evaluatePrestige(String artifactId, double eraMultiplier) {
        CulturalArtifact artifact = artifacts.get(artifactId);
        if (artifact == null) {
            return 0.0;
        }
        return artifact.prestigeValue() * Math.max(0.0, eraMultiplier);
    }

    public Map<String, Double> evaluateCulturalMovementTrends() {
        Map<String, Double> trends = new HashMap<>();
        for (CulturalArtifact artifact : artifacts.values()) {
            String eraKey = artifact.era() != null ? artifact.era() : "UNKNOWN";
            trends.merge(eraKey, artifact.prestigeValue(), Double::sum);
        }
        return trends;
    }

    public void processCultureTick() {
        double totalPrestige = evaluateTotalPrestige();
        log.info("[CULTURE ENGINE TICK] Total Artifacts: {}, Total Prestige: {}", artifacts.size(), totalPrestige);
    }
}
