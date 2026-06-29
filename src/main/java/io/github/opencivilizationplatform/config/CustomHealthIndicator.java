package io.github.opencivilizationplatform.config;

import io.github.opencivilizationplatform.modules.region.infrastructure.ResourceRegionRepository;
import io.github.opencivilizationplatform.modules.civilization.infrastructure.CivilizationRepository;
import io.github.opencivilizationplatform.modules.nexus.infrastructure.NexusNodeRepository;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class CustomHealthIndicator implements HealthIndicator {

    private final CivilizationRepository civilizationRepository;
    private final ResourceRegionRepository regionRepository;
    private final NexusNodeRepository nodeRepository;

    public CustomHealthIndicator(CivilizationRepository civilizationRepository,
                                  ResourceRegionRepository regionRepository,
                                  NexusNodeRepository nodeRepository) {
        this.civilizationRepository = civilizationRepository;
        this.regionRepository = regionRepository;
        this.nodeRepository = nodeRepository;
    }

    @Override
    public Health health() {
        try {
            long civCount = civilizationRepository.count();
            long regionCount = regionRepository.count();
            long nodeCount = nodeRepository.count();

            return Health.up()
                .withDetail("civilizations", civCount)
                .withDetail("resourceRegions", regionCount)
                .withDetail("NexusNodes", nodeCount)
                .withDetail("meshOnline", nodeCount > 0)
                .build();
        } catch (Exception e) {
            return Health.down()
                .withDetail("error", e.getMessage())
                .build();
        }
    }
}

