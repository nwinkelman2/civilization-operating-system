package io.github.opencivilizationplatform.web.controller;

import io.github.opencivilizationplatform.modules.civilization.application.CivilizationService;
import io.github.opencivilizationplatform.modules.monitoring.application.BiosphereMetricService;
import io.github.opencivilizationplatform.modules.needs.application.NeedService;
import io.github.opencivilizationplatform.modules.region.application.ResourceRegionService;
import io.github.opencivilizationplatform.modules.resources.application.ResourceService;
import io.github.opencivilizationplatform.modules.strategy.application.BalanceService;
import io.github.opencivilizationplatform.modules.production.application.FacilityService;
import io.github.opencivilizationplatform.modules.logistics.application.ShipmentService;
import io.github.opencivilizationplatform.modules.participation.application.InteractionService;
import io.github.opencivilizationplatform.modules.participation.application.RuleService;
import io.github.opencivilizationplatform.modules.contribution.application.ContributionService;
import io.github.opencivilizationplatform.modules.simulation.application.CortexEngineService;
import io.github.opencivilizationplatform.modules.execution.application.AutomationUnitService;
import io.github.opencivilizationplatform.modules.social.application.SocialStabilityService;
import io.github.opencivilizationplatform.modules.voxtex.application.VoxtexMeshService;
import io.github.opencivilizationplatform.modules.technology.application.TechnologyService;
import io.github.opencivilizationplatform.modules.technology.domain.TechnologyStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@Controller
public class PageController {

    private final BiosphereMetricService biosphereService;
    private final NeedService needService;
    private final ResourceService resourceService;
    private final BalanceService balanceService;
    private final FacilityService facilityService;
    private final ShipmentService shipmentService;
    private final InteractionService interactionService;
    private final RuleService ruleService;
    private final ContributionService contributionService;
    private final CortexEngineService cortexService;
    private final SocialStabilityService socialService;
    private final AutomationUnitService automationService;
    private final CivilizationService civilizationService;
    private final ResourceRegionService regionService;
    private final VoxtexMeshService voxtexService;
    private final TechnologyService technologyService;

    public PageController(BiosphereMetricService biosphereService,
                          NeedService needService,
                          ResourceService resourceService,
                          BalanceService balanceService,
                          FacilityService facilityService,
                          ShipmentService shipmentService,
                          InteractionService interactionService,
                          RuleService ruleService,
                          ContributionService contributionService,
                          CortexEngineService cortexService,
                          SocialStabilityService socialService,
                          AutomationUnitService automationService,
                          CivilizationService civilizationService,
                          ResourceRegionService regionService,
                          VoxtexMeshService voxtexService,
                          TechnologyService technologyService) {
        this.biosphereService = biosphereService;
        this.needService = needService;
        this.resourceService = resourceService;
        this.balanceService = balanceService;
        this.facilityService = facilityService;
        this.shipmentService = shipmentService;
        this.interactionService = interactionService;
        this.ruleService = ruleService;
        this.contributionService = contributionService;
        this.cortexService = cortexService;
        this.socialService = socialService;
        this.automationService = automationService;
        this.civilizationService = civilizationService;
        this.regionService = regionService;
        this.voxtexService = voxtexService;
        this.technologyService = technologyService;
    }

    private String render(Model model, String viewName, String pageTitle, String currentPage) {
        model.addAttribute("viewName", viewName);
        model.addAttribute("pageTitle", pageTitle);
        model.addAttribute("currentPage", currentPage);
        return "layout";
    }

    @GetMapping("/")
    public String dashboard(Model model) {
        model.addAttribute("balance", balanceService.getBalanceReport());
        model.addAttribute("simulationStatus", cortexService.getStatus());
        model.addAttribute("resources", resourceService.getAllResources(Pageable.unpaged()).getContent());
        return render(model, "dashboard", "Dashboard", "dashboard");
    }

    @GetMapping("/biosphere")
    public String biosphere(Model model) {
        model.addAttribute("metrics", biosphereService.getAllMetrics(Pageable.unpaged()).getContent());
        return render(model, "biosphere", "Biosphere", "biosphere");
    }

    @GetMapping("/resources")
    public String resources(Model model) {
        model.addAttribute("resources", resourceService.getAllResources(Pageable.unpaged()).getContent());
        return render(model, "resources", "Resources", "resources");
    }

    @GetMapping("/needs")
    public String needs(Model model) {
        model.addAttribute("needs", needService.getAllNeeds(Pageable.unpaged()).getContent());
        return render(model, "needs", "Needs", "needs");
    }

    @GetMapping("/strategy")
    public String strategy(Model model) {
        model.addAttribute("balance", balanceService.getBalanceReport());
        return render(model, "strategy", "Strategy", "strategy");
    }

    @GetMapping("/production")
    public String production(Model model) {
        model.addAttribute("facilities", facilityService.getAllFacilities(Pageable.unpaged()).getContent());
        return render(model, "production", "Production", "production");
    }

    @GetMapping("/logistics")
    public String logistics(Model model) {
        model.addAttribute("shipments", shipmentService.getAllShipments(Pageable.unpaged()).getContent());
        return render(model, "logistics", "Logistics", "logistics");
    }

    @GetMapping("/interaction")
    public String interaction(Model model) {
        model.addAttribute("interactions", interactionService.getAllInteractions(Pageable.unpaged()).getContent());
        return render(model, "interaction", "Interaction", "interaction");
    }

    @GetMapping("/constitution")
    public String constitution(Model model) {
        model.addAttribute("rules", ruleService.getAllRules(Pageable.unpaged()).getContent());
        return render(model, "constitution", "Governance", "constitution");
    }

    @GetMapping("/purpose")
    public String purpose(Model model) {
        model.addAttribute("citizens", contributionService.getAllCitizens(Pageable.unpaged()).getContent());
        model.addAttribute("projects", contributionService.getActiveProjects());
        model.addAttribute("contributions", contributionService.getAllContributions(Pageable.unpaged()).getContent());
        return render(model, "purpose", "Contribution", "purpose");
    }

    @GetMapping("/social")
    public String social(Model model) {
        model.addAttribute("incidents", socialService.getAllIncidents(Pageable.unpaged()).getContent());
        model.addAttribute("cases", socialService.getAllCases(Pageable.unpaged()).getContent());
        return render(model, "social", "Social Stability", "social");
    }

    @GetMapping("/simulation")
    public String simulation(Model model) {
        model.addAttribute("status", cortexService.getStatus());
        model.addAttribute("balance", balanceService.getBalanceReport());
        model.addAttribute("automations", automationService.getAllUnits(Pageable.unpaged()).getContent());
        return render(model, "simulation", "Cortex Engine", "simulation");
    }

    @GetMapping("/simulation/fragments/cortex-telemetry")
    public String cortexTelemetry(Model model) {
        model.addAttribute("status", cortexService.getStatus());
        model.addAttribute("balance", balanceService.getBalanceReport());
        model.addAttribute("automations", automationService.getAllUnits(Pageable.unpaged()).getContent());
        return "simulation :: cortex-telemetry";
    }

    @GetMapping("/simulation/fragments/decision-log")
    public String decisionLog(Model model) {
        model.addAttribute("status", cortexService.getStatus());
        return "simulation :: decision-log";
    }

    @GetMapping("/resources/fragment")
    public String resourceTable(Model model, @RequestParam(required = false, defaultValue = "") String type) {
        var allResources = resourceService.getAllResources(Pageable.unpaged()).getContent();
        model.addAttribute("resources", type.isBlank() ? allResources : allResources.stream()
                .filter(r -> r.getType().name().toLowerCase().contains(type.toLowerCase()))
                .toList());
        return "resources :: resource-table";
    }

    @GetMapping("/needs/fragment")
    public String needTable(Model model, @RequestParam(required = false, defaultValue = "") String region) {
        var allNeeds = needService.getAllNeeds(Pageable.unpaged()).getContent();
        model.addAttribute("needs", region.isBlank() ? allNeeds : allNeeds.stream()
                .filter(n -> n.getRegion().toLowerCase().contains(region.toLowerCase()))
                .toList());
        return "needs :: needs-table";
    }

    @GetMapping("/play")
    public String play(Model model) {
        model.addAttribute("regions", regionService.getAllRegions());
        return render(model, "play", "Found Civilization", "play");
    }

    @GetMapping("/civilization/{id}")
    public String civilizationDetail(@PathVariable Long id, Model model) {
        var civ = civilizationService.getCivilizationOrNull(id);
        if (civ == null) {
            return "redirect:/play";
        }
        model.addAttribute("civ", civ);

        var region = civ.getHomeRegion();
        model.addAttribute("region", region);

        var nodes = voxtexService.getNodesForCivilization(id);
        model.addAttribute("nodes", nodes);

        var messages = nodes.isEmpty() ? List.of() :
            voxtexService.getConversation(nodes.get(0).getId(),
                nodes.size() > 1 ? nodes.get(1).getId() : nodes.get(0).getId());
        model.addAttribute("messages", messages);

        var techTree = technologyService.getTechTree(id);
        model.addAttribute("techTree", techTree);
        model.addAttribute("techCount", techTree.stream()
            .filter(t -> t.getStatus() == TechnologyStatus.COMPLETED).count());

        // Build resource list from region
        if (region != null) {
            model.addAttribute("resourceList", List.of(
                Map.of("label", "FOOD", "value", region.getFoodAvailability(), "color", "#00f2ff"),
                Map.of("label", "WATER", "value", region.getWaterAvailability(), "color", "#006aff"),
                Map.of("label", "MINERAL", "value", region.getMineralAvailability(), "color", "#ff6b35"),
                Map.of("label", "ENERGY", "value", region.getEnergyAvailability(), "color", "#ffd700"),
                Map.of("label", "HOUSING", "value", region.getHousingAvailability(), "color", "#00ff88")
            ));
        } else {
            model.addAttribute("resourceList", List.of());
        }

        return render(model, "civilization", "Civilization: " + civ.getName(), "civilization");
    }

    @GetMapping("/voxtex")
    public String voxtex(Model model) {
        model.addAttribute("status", voxtexService.getNetworkStatus());
        model.addAttribute("nodes", voxtexService.getAllNodes());
        model.addAttribute("connections", voxtexService.getAllConnections());
        model.addAttribute("recentMessages",
            voxtexService.getAllNodes().isEmpty() ? List.of() :
            voxtexService.getConversation(
                voxtexService.getAllNodes().get(0).getId(),
                voxtexService.getAllNodes().size() > 1 ?
                    voxtexService.getAllNodes().get(1).getId() :
                    voxtexService.getAllNodes().get(0).getId()
            )
        );
        return render(model, "voxtex", "Voxtex Mesh", "voxtex");
    }
}
