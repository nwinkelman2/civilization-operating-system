package io.github.opencivilizationplatform.saga;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class SagaOrchestrator {

    private static final Logger log = LoggerFactory.getLogger(SagaOrchestrator.class);

    public <T> void execute(String sagaName, T context, List<SagaStep<T>> steps) {
        List<SagaStep<T>> executed = new ArrayList<>();
        log.info("SAGA [{}] starting with {} steps", sagaName, steps.size());

        for (SagaStep<T> step : steps) {
            try {
                log.debug("SAGA [{}] executing step: {}", sagaName, step.getName());
                step.execute(context);
                executed.add(step);
            } catch (Exception e) {
                log.error("SAGA [{}] step {} failed: {}", sagaName, step.getName(), e.getMessage());
                compensate(sagaName, context, executed, e);
                throw new SagaException("Saga " + sagaName + " failed at step " + step.getName(), e);
            }
        }

        log.info("SAGA [{}] completed successfully", sagaName);
    }

    private <T> void compensate(String sagaName, T context, List<SagaStep<T>> executed, Exception cause) {
        log.warn("SAGA [{}] compensating {} steps due to: {}", sagaName, executed.size(), cause.getMessage());
        for (int i = executed.size() - 1; i >= 0; i--) {
            SagaStep<T> step = executed.get(i);
            try {
                log.debug("SAGA [{}] compensating step: {}", sagaName, step.getName());
                step.compensate(context);
            } catch (Exception e) {
                log.error("SAGA [{}] compensation failed for step {}: {}", sagaName, step.getName(), e.getMessage());
            }
        }
    }
}
