package io.github.opencivilizationplatform.modules.region.application;

import io.github.opencivilizationplatform.modules.region.domain.AgentPosition;
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
public class AgentSpatialMapService {

    private static final Logger log = LoggerFactory.getLogger(AgentSpatialMapService.class);
    private final Map<String, AgentPosition> positions = new ConcurrentHashMap<>();

    public void updatePosition(AgentPosition position) {
        if (position != null && position.agentId() != null) {
            positions.put(position.agentId(), position);
        }
    }

    public void updatePosition(String agentId, double x, double y, double speed) {
        if (agentId != null) {
            positions.put(agentId, new AgentPosition(agentId, x, y, speed));
        }
    }

    public Optional<AgentPosition> getPosition(String agentId) {
        if (agentId == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(positions.get(agentId));
    }

    public List<AgentPosition> getAllPositions() {
        return new ArrayList<>(positions.values());
    }

    public AgentPosition moveTowards(String agentId, double targetX, double targetY, double deltaTime) {
        AgentPosition current = positions.get(agentId);
        if (current == null) {
            return null;
        }

        double dx = targetX - current.x();
        double dy = targetY - current.y();
        double distance = Math.sqrt(dx * dx + dy * dy);

        if (distance == 0.0) {
            return current;
        }

        double maxStep = current.speed() * deltaTime;
        if (maxStep >= distance) {
            AgentPosition updated = new AgentPosition(agentId, targetX, targetY, current.speed());
            positions.put(agentId, updated);
            return updated;
        } else {
            double newX = current.x() + (dx / distance) * maxStep;
            double newY = current.y() + (dy / distance) * maxStep;
            AgentPosition updated = new AgentPosition(agentId, newX, newY, current.speed());
            positions.put(agentId, updated);
            return updated;
        }
    }

    public double calculateDistance(AgentPosition pos1, AgentPosition pos2) {
        if (pos1 == null || pos2 == null) {
            return Double.MAX_VALUE;
        }
        double dx = pos1.x() - pos2.x();
        double dy = pos1.y() - pos2.y();
        return Math.sqrt(dx * dx + dy * dy);
    }

    public List<AgentPosition> findAgentsInProximity(String agentId, double radius) {
        AgentPosition origin = positions.get(agentId);
        if (origin == null) {
            return Collections.emptyList();
        }
        return findAgentsInProximity(origin.x(), origin.y(), radius).stream()
                .filter(pos -> !pos.agentId().equals(agentId))
                .toList();
    }

    public List<AgentPosition> findAgentsInProximity(double x, double y, double radius) {
        if (radius < 0) {
            return Collections.emptyList();
        }
        List<AgentPosition> result = new ArrayList<>();
        for (AgentPosition pos : positions.values()) {
            double dx = pos.x() - x;
            double dy = pos.y() - y;
            double dist = Math.sqrt(dx * dx + dy * dy);
            if (dist <= radius) {
                result.add(pos);
            }
        }
        return result;
    }

    public void processSpatialTick() {
        log.info("[SPATIAL MAP TICK] Tracking {} agent positions.", positions.size());
    }
}
