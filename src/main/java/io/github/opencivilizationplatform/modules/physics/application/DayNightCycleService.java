package io.github.opencivilizationplatform.modules.physics.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class DayNightCycleService {

    private static final Logger log = LoggerFactory.getLogger(DayNightCycleService.class);

    private double currentHour = 12.0;

    public void advanceCycle(double hours) {
        if (hours < 0) {
            return;
        }
        currentHour = (currentHour + hours) % 24.0;
        if (currentHour < 0) {
            currentHour += 24.0;
        }
    }

    public void advanceCycle() {
        advanceCycle(1.0);
    }

    public double getCurrentHour() {
        return currentHour;
    }

    public void setCurrentHour(double hour) {
        double normalized = hour % 24.0;
        if (normalized < 0) {
            normalized += 24.0;
        }
        this.currentHour = normalized;
    }

    public boolean isDaytime() {
        return currentHour >= 6.0 && currentHour < 18.0;
    }

    public double calculateSolarIntensity() {
        if (!isDaytime()) {
            return 0.0;
        }
        double normalizedDayTime = (currentHour - 6.0) / 12.0;
        return Math.max(0.0, Math.sin(Math.PI * normalizedDayTime));
    }

    public String getDayNightActiveStatus() {
        return isDaytime() ? "DAY_ACTIVE" : "NIGHT_REST";
    }

    public void processDayNightTick() {
        advanceCycle(1.0);
        log.info("[DAY/NIGHT TICK] Current Hour: {}, Daytime: {}, Solar Intensity: {}, Active Status: {}",
                String.format("%.2f", currentHour), isDaytime(), String.format("%.2f", calculateSolarIntensity()), getDayNightActiveStatus());
    }
}
