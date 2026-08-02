package io.github.opencivilizationplatform.worker;

import io.github.opencivilizationplatform.modules.cortex.cortex.CortexEngineService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "civos.worker.remote-url", matchIfMissing = true)
public class CortexWorkerOrchestrator {

    private static final Logger log = LoggerFactory.getLogger(CortexWorkerOrchestrator.class);
    private final CortexEngineService cortexEngine;
    private final CortexWorkerClient workerClient;
    private final boolean remoteMode;

    public CortexWorkerOrchestrator(CortexEngineService cortexEngine,
                                     @org.springframework.beans.factory.annotation.Autowired(required = false)
                                     CortexWorkerClient workerClient) {
        this.cortexEngine = cortexEngine;
        this.workerClient = workerClient;
        this.remoteMode = workerClient != null;
    }

    @Scheduled(fixedRateString = "${cortex.engine.tick-rate-ms:30000}")
    public void orchestrateTick() {
        if (remoteMode && workerClient != null && workerClient.isHealthy()) {
            log.debug("Delegating cortex tick to remote worker");
            workerClient.triggerTick();
        } else {
            log.debug("Running cortex tick locally");
            cortexEngine.tick();
        }
    }
}
