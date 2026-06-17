package io.github.opencivilizationplatform.web.controller;

import io.github.opencivilizationplatform.modules.monitoring.application.BiosphereMetricService;
import io.github.opencivilizationplatform.modules.needs.application.NeedService;
import io.github.opencivilizationplatform.modules.resources.application.ResourceService;
import io.github.opencivilizationplatform.modules.strategy.application.BalanceService;
import io.github.opencivilizationplatform.modules.production.application.FacilityService;
import io.github.opencivilizationplatform.modules.logistics.application.ShipmentService;
import io.github.opencivilizationplatform.modules.participation.application.InteractionService;
import io.github.opencivilizationplatform.modules.participation.application.RuleService;
import io.github.opencivilizationplatform.modules.contribution.application.ContributionService;
import io.github.opencivilizationplatform.modules.simulation.application.CortexEngineService;
import io.github.opencivilizationplatform.modules.execution.infrastructure.AutomationUnitRepository;
import io.github.opencivilizationplatform.modules.social.application.SocialStabilityService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

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
    private final AutomationUnitRepository automationRepository;

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
                          AutomationUnitRepository automationRepository) {
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
        this.automationRepository = automationRepository;
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
        model.addAttribute("resources", resourceService.getAllResources());
        return render(model, "dashboard", "Dashboard", "dashboard");
    }

    @GetMapping("/biosphere")
    public String biosphere(Model model) {
        model.addAttribute("metrics", biosphereService.getAllMetrics());
        return render(model, "biosphere", "Biosphere", "biosphere");
    }

    @GetMapping("/resources")
    public String resources(Model model) {
        model.addAttribute("resources", resourceService.getAllResources());
        return render(model, "resources", "Resources", "resources");
    }

    @GetMapping("/needs")
    public String needs(Model model) {
        model.addAttribute("needs", needService.getAllNeeds());
        return render(model, "needs", "Needs", "needs");
    }

    @GetMapping("/strategy")
    public String strategy(Model model) {
        model.addAttribute("balance", balanceService.getBalanceReport());
        return render(model, "strategy", "Strategy", "strategy");
    }

    @GetMapping("/production")
    public String production(Model model) {
        model.addAttribute("facilities", facilityService.getAllFacilities());
        return render(model, "production", "Production", "production");
    }

    @GetMapping("/logistics")
    public String logistics(Model model) {
        model.addAttribute("shipments", shipmentService.getAllShipments());
        return render(model, "logistics", "Logistics", "logistics");
    }

    @GetMapping("/interaction")
    public String interaction(Model model) {
        model.addAttribute("interactions", interactionService.getAllInteractions());
        return render(model, "interaction", "Interaction", "interaction");
    }

    @GetMapping("/constitution")
    public String constitution(Model model) {
        model.addAttribute("rules", ruleService.getAllRules());
        return render(model, "constitution", "Governance", "constitution");
    }

    @GetMapping("/purpose")
    public String purpose(Model model) {
        model.addAttribute("citizens", contributionService.getAllCitizens());
        model.addAttribute("projects", contributionService.getActiveProjects());
        model.addAttribute("contributions", contributionService.getAllContributions());
        return render(model, "purpose", "Contribution", "purpose");
    }

    @GetMapping("/social")
    public String social(Model model) {
        model.addAttribute("incidents", socialService.getAllIncidents());
        model.addAttribute("cases", socialService.getAllCases());
        return render(model, "social", "Social Stability", "social");
    }

    @GetMapping("/simulation")
    public String simulation(Model model) {
        model.addAttribute("status", cortexService.getStatus());
        model.addAttribute("balance", balanceService.getBalanceReport());
        model.addAttribute("automations", automationRepository.findAll());
        return render(model, "simulation", "Cortex Engine", "simulation");
    }

    @GetMapping("/simulation/fragments/cortex-telemetry")
    public String cortexTelemetry(Model model) {
        model.addAttribute("status", cortexService.getStatus());
        model.addAttribute("balance", balanceService.getBalanceReport());
        model.addAttribute("automations", automationRepository.findAll());
        return "simulation :: cortex-telemetry";
    }

    @GetMapping("/simulation/fragments/decision-log")
    public String decisionLog(Model model) {
        model.addAttribute("status", cortexService.getStatus());
        return "simulation :: decision-log";
    }
}
