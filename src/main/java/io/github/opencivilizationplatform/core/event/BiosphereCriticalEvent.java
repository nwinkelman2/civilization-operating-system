package io.github.opencivilizationplatform.core.event;

import io.github.opencivilizationplatform.modules.monitoring.domain.BiosphereMetric;

public class BiosphereCriticalEvent extends CivilizationEvent {
    private final BiosphereMetric metric;

    public BiosphereCriticalEvent(Object source, BiosphereMetric metric) {
        super(source, "MONITORING");
        this.metric = metric;
    }

    public BiosphereMetric getMetric() {
        return metric;
    }
}
