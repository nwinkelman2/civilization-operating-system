package io.github.opencivilizationplatform.config;

import io.github.opencivilizationplatform.modules.cortex.cortex.CortexEngineService;
import io.github.opencivilizationplatform.modules.simulation.application.SimulationEngineService;
import io.github.opencivilizationplatform.web.handler.NexusWebSocketHandler;
import io.github.opencivilizationplatform.modules.region.infrastructure.ResourceRegionRepository;
import io.github.opencivilizationplatform.modules.civilization.infrastructure.CivilizationRepository;
import io.github.opencivilizationplatform.modules.nexus.infrastructure.NexusNodeRepository;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.time.Duration;
import java.time.LocalDateTime;

@Component
public class CustomHealthIndicator implements HealthIndicator {

    private final DataSource dataSource;
    private final CortexEngineService cortexEngine;
    private final SimulationEngineService simulationEngine;
    private final NexusWebSocketHandler webSocketHandler;
    private final CivilizationRepository civilizationRepository;
    private final ResourceRegionRepository regionRepository;
    private final NexusNodeRepository nodeRepository;

    public CustomHealthIndicator(DataSource dataSource,
                                  CortexEngineService cortexEngine,
                                  SimulationEngineService simulationEngine,
                                  NexusWebSocketHandler webSocketHandler,
                                  CivilizationRepository civilizationRepository,
                                  ResourceRegionRepository regionRepository,
                                  NexusNodeRepository nodeRepository) {
        this.dataSource = dataSource;
        this.cortexEngine = cortexEngine;
        this.simulationEngine = simulationEngine;
        this.webSocketHandler = webSocketHandler;
        this.civilizationRepository = civilizationRepository;
        this.regionRepository = regionRepository;
        this.nodeRepository = nodeRepository;
    }

    @Override
    public Health health() {
        try {
            Health.Builder builder = Health.up();

            checkDatabase(builder);
            checkCortexEngine(builder);
            checkSimulationEngine(builder);
            checkWebSocket(builder);

            long civCount = civilizationRepository.count();
            long regionCount = regionRepository.count();
            long nodeCount = nodeRepository.count();
            builder.withDetail("civilizations", civCount)
                   .withDetail("resourceRegions", regionCount)
                   .withDetail("nexusNodes", nodeCount)
                   .withDetail("meshOnline", nodeCount > 0);

            return builder.build();
        } catch (Exception e) {
            return Health.down()
                .withDetail("error", e.getMessage())
                .build();
        }
    }

    private void checkDatabase(Health.Builder builder) {
        try (Connection conn = dataSource.getConnection()) {
            builder.withDetail("database", conn.getMetaData().getDatabaseProductName())
                   .withDetail("databaseStatus", "UP");
        } catch (Exception e) {
            builder.withDetail("databaseStatus", "DOWN")
                   .withDetail("databaseError", e.getMessage());
        }
    }

    private void checkCortexEngine(Health.Builder builder) {
        LocalDateTime lastTick = cortexEngine.getLastTickTime();
        Duration sinceLastTick = Duration.between(lastTick, LocalDateTime.now());
        boolean healthy = sinceLastTick.toSeconds() < 120;
        builder.withDetail("cortexEngine", healthy ? "UP" : "DEGRADED")
               .withDetail("cortexLastTick", lastTick.toString())
               .withDetail("cortexSecondsSinceLastTick", sinceLastTick.toSeconds());
    }

    private void checkSimulationEngine(Health.Builder builder) {
        LocalDateTime lastTick = simulationEngine.getLastTickTime();
        Duration sinceLastTick = Duration.between(lastTick, LocalDateTime.now());
        boolean healthy = sinceLastTick.toSeconds() < 120;
        builder.withDetail("simulationEngine", healthy ? "UP" : "DEGRADED")
               .withDetail("simulationLastTick", lastTick.toString())
               .withDetail("simulationSecondsSinceLastTick", sinceLastTick.toSeconds());
    }

    private void checkWebSocket(Health.Builder builder) {
        int activeSessions = webSocketHandler.getActiveSessionCount();
        builder.withDetail("webSocketSessions", activeSessions)
               .withDetail("webSocket", "UP");
    }
}
