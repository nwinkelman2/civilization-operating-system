package io.github.opencivilizationplatform.modules.region.application;

import io.github.opencivilizationplatform.modules.region.domain.ExplorationExpedition;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ExplorationColonyService {

    private final Map<String, ExplorationExpedition> expeditions = new ConcurrentHashMap<>();

    public record ColonyEstablishmentResult(
        String expeditionId,
        Long civilizationId,
        String targetRegion,
        double progress,
        boolean colonyEstablished
    ) {}

    public ExplorationExpedition launchExpedition(String expeditionId, Long civilizationId, String targetRegion) {
        if (expeditionId == null || civilizationId == null || targetRegion == null) {
            throw new IllegalArgumentException("Expedition ID, Civilization ID, and target region must not be null.");
        }

        ExplorationExpedition expedition = new ExplorationExpedition(expeditionId, civilizationId, targetRegion, 0.0);
        expeditions.put(expeditionId, expedition);
        return expedition;
    }

    public ColonyEstablishmentResult advanceExploration(String expeditionId, double progressDelta) {
        ExplorationExpedition current = expeditions.get(expeditionId);
        if (current == null) {
            return new ColonyEstablishmentResult(expeditionId, null, null, 0.0, false);
        }

        double newProgress = Math.min(100.0, Math.max(0.0, current.progress() + progressDelta));
        boolean established = newProgress >= 100.0;

        ExplorationExpedition updated = new ExplorationExpedition(
            current.expeditionId(),
            current.civilizationId(),
            current.targetRegion(),
            newProgress
        );

        expeditions.put(expeditionId, updated);

        return new ColonyEstablishmentResult(
            updated.expeditionId(),
            updated.civilizationId(),
            updated.targetRegion(),
            updated.progress(),
            established
        );
    }

    public List<ColonyEstablishmentResult> processExplorationTick(double progressStep) {
        List<ColonyEstablishmentResult> results = new ArrayList<>();
        for (String id : expeditions.keySet()) {
            results.add(advanceExploration(id, progressStep));
        }
        return results;
    }

    public List<ExplorationExpedition> getActiveExpeditions() {
        return new ArrayList<>(expeditions.values());
    }

    public ExplorationExpedition getExpedition(String expeditionId) {
        return expeditions.get(expeditionId);
    }
}
