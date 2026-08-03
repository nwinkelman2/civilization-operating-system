package io.github.opencivilizationplatform.saga;

import io.github.opencivilizationplatform.modules.civilization.application.CivilizationService;
import io.github.opencivilizationplatform.modules.civilization.domain.Civilization;
import io.github.opencivilizationplatform.modules.civilization.domain.CivilizationStatus;
import io.github.opencivilizationplatform.modules.region.application.ResourceRegionService;
import io.github.opencivilizationplatform.modules.nexus.application.NexusMeshService;
import io.github.opencivilizationplatform.modules.nexus.domain.NexusNodeType;
import io.github.opencivilizationplatform.modules.nexus.domain.NexusNodeStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class FoundCivilizationSagaSteps {

    private static final Logger log = LoggerFactory.getLogger(FoundCivilizationSagaSteps.class);
    private final CivilizationService civilizationService;
    private final ResourceRegionService regionService;
    private final NexusMeshService nexusService;

    public FoundCivilizationSagaSteps(CivilizationService civilizationService,
                                       ResourceRegionService regionService,
                                       NexusMeshService nexusService) {
        this.civilizationService = civilizationService;
        this.regionService = regionService;
        this.nexusService = nexusService;
    }

    public SagaStep<FoundCivilizationContext> createCivilization() {
        return new SagaStep<>() {
            @Override
            public void execute(FoundCivilizationContext ctx) {
                Civilization civ = civilizationService.createCivilization(
                    ctx.getName(), ctx.getScale(),
                    regionService.getRegion(ctx.getRegionId()).getName(),
                    ctx.getOwnerToken()
                );
                ctx.setCivilization(civ);
                log.info("SAGA step: civilization {} created (id={})", civ.getName(), civ.getId());
            }

            @Override
            public void compensate(FoundCivilizationContext ctx) {
                if (ctx.getCivilization() != null && ctx.getCivilization().getId() != null) {
                    civilizationService.updateStatus(ctx.getCivilization().getId(), CivilizationStatus.FALLEN);
                    log.warn("SAGA compensate: civilization {} marked as FALLEN", ctx.getCivilization().getId());
                }
            }

            @Override
            public String getName() { return "CreateCivilization"; }
        };
    }

    public SagaStep<FoundCivilizationContext> claimRegion() {
        return new SagaStep<>() {
            @Override
            public void execute(FoundCivilizationContext ctx) {
                regionService.claimRegion(ctx.getRegionId(), ctx.getCivilization().getId());
                ctx.setRegionClaimed(true);
                log.info("SAGA step: region {} claimed for civilization {}", ctx.getRegionId(), ctx.getCivilization().getId());
            }

            @Override
            public void compensate(FoundCivilizationContext ctx) {
                if (ctx.isRegionClaimed()) {
                    regionService.unclaimRegion(ctx.getRegionId());
                    log.warn("SAGA compensate: region {} unclaimed", ctx.getRegionId());
                }
            }

            @Override
            public String getName() { return "ClaimRegion"; }
        };
    }

    public SagaStep<FoundCivilizationContext> deployNexusNode() {
        return new SagaStep<>() {
            @Override
            public void execute(FoundCivilizationContext ctx) {
                String regionName = regionService.getRegion(ctx.getRegionId()).getName();
                nexusService.registerNode(
                    ctx.getCivilization().getName() + "-Primary",
                    NexusNodeType.PRIMARY,
                    regionName,
                    ctx.getCivilization().getId(),
                    "Primary neural node for " + ctx.getCivilization().getName()
                );
                ctx.setNexusNodeDeployed(true);
                log.info("SAGA step: nexus node deployed for civilization {}", ctx.getCivilization().getId());
            }

            @Override
            public void compensate(FoundCivilizationContext ctx) {
                if (ctx.isNexusNodeDeployed()) {
                    var nodes = nexusService.getNodesForCivilization(ctx.getCivilization().getId());
                    nodes.forEach(node -> {
                        nexusService.updateNodeStatus(node.getId(), NexusNodeStatus.OFFLINE);
                    });
                    log.warn("SAGA compensate: nexus nodes set to OFFLINE for civilization {}", ctx.getCivilization().getId());
                }
            }

            @Override
            public String getName() { return "DeployNexusNode"; }
        };
    }
}
