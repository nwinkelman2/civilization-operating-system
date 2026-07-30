package io.github.opencivilizationplatform.config;

import io.github.opencivilizationplatform.modules.cortex.cortex.CortexEngineService;
import io.github.opencivilizationplatform.modules.simulation.application.SimulationEngineService;
import io.github.opencivilizationplatform.web.handler.VoxtexWebSocketHandler;
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
    private final VoxtexWebSocketHandler webSocketHandler;

    public CustomHealthIndicator(DataSource dataSource,
                                  CortexEngineService cortexEngine,
                                  SimulationEngineService simulationEngine,
                                  VoxtexWebSocketHandler webSocketHandler) {
        this.dataSource = dataSource;
        this.cortexEngine = cortexEngine;
        this.simulationEngine = simulationEngine;
        this.webSocketHandler = webSocketHandler;
    }

    @Override
    public Health health() {
        try {
            Health.Builder builder = Health.up();

            checkDatabase(builder);
            checkCortexEngine(builder);
            checkSimulationEngine(builder);
            checkWebSocket(builder);

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
