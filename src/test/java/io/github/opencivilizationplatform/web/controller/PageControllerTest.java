package io.github.opencivilizationplatform.web.controller;

import io.github.opencivilizationplatform.modules.contribution.application.ContributionService;
import io.github.opencivilizationplatform.modules.execution.application.AutomationUnitService;
import io.github.opencivilizationplatform.modules.logistics.application.ShipmentService;
import io.github.opencivilizationplatform.modules.monitoring.application.BiosphereMetricService;
import io.github.opencivilizationplatform.modules.needs.application.NeedService;
import io.github.opencivilizationplatform.modules.participation.application.InteractionService;
import io.github.opencivilizationplatform.modules.participation.application.RuleService;
import io.github.opencivilizationplatform.modules.production.application.FacilityService;
import io.github.opencivilizationplatform.modules.resources.application.ResourceService;
import io.github.opencivilizationplatform.modules.simulation.application.SimulationEngineService;
import io.github.opencivilizationplatform.modules.social.application.SocialStabilityService;
import io.github.opencivilizationplatform.modules.strategy.application.BalanceService;
import io.github.opencivilizationplatform.modules.civilization.application.CivilizationService;
import io.github.opencivilizationplatform.modules.region.application.ResourceRegionService;
import io.github.opencivilizationplatform.modules.nexus.application.NexusMeshService;
import io.github.opencivilizationplatform.modules.technology.application.TechnologyService;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;
import java.util.ArrayList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PageControllerTest {

    private MockMvc mockMvc;
    private BiosphereMetricService biosphereMetricService;
    private NeedService needService;
    private ResourceService resourceService;
    private BalanceService balanceService;
    private FacilityService facilityService;
    private ShipmentService shipmentService;
    private InteractionService interactionService;
    private RuleService ruleService;
    private ContributionService contributionService;
    private SimulationEngineService simulationEngineService;
    private SocialStabilityService socialStabilityService;
    private AutomationUnitService automationService;
    private CivilizationService civilizationService;
    private ResourceRegionService regionService;
    private NexusMeshService nexusService;
    private TechnologyService technologyService;
    private io.github.opencivilizationplatform.modules.nexus.infrastructure.MeshTradeRepository meshTradeRepository;
    private io.github.opencivilizationplatform.modules.social.infrastructure.EspionageRepository espionageRepository;

    @BeforeEach
    void setUp() {
        biosphereMetricService = mock(BiosphereMetricService.class);
        needService = mock(NeedService.class);
        resourceService = mock(ResourceService.class);
        balanceService = mock(BalanceService.class);
        facilityService = mock(FacilityService.class);
        shipmentService = mock(ShipmentService.class);
        interactionService = mock(InteractionService.class);
        ruleService = mock(RuleService.class);
        contributionService = mock(ContributionService.class);
        simulationEngineService = mock(SimulationEngineService.class);
        socialStabilityService = mock(SocialStabilityService.class);
        automationService = mock(AutomationUnitService.class);
        civilizationService = mock(CivilizationService.class);
        regionService = mock(ResourceRegionService.class);
        nexusService = mock(NexusMeshService.class);
        technologyService = mock(TechnologyService.class);
        meshTradeRepository = mock(io.github.opencivilizationplatform.modules.nexus.infrastructure.MeshTradeRepository.class);
        espionageRepository = mock(io.github.opencivilizationplatform.modules.social.infrastructure.EspionageRepository.class);
        mockMvc = standaloneSetup(new PageController(biosphereMetricService, needService, resourceService, balanceService, facilityService, shipmentService, interactionService, ruleService, contributionService, simulationEngineService, socialStabilityService, automationService, civilizationService, regionService, nexusService, technologyService, meshTradeRepository, espionageRepository)).build();
    }


    @Test
    void testDashboard() throws Exception {
        when(resourceService.getAllResources(any(Pageable.class))).thenReturn(Page.empty());
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("layout"))
                .andExpect(model().attribute("viewName", "dashboard"));
    }

    @Test
    void testNexus() throws Exception {
        when(nexusService.getAllNodes()).thenReturn(new ArrayList<>());
        mockMvc.perform(get("/nexus"))
                .andExpect(status().isOk())
                .andExpect(view().name("layout"))
                .andExpect(model().attribute("viewName", "nexus"));
    }

    @Test
    void testBiosphere() throws Exception {
        when(biosphereMetricService.getAllMetrics(any(Pageable.class))).thenReturn(Page.empty());
        mockMvc.perform(get("/biosphere"))
                .andExpect(status().isOk())
                .andExpect(view().name("layout"))
                .andExpect(model().attribute("viewName", "biosphere"));
    }

    @Test
    void testResources() throws Exception {
        when(resourceService.getAllResources(any(Pageable.class))).thenReturn(Page.empty());
        mockMvc.perform(get("/resources"))
                .andExpect(status().isOk())
                .andExpect(view().name("layout"))
                .andExpect(model().attribute("viewName", "resources"));
    }

    @Test
    void testNeeds() throws Exception {
        when(needService.getAllNeeds(any(Pageable.class))).thenReturn(Page.empty());
        mockMvc.perform(get("/needs"))
                .andExpect(status().isOk())
                .andExpect(view().name("layout"))
                .andExpect(model().attribute("viewName", "needs"));
    }

    @Test
    void testStrategy() throws Exception {
        mockMvc.perform(get("/strategy"))
                .andExpect(status().isOk())
                .andExpect(view().name("layout"))
                .andExpect(model().attribute("viewName", "strategy"));
    }

    @Test
    void testProduction() throws Exception {
        when(facilityService.getAllFacilities(any(Pageable.class))).thenReturn(Page.empty());
        mockMvc.perform(get("/production"))
                .andExpect(status().isOk())
                .andExpect(view().name("layout"))
                .andExpect(model().attribute("viewName", "production"));
    }

    @Test
    void testLogistics() throws Exception {
        when(shipmentService.getAllShipments(any(Pageable.class))).thenReturn(Page.empty());
        mockMvc.perform(get("/logistics"))
                .andExpect(status().isOk())
                .andExpect(view().name("layout"))
                .andExpect(model().attribute("viewName", "logistics"));
    }

    @Test
    void testInteraction() throws Exception {
        when(interactionService.getAllInteractions(any(Pageable.class))).thenReturn(Page.empty());
        mockMvc.perform(get("/interaction"))
                .andExpect(status().isOk())
                .andExpect(view().name("layout"))
                .andExpect(model().attribute("viewName", "interaction"));
    }

    @Test
    void testConstitution() throws Exception {
        when(ruleService.getAllRules(any(Pageable.class))).thenReturn(Page.empty());
        mockMvc.perform(get("/constitution"))
                .andExpect(status().isOk())
                .andExpect(view().name("layout"))
                .andExpect(model().attribute("viewName", "constitution"));
    }

    @Test
    void testPurpose() throws Exception {
        when(contributionService.getAllCitizens(any(Pageable.class))).thenReturn(Page.empty());
        when(contributionService.getAllContributions(any(Pageable.class))).thenReturn(Page.empty());
        mockMvc.perform(get("/purpose"))
                .andExpect(status().isOk())
                .andExpect(view().name("layout"))
                .andExpect(model().attribute("viewName", "purpose"));
    }

    @Test
    void testSocial() throws Exception {
        when(socialStabilityService.getAllIncidents(any(Pageable.class))).thenReturn(Page.empty());
        when(socialStabilityService.getAllCases(any(Pageable.class))).thenReturn(Page.empty());
        mockMvc.perform(get("/social"))
                .andExpect(status().isOk())
                .andExpect(view().name("layout"))
                .andExpect(model().attribute("viewName", "social"));
    }

    @Test
    void testSimulation() throws Exception {
        when(automationService.getAllUnits(any(Pageable.class))).thenReturn(Page.empty());
        mockMvc.perform(get("/simulation"))
                .andExpect(status().isOk())
                .andExpect(view().name("layout"))
                .andExpect(model().attribute("viewName", "simulation"));
    }
}

