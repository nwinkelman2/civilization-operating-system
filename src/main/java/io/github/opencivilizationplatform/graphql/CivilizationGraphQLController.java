package io.github.opencivilizationplatform.graphql;

import io.github.opencivilizationplatform.config.seed.CivilizationScale;
import io.github.opencivilizationplatform.modules.civilization.application.CivilizationService;
import io.github.opencivilizationplatform.modules.civilization.domain.Civilization;
import io.github.opencivilizationplatform.modules.contribution.application.ContributionService;
import io.github.opencivilizationplatform.modules.contribution.domain.Project;
import io.github.opencivilizationplatform.modules.contribution.domain.ProjectCategory;
import io.github.opencivilizationplatform.modules.events.application.GlobalEventService;
import io.github.opencivilizationplatform.modules.events.domain.EventSeverity;
import io.github.opencivilizationplatform.modules.events.domain.GlobalEvent;
import io.github.opencivilizationplatform.modules.logistics.application.ShipmentService;
import io.github.opencivilizationplatform.modules.logistics.domain.Shipment;
import io.github.opencivilizationplatform.modules.logistics.domain.ShipmentStatus;
import io.github.opencivilizationplatform.modules.nexus.application.ElectionService;
import io.github.opencivilizationplatform.modules.nexus.application.NexusMeshService;
import io.github.opencivilizationplatform.modules.nexus.application.TreatyService;
import io.github.opencivilizationplatform.modules.nexus.domain.*;
import io.github.opencivilizationplatform.modules.region.application.ResourceRegionService;
import io.github.opencivilizationplatform.modules.region.domain.ResourceRegion;
import io.github.opencivilizationplatform.modules.social.application.SocialStabilityService;
import io.github.opencivilizationplatform.modules.social.domain.Incident;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import io.github.opencivilizationplatform.dto.BalanceDTO;
import io.github.opencivilizationplatform.modules.leaderboard.application.LeaderboardService;
import io.github.opencivilizationplatform.modules.leaderboard.domain.CivilizationScore;
import io.github.opencivilizationplatform.modules.resources.application.ResourceService;
import io.github.opencivilizationplatform.modules.resources.domain.Resource;
import io.github.opencivilizationplatform.modules.simulation.api.dto.SimulationStatusResponse;
import io.github.opencivilizationplatform.modules.simulation.application.SimulationEngineService;
import io.github.opencivilizationplatform.modules.strategy.application.BalanceService;
import io.github.opencivilizationplatform.modules.strategy.application.EmergentCivilizationSearchService;
import io.github.opencivilizationplatform.modules.strategy.domain.EmergentArchetypeReport;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
public class CivilizationGraphQLController {

    private final CivilizationService civilizationService;
    private final ResourceRegionService regionService;
    private final ResourceService resourceService;
    private final NexusMeshService nexusService;
    private final ShipmentService shipmentService;
    private final GlobalEventService globalEventService;
    private final ContributionService contributionService;
    private final SocialStabilityService socialStabilityService;
    private final ElectionService electionService;
    private final TreatyService treatyService;
    private final LeaderboardService leaderboardService;
    private final SimulationEngineService simulationEngineService;
    private final BalanceService balanceService;
    private final EmergentCivilizationSearchService emergentCivilizationSearchService;

    public CivilizationGraphQLController(CivilizationService civilizationService,
                                          ResourceRegionService regionService,
                                          ResourceService resourceService,
                                          NexusMeshService nexusService,
                                          ShipmentService shipmentService,
                                          GlobalEventService globalEventService,
                                          ContributionService contributionService,
                                          SocialStabilityService socialStabilityService,
                                          ElectionService electionService,
                                          TreatyService treatyService,
                                          LeaderboardService leaderboardService,
                                          SimulationEngineService simulationEngineService,
                                          BalanceService balanceService,
                                          EmergentCivilizationSearchService emergentCivilizationSearchService) {
        this.civilizationService = civilizationService;
        this.regionService = regionService;
        this.resourceService = resourceService;
        this.nexusService = nexusService;
        this.shipmentService = shipmentService;
        this.globalEventService = globalEventService;
        this.contributionService = contributionService;
        this.socialStabilityService = socialStabilityService;
        this.electionService = electionService;
        this.treatyService = treatyService;
        this.leaderboardService = leaderboardService;
        this.simulationEngineService = simulationEngineService;
        this.balanceService = balanceService;
        this.emergentCivilizationSearchService = emergentCivilizationSearchService;
    }

    @QueryMapping
    public List<EmergentArchetypeReport> emergentArchetypes() {
        return emergentCivilizationSearchService.evaluateEmergentArchetypes();
    }

    @QueryMapping
    public List<ResourceRegion> regions(@Argument Boolean claimed) {
        if (Boolean.TRUE.equals(claimed)) {
            return regionService.getAllRegions().stream().filter(r -> Boolean.TRUE.equals(r.getClaimed())).toList();
        } else if (Boolean.FALSE.equals(claimed)) {
            return regionService.getAvailableRegions();
        }
        return regionService.getAllRegions();
    }

    @QueryMapping
    public List<Resource> resources(@Argument String region) {
        return resourceService.getAllResources(PageRequest.of(0, 100)).getContent();
    }

    @QueryMapping
    public List<CivilizationScore> leaderboard() {
        return leaderboardService.getLeaderboard();
    }

    @QueryMapping
    public SimulationStatusResponse simulationStatus() {
        return simulationEngineService.getStatus();
    }

    @QueryMapping
    public List<BalanceDTO> balanceReport() {
        return balanceService.getBalanceReport();
    }

    @QueryMapping
    public Map<String, Object> civilizations(@Argument int page, @Argument int size) {
        Page<Civilization> p = civilizationService.getAllCivilizations(
            PageRequest.of(page > 0 ? page : 0, size > 0 ? size : 20));
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("content", p.getContent());
        result.put("totalPages", p.getTotalPages());
        result.put("totalElements", p.getTotalElements());
        result.put("size", p.getSize());
        result.put("number", p.getNumber());
        return result;
    }

    @QueryMapping
    public Civilization civilization(@Argument Long id) {
        return civilizationService.getCivilization(id);
    }

    @QueryMapping
    public List<NexusNode> nexusNodes(@Argument Long civilizationId) {
        if (civilizationId != null) {
            return nexusService.getNodesForCivilization(civilizationId);
        }
        return nexusService.getAllNodes();
    }

    @QueryMapping
    public List<NexusMessage> nexusMessages(@Argument Long sourceNodeId, @Argument Long targetNodeId) {
        if (sourceNodeId != null && targetNodeId != null) {
            return nexusService.getConversation(sourceNodeId, targetNodeId);
        } else if (targetNodeId != null) {
            return nexusService.getPendingMessages(targetNodeId);
        }
        return List.of();
    }

    @QueryMapping
    public List<Shipment> shipments(@Argument Long civilizationId, @Argument ShipmentStatus status) {
        List<Shipment> list = shipmentService.getAllShipments(PageRequest.of(0, 100)).getContent();
        if (status != null) {
            list = list.stream().filter(s -> s.getStatus() == status).toList();
        }
        return list;
    }

    @QueryMapping
    public List<GlobalEvent> globalEvents(@Argument Boolean activeOnly) {
        if (Boolean.TRUE.equals(activeOnly)) {
            return globalEventService.getActiveEvents();
        }
        return globalEventService.getActiveEvents();
    }

    @QueryMapping
    public List<Project> projects(@Argument Long civilizationId, @Argument ProjectCategory category) {
        List<Project> list = civilizationId != null
            ? contributionService.getProjectsForCivilization(civilizationId)
            : contributionService.getActiveProjects();
        if (category != null) {
            list = list.stream().filter(p -> p.getCategory() == category).toList();
        }
        return list;
    }

    @QueryMapping
    public List<Election> elections(@Argument Long civilizationId, @Argument ElectionStatus status) {
        List<Election> list = civilizationId != null
            ? electionService.getElectionsForCiv(civilizationId)
            : List.of();
        if (status != null) {
            list = list.stream().filter(e -> e.getStatus() == status).toList();
        }
        return list;
    }

    @QueryMapping
    public List<Incident> incidents(@Argument Long civilizationId) {
        if (civilizationId != null) {
            return socialStabilityService.getIncidentsForCivilization(civilizationId);
        }
        return socialStabilityService.getAllIncidents(PageRequest.of(0, 100)).getContent();
    }

    @QueryMapping
    public List<Treaty> treaties(@Argument Long civilizationId) {
        if (civilizationId != null) {
            return treatyService.getTreatiesForCiv(civilizationId);
        }
        return treatyService.getActiveTreaties();
    }

    @MutationMapping
    public Civilization createCivilization(@Argument String name, @Argument CivilizationScale scale, @Argument String region) {
        return civilizationService.createCivilization(name, scale != null ? scale : CivilizationScale.LOCAL,
            region != null ? region : "unknown", "graphql-user");
    }

    @MutationMapping
    public Civilization foundCivilization(@Argument String name, @Argument CivilizationScale scale,
                                           @Argument Long regionId) {
        ResourceRegion reg = regionService.getRegion(regionId);
        Civilization civ = civilizationService.createCivilization(name,
            scale != null ? scale : CivilizationScale.LOCAL, reg.getName(), "graphql-user");
        regionService.claimRegion(regionId, civ.getId());
        nexusService.registerNode(civ.getName() + "-Primary",
            NexusNodeType.PRIMARY,
            reg.getName(), civ.getId(), "Primary node for " + civ.getName());
        return civ;
    }

    @MutationMapping
    public NexusMessage sendNexusMessage(@Argument Long sourceNodeId, @Argument Long targetNodeId,
                                          @Argument NexusMessageType messageType, @Argument String content) {
        return nexusService.sendMessage(sourceNodeId, targetNodeId, messageType, content);
    }

    @MutationMapping
    public NexusNode registerNexusNode(@Argument String name, @Argument NexusNodeType type,
                                        @Argument String region, @Argument Long civilizationId,
                                        @Argument String knowledgeBase) {
        return nexusService.registerNode(name, type, region, civilizationId, knowledgeBase);
    }

    @SchemaMapping(typeName = "Civilization", field = "resources")
    public Map<String, Object> resources(Civilization civ) {
        Map<String, Object> r = new LinkedHashMap<>();
        r.put("food", civ.getFood() != null ? civ.getFood() : 0.0);
        r.put("water", civ.getWater() != null ? civ.getWater() : 0.0);
        r.put("minerals", civ.getMinerals() != null ? civ.getMinerals() : 0.0);
        r.put("energy", civ.getEnergy() != null ? civ.getEnergy() : 0.0);
        r.put("housing", civ.getHousing() != null ? civ.getHousing() : 0.0);
        return r;
    }

    @SchemaMapping(typeName = "Civilization", field = "nexusNodes")
    public List<NexusNode> nexusNodesForCivilization(Civilization civ) {
        return nexusService.getNodesForCivilization(civ.getId());
    }

    @SchemaMapping(typeName = "Civilization", field = "homeRegion")
    public ResourceRegion homeRegion(Civilization civ) {
        if (civ.getHomeRegionId() != null) {
            return regionService.getRegion(civ.getHomeRegionId());
        }
        return null;
    }

    @SchemaMapping(typeName = "NexusNode", field = "civilizationId")
    public Long nexusNodeCivilizationId(NexusNode node) {
        return node.getCivilization() != null ? node.getCivilization().getId() : null;
    }

    @SchemaMapping(typeName = "NexusMessage", field = "sourceNodeId")
    public Long nexusMessageSourceNodeId(NexusMessage msg) {
        return msg.getSourceNode() != null ? msg.getSourceNode().getId() : null;
    }

    @SchemaMapping(typeName = "NexusMessage", field = "targetNodeId")
    public Long nexusMessageTargetNodeId(NexusMessage msg) {
        return msg.getTargetNode() != null ? msg.getTargetNode().getId() : null;
    }

    @SchemaMapping(typeName = "Shipment", field = "originRegion")
    public String shipmentOriginRegion(Shipment s) { return s.getOrigin(); }

    @SchemaMapping(typeName = "Shipment", field = "destinationRegion")
    public String shipmentDestinationRegion(Shipment s) { return s.getDestination(); }

    @SchemaMapping(typeName = "Shipment", field = "resourceType")
    public String shipmentResourceType(Shipment s) { return s.getCargo(); }

    @SchemaMapping(typeName = "Shipment", field = "civilizationId")
    public Long shipmentCivilizationId(Shipment s) { return 1L; }

    @SchemaMapping(typeName = "Shipment", field = "createdAt")
    public String shipmentCreatedAt(Shipment s) {
        return s.getEta() != null ? s.getEta().toString() : LocalDateTime.now().toString();
    }

    @SchemaMapping(typeName = "GlobalEvent", field = "title")
    public String globalEventTitle(GlobalEvent e) {
        return e.getType() != null ? e.getType().name() : "Global Event";
    }

    @SchemaMapping(typeName = "GlobalEvent", field = "severity")
    public EventSeverity globalEventSeverity(GlobalEvent e) {
        return EventSeverity.MODERATE;
    }

    @SchemaMapping(typeName = "Project", field = "name")
    public String projectName(Project p) { return p.getTitle(); }

    @SchemaMapping(typeName = "Project", field = "civilizationId")
    public Long projectCivilizationId(Project p) {
        return p.getCivilization() != null ? p.getCivilization().getId() : null;
    }

    @SchemaMapping(typeName = "Project", field = "targetContribution")
    public Double projectTargetContribution(Project p) { return 100.0; }

    @SchemaMapping(typeName = "Project", field = "currentContribution")
    public Double projectCurrentContribution(Project p) { return 50.0; }

    @SchemaMapping(typeName = "Election", field = "title")
    public String electionTitle(Election e) { return "Election #" + e.getId(); }

    @SchemaMapping(typeName = "Treaty", field = "name")
    public String treatyName(Treaty t) { return t.getTitle(); }

    @SchemaMapping(typeName = "Treaty", field = "createdAt")
    public String treatyCreatedAt(Treaty t) {
        return t.getProposedAt() != null ? t.getProposedAt().toString() : LocalDateTime.now().toString();
    }

    @SchemaMapping(typeName = "Treaty", field = "signatoryCivilizationIds")
    public List<String> treatySignatoryCivilizationIds(Treaty t) {
        if (t.getSignatoryCivIds() == null || t.getSignatoryCivIds().isBlank()) return List.of();
        String inner = t.getSignatoryCivIds().replace("[", "").replace("]", "").trim();
        if (inner.isEmpty()) return List.of();
        return Arrays.stream(inner.split(","))
            .map(String::trim)
            .filter(s -> !s.isEmpty())
            .toList();
    }

    @SchemaMapping(typeName = "Incident", field = "title")
    public String incidentTitle(Incident i) {
        return i.getType() != null ? i.getType().name() + " Incident" : "Incident";
    }

    @SchemaMapping(typeName = "Incident", field = "civilizationId")
    public Long incidentCivilizationId(Incident i) {
        return i.getCivilization() != null ? i.getCivilization().getId() : null;
    }

    @SchemaMapping(typeName = "Incident", field = "createdAt")
    public String incidentCreatedAt(Incident i) {
        return i.getReportedAt() != null ? i.getReportedAt().toString() : LocalDateTime.now().toString();
    }
}
