package io.github.opencivilizationplatform.config;

import io.github.opencivilizationplatform.modules.region.infrastructure.ResourceRegionRepository;
import io.github.opencivilizationplatform.modules.civilization.infrastructure.CivilizationRepository;
import io.github.opencivilizationplatform.modules.voxtex.infrastructure.VoxtexNodeRepository;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class CustomHealthIndicator implements HealthIndicator {

    private final CivilizationRepository civilizationRepository;
    private final ResourceRegionRepository regionRepository;
    private final VoxtexNodeRepository nodeRepository;

    public CustomHealthIndicator(CivilizationRepository civilizationRepository,
                                  ResourceRegionRepository regionRepository,
                                  VoxtexNodeRepository nodeRepository) {
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
                .withDetail("voxtexNodes", nodeCount)
                .withDetail("meshOnline", nodeCount > 0)
                .build();
        } catch (Exception e) {
            return Health.down()
                .withDetail("error", e.getMessage())
                .build();
        }
    }
}
