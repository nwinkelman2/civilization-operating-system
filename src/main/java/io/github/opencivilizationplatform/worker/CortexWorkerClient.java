package io.github.opencivilizationplatform.worker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
@ConditionalOnProperty(name = "civos.worker.remote-url")
public class CortexWorkerClient {

    private static final Logger log = LoggerFactory.getLogger(CortexWorkerClient.class);
    private final RestTemplate rest;
    private final String workerUrl;

    public CortexWorkerClient(@org.springframework.beans.factory.annotation.Value("${civos.worker.remote-url}") String workerUrl) {
        this.rest = new RestTemplate();
        this.workerUrl = workerUrl;
    }

    public void triggerTick() {
        try {
            Map response = rest.postForObject(workerUrl + "/api/v1/worker/tick", null, Map.class);
            log.debug("Worker tick response: {}", response);
        } catch (Exception e) {
            log.warn("Failed to trigger remote worker tick: {}", e.getMessage());
        }
    }

    public void triggerTickForCiv(Long civilizationId) {
        try {
            rest.postForObject(workerUrl + "/api/v1/worker/tick/" + civilizationId, null, Map.class);
        } catch (Exception e) {
            log.warn("Failed to trigger remote worker tick for civ {}: {}", civilizationId, e.getMessage());
        }
    }

    public boolean isHealthy() {
        try {
            Map health = rest.getForObject(workerUrl + "/api/v1/worker/health", Map.class);
            return "UP".equals(health.get("status"));
        } catch (Exception e) {
            return false;
        }
    }
}
