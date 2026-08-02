package io.github.opencivilizationplatform.graphql;

import io.github.opencivilizationplatform.config.seed.CivilizationScale;
import io.github.opencivilizationplatform.modules.civilization.application.CivilizationService;
import io.github.opencivilizationplatform.modules.civilization.domain.Civilization;
import io.github.opencivilizationplatform.modules.contribution.application.ContributionService;
import io.github.opencivilizationplatform.modules.contribution.domain.Project;
import io.github.opencivilizationplatform.modules.contribution.domain.ProjectCategory;
import io.github.opencivilizationplatform.modules.events.application.GlobalEventService;
import io.github.opencivilizationplatform.modules.events.domain.GlobalEvent;
import io.github.opencivilizationplatform.modules.events.domain.GlobalEventType;
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
import io.github.opencivilizationplatform.modules.social.domain.IncidentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CivilizationGraphQLControllerTest {

    @Mock
    private CivilizationService civilizationService;
    @Mock
    private ResourceRegionService regionService;
    @Mock
    private NexusMeshService nexusService;
    @Mock
    private ShipmentService shipmentService;
    @Mock
    private GlobalEventService globalEventService;
    @Mock
    private ContributionService contributionService;
    @Mock
    private SocialStabilityService socialStabilityService;
    @Mock
    private ElectionService electionService;
    @Mock
    private TreatyService treatyService;

    private CivilizationGraphQLController controller;

    @BeforeEach
    void setUp() {
        controller = new CivilizationGraphQLController(
            civilizationService,
            regionService,
            nexusService,
            shipmentService,
            globalEventService,
            contributionService,
            socialStabilityService,
            electionService,
            treatyService
        );
    }

    @Test
    void testCivilizationsQuery() {
        Civilization civ = new Civilization();
        civ.setId(1L);
        civ.setName("Test Civ");

        when(civilizationService.getAllCivilizations(any(Pageable.class)))
            .thenReturn(new PageImpl<>(List.of(civ)));

        Map<String, Object> result = controller.civilizations(0, 10);
        assertNotNull(result);
        assertTrue(result.containsKey("content"));
        assertEquals(1, ((List<?>) result.get("content")).size());
    }

    @Test
    void testCivilizationQuery() {
        Civilization civ = new Civilization();
        civ.setId(1L);
        civ.setName("Test Civ");

        when(civilizationService.getCivilization(1L)).thenReturn(civ);

        Civilization result = controller.civilization(1L);
        assertNotNull(result);
        assertEquals("Test Civ", result.getName());
    }

    @Test
    void testNexusNodesQuery() {
        NexusNode node = new NexusNode();
        node.setId(10L);
        node.setName("Nexus Alpha");

        when(nexusService.getNodesForCivilization(1L)).thenReturn(List.of(node));
        when(nexusService.getAllNodes()).thenReturn(List.of(node));

        List<NexusNode> civNodes = controller.nexusNodes(1L);
        assertEquals(1, civNodes.size());

        List<NexusNode> allNodes = controller.nexusNodes((Long) null);
        assertEquals(1, allNodes.size());
    }

    @Test
    void testNexusMessagesQuery() {
        NexusMessage msg = new NexusMessage();
        msg.setId(100L);
        msg.setContent("Hello Mesh");

        when(nexusService.getConversation(1L, 2L)).thenReturn(List.of(msg));
        when(nexusService.getPendingMessages(2L)).thenReturn(List.of(msg));

        List<NexusMessage> conv = controller.nexusMessages(1L, 2L);
        assertEquals(1, conv.size());

        List<NexusMessage> pending = controller.nexusMessages(null, 2L);
        assertEquals(1, pending.size());

        List<NexusMessage> empty = controller.nexusMessages(null, null);
        assertTrue(empty.isEmpty());
    }

    @Test
    void testShipmentsQuery() {
        Shipment shipment = new Shipment();
        shipment.setId(5L);
        shipment.setStatus(ShipmentStatus.IN_TRANSIT);

        when(shipmentService.getAllShipments(any(Pageable.class)))
            .thenReturn(new PageImpl<>(List.of(shipment)));

        List<Shipment> result = controller.shipments(1L, ShipmentStatus.IN_TRANSIT);
        assertEquals(1, result.size());

        List<Shipment> filteredOut = controller.shipments(1L, ShipmentStatus.DELIVERED);
        assertTrue(filteredOut.isEmpty());
    }

    @Test
    void testGlobalEventsQuery() {
        GlobalEvent event = new GlobalEvent();
        event.setId(1L);
        event.setType(GlobalEventType.SOLAR_STORM);

        when(globalEventService.getActiveEvents()).thenReturn(List.of(event));

        List<GlobalEvent> active = controller.globalEvents(true);
        assertEquals(1, active.size());
    }

    @Test
    void testProjectsQuery() {
        Project proj = new Project();
        proj.setId(1L);
        proj.setTitle("Quantum Array");
        proj.setCategory(ProjectCategory.AGRICULTURE);

        when(contributionService.getProjectsForCivilization(1L)).thenReturn(List.of(proj));
        when(contributionService.getActiveProjects()).thenReturn(List.of(proj));

        List<Project> civProjects = controller.projects(1L, ProjectCategory.AGRICULTURE);
        assertEquals(1, civProjects.size());

        List<Project> allProjects = controller.projects(null, null);
        assertEquals(1, allProjects.size());
    }

    @Test
    void testElectionsQuery() {
        Election election = new Election();
        election.setId(1L);
        election.setCivilizationId(1L);
        election.setStatus(ElectionStatus.OPEN);

        when(electionService.getElectionsForCiv(1L)).thenReturn(List.of(election));

        List<Election> result = controller.elections(1L, ElectionStatus.OPEN);
        assertEquals(1, result.size());
    }

    @Test
    void testIncidentsQuery() {
        Incident incident = new Incident();
        incident.setId(1L);
        incident.setType(IncidentType.CONFLICT);

        when(socialStabilityService.getIncidentsForCivilization(1L)).thenReturn(List.of(incident));
        when(socialStabilityService.getAllIncidents(any(Pageable.class)))
            .thenReturn(new PageImpl<>(List.of(incident)));

        List<Incident> civIncidents = controller.incidents(1L);
        assertEquals(1, civIncidents.size());

        List<Incident> allIncidents = controller.incidents(null);
        assertEquals(1, allIncidents.size());
    }

    @Test
    void testTreatiesQuery() {
        Treaty treaty = new Treaty();
        treaty.setId(1L);
        treaty.setTitle("Non-Aggression Pact");

        when(treatyService.getTreatiesForCiv(1L)).thenReturn(List.of(treaty));
        when(treatyService.getActiveTreaties()).thenReturn(List.of(treaty));

        List<Treaty> civTreaties = controller.treaties(1L);
        assertEquals(1, civTreaties.size());

        List<Treaty> activeTreaties = controller.treaties(null);
        assertEquals(1, activeTreaties.size());
    }

    @Test
    void testMutations() {
        Civilization civ = new Civilization();
        civ.setId(1L);
        civ.setName("Solaria");

        ResourceRegion region = new ResourceRegion();
        region.setId(10L);
        region.setName("Alpha Sector");

        NexusNode node = new NexusNode();
        node.setId(20L);
        node.setName("Solaria-Primary");

        NexusMessage msg = new NexusMessage();
        msg.setId(30L);
        msg.setContent("Test Message");

        when(civilizationService.createCivilization(eq("Solaria"), any(), any(), any())).thenReturn(civ);
        when(regionService.getRegion(10L)).thenReturn(region);
        when(nexusService.registerNode(any(), any(), any(), any(), any())).thenReturn(node);
        when(nexusService.sendMessage(eq(1L), eq(2L), eq(NexusMessageType.INNOVATION_SHARE), eq("Hello"))).thenReturn(msg);

        Civilization created = controller.createCivilization("Solaria", CivilizationScale.LOCAL, "Alpha Sector");
        assertNotNull(created);

        Civilization founded = controller.foundCivilization("Solaria", CivilizationScale.LOCAL, 10L);
        assertNotNull(founded);

        NexusNode registeredNode = controller.registerNexusNode("Node1", NexusNodeType.PRIMARY, "RegionA", 1L, "KB");
        assertNotNull(registeredNode);

        NexusMessage sentMsg = controller.sendNexusMessage(1L, 2L, NexusMessageType.INNOVATION_SHARE, "Hello");
        assertNotNull(sentMsg);
    }

    @Test
    void testSchemaMappings() {
        ResourceRegion region = new ResourceRegion();
        region.setId(10L);
        region.setName("Home Sector");

        Civilization civ = new Civilization();
        civ.setId(1L);
        civ.setFood(100.0);
        civ.setHomeRegion(region);

        NexusNode node = new NexusNode();
        node.setId(20L);
        node.setCivilization(civ);

        NexusMessage msg = new NexusMessage();
        msg.setSourceNode(node);
        msg.setTargetNode(node);

        when(regionService.getRegion(10L)).thenReturn(region);
        when(nexusService.getNodesForCivilization(1L)).thenReturn(List.of(node));

        Map<String, Object> res = controller.resources(civ);
        assertEquals(100.0, res.get("food"));

        List<NexusNode> nodes = controller.nexusNodesForCivilization(civ);
        assertEquals(1, nodes.size());

        ResourceRegion home = controller.homeRegion(civ);
        assertEquals("Home Sector", home.getName());

        assertEquals(1L, controller.nexusNodeCivilizationId(node));
        assertEquals(20L, controller.nexusMessageSourceNodeId(msg));
        assertEquals(20L, controller.nexusMessageTargetNodeId(msg));
    }
}
