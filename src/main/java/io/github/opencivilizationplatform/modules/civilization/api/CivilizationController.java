package io.github.opencivilizationplatform.modules.civilization.api;

import io.github.opencivilizationplatform.config.JwtService;
import io.github.opencivilizationplatform.config.seed.CivilizationScale;
import io.github.opencivilizationplatform.modules.civilization.application.CivilizationService;
import io.github.opencivilizationplatform.modules.civilization.domain.Civilization;
import io.github.opencivilizationplatform.modules.civilization.domain.CivilizationStatus;
import io.github.opencivilizationplatform.modules.participation.application.GovernanceBootstrapService;
import io.github.opencivilizationplatform.modules.region.application.ResourceRegionService;
import io.github.opencivilizationplatform.modules.nexus.application.NexusMeshService;
import io.github.opencivilizationplatform.modules.nexus.domain.NexusNodeType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/civilizations")
@Tag(name = "Civilizations", description = "Multi-civilization management endpoints")
public class CivilizationController {

    private final CivilizationService service;
    private final ResourceRegionService regionService;
    private final NexusMeshService NexusService;
    private final JwtService jwtService;
    private final GovernanceBootstrapService governanceBootstrap;
    private final io.github.opencivilizationplatform.modules.participation.application.RuleService ruleService;
    private final io.github.opencivilizationplatform.modules.contribution.application.ContributionService contributionService;
    private final io.github.opencivilizationplatform.modules.social.application.SocialStabilityService socialService;
    private final io.github.opencivilizationplatform.modules.contribution.application.DelegateElectionService electionService;
    private final io.github.opencivilizationplatform.modules.contribution.infrastructure.CitizenRepository citizenRepository;
    private final io.github.opencivilizationplatform.modules.nexus.application.TreatyService treatyService;
    private final io.github.opencivilizationplatform.modules.nexus.application.ElectionService nexusElectionService;
    private final io.github.opencivilizationplatform.modules.civilization.infrastructure.CivilizationRepository civilizationRepository;
    private final io.github.opencivilizationplatform.modules.participation.infrastructure.RuleRepository ruleRepository;
    private final io.github.opencivilizationplatform.modules.social.infrastructure.IncidentRepository incidentRepository;
    private final io.github.opencivilizationplatform.modules.nexus.infrastructure.MeshTradeRepository meshTradeRepository;

    public CivilizationController(CivilizationService service,
                                   ResourceRegionService regionService,
                                   NexusMeshService NexusService,
                                   JwtService jwtService,
                                   GovernanceBootstrapService governanceBootstrap,
                                   io.github.opencivilizationplatform.modules.participation.application.RuleService ruleService,
                                   io.github.opencivilizationplatform.modules.contribution.application.ContributionService contributionService,
                                   io.github.opencivilizationplatform.modules.social.application.SocialStabilityService socialService,
                                   io.github.opencivilizationplatform.modules.contribution.application.DelegateElectionService electionService,
                                   io.github.opencivilizationplatform.modules.contribution.infrastructure.CitizenRepository citizenRepository,
                                   io.github.opencivilizationplatform.modules.nexus.application.TreatyService treatyService,
                                   io.github.opencivilizationplatform.modules.nexus.application.ElectionService nexusElectionService,
                                   io.github.opencivilizationplatform.modules.civilization.infrastructure.CivilizationRepository civilizationRepository,
                                   io.github.opencivilizationplatform.modules.participation.infrastructure.RuleRepository ruleRepository,
                                   io.github.opencivilizationplatform.modules.social.infrastructure.IncidentRepository incidentRepository,
                                   io.github.opencivilizationplatform.modules.nexus.infrastructure.MeshTradeRepository meshTradeRepository) {
        this.service = service;
        this.regionService = regionService;
        this.NexusService = NexusService;
        this.jwtService = jwtService;
        this.governanceBootstrap = governanceBootstrap;
        this.ruleService = ruleService;
        this.contributionService = contributionService;
        this.socialService = socialService;
        this.electionService = electionService;
        this.citizenRepository = citizenRepository;
        this.treatyService = treatyService;
        this.nexusElectionService = nexusElectionService;
        this.civilizationRepository = civilizationRepository;
        this.ruleRepository = ruleRepository;
        this.incidentRepository = incidentRepository;
        this.meshTradeRepository = meshTradeRepository;
    }

    @GetMapping
    @Operation(summary = "List all civilizations")
    public Page<Civilization> getAll(@RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "20") int size) {
        return service.getAllCivilizations(org.springframework.data.domain.PageRequest.of(page, size));
    }

    @GetMapping("/mine")
    @Operation(summary = "Get my civilizations")
    public java.util.List<Civilization> getMine(HttpServletRequest request) {
        String token = resolveToken(request);
        return service.getCivilizationsByOwner(token);
    }

    @PostMapping
    @Operation(summary = "Found a new civilization")
    @ResponseStatus(HttpStatus.CREATED)
    public Civilization create(@RequestBody CreateCivilizationRequest request, HttpServletRequest http) {
        String token = resolveToken(http);
        return service.createCivilization(
            request.name(),
            request.scale() != null ? request.scale() : CivilizationScale.LOCAL,
            request.region() != null ? request.region() : "unknown",
            token
        );
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get civilization by ID")
    public Civilization getById(@PathVariable Long id) {
        return service.getCivilization(id);
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update civilization status")
    public Civilization updateStatus(@PathVariable Long id, @RequestBody UpdateStatusRequest request) {
        return service.updateStatus(id, request.status());
    }

    @PostMapping("/found")
    @Operation(summary = "Found a civilization on a region", description = "Creates a civilization on a resource region and deploys a primary Nexus node")
    @ResponseStatus(HttpStatus.CREATED)
    public Civilization found(@RequestBody FoundCivilizationRequest request, HttpServletRequest http) {
        String token = resolveToken(http);
        Civilization civ = service.createCivilization(
            request.name(),
            request.scale() != null ? request.scale() : CivilizationScale.LOCAL,
            regionService.getRegion(request.regionId()).getName(),
            token
        );

        // Claim the region
        regionService.claimRegion(request.regionId(), civ.getId());

        // Update region link
        civ = service.getCivilization(civ.getId());
        var region = regionService.getRegion(request.regionId());
        // Deploy primary Nexus node
        NexusService.registerNode(
            civ.getName() + "-Primary",
            NexusNodeType.PRIMARY,
            region.getName(),
            civ.getId(),
            "Primary neural node for " + civ.getName()
        );

        // Bootstrap default Nexus governance rules
        governanceBootstrap.bootstrapGovernance(civ);

        return civ;
    }

    @GetMapping("/map-status")
    @Operation(summary = "Get world map claim status", description = "Returns total cities, claimed count, available count, and whether all cities are claimed")
    public MapStatusResponse mapStatus() {
        long total = regionService.countTotal();
        long available = regionService.countAvailable();
        long claimed = total - available;
        return new MapStatusResponse(total, claimed, available, available == 0);
    }

    @PostMapping("/{id}/join")
    @Operation(summary = "Join a civilization as an agent")
    public Civilization joinAsAgent(@PathVariable Long id, HttpServletRequest request) {
        String token = resolveToken(request);
        return service.joinAsAgent(id, token);
    }

    @GetMapping("/{id}/rules")
    @Operation(summary = "Get constitutional rules of a civilization")
    public java.util.List<io.github.opencivilizationplatform.modules.participation.domain.Rule> getRules(@PathVariable Long id) {
        return ruleService.getRulesByCivilization(id);
    }

    @PostMapping("/{id}/rules/propose")
    @Operation(summary = "Propose a constitutional rule for a civilization")
    @ResponseStatus(HttpStatus.CREATED)
    public io.github.opencivilizationplatform.modules.participation.domain.Rule proposeRule(@PathVariable Long id, @RequestBody ProposeRuleRequest request) {
        Civilization civ = service.getCivilization(id);
        io.github.opencivilizationplatform.modules.participation.domain.Rule rule = new io.github.opencivilizationplatform.modules.participation.domain.Rule();
        rule.setTitle(request.title());
        rule.setDescription(request.description());
        rule.setLogicCode(request.logicCode() != null && !request.logicCode().isBlank() ? request.logicCode() : "{\"type\": \"THRESHOLD_TRIGGER\", \"metric\": \"ENERGY\", \"action\": \"RESTRICT\"}");
        return ruleService.proposeRuleForCivilization(rule, civ);
    }

    @PostMapping("/{id}/rules/{ruleId}/vote")
    @Operation(summary = "Vote on a constitutional rule")
    public io.github.opencivilizationplatform.modules.participation.domain.Rule voteRule(@PathVariable Long id, @PathVariable Long ruleId) {
        return ruleService.voteRule(ruleId);
    }

    @GetMapping("/{id}/projects")
    @Operation(summary = "Get cooperative projects of a civilization")
    public java.util.List<io.github.opencivilizationplatform.modules.contribution.domain.Project> getProjects(@PathVariable Long id) {
        return contributionService.getProjectsForCivilization(id);
    }

    @PostMapping("/{id}/projects/propose")
    @Operation(summary = "Propose a cooperative project for a civilization")
    @ResponseStatus(HttpStatus.CREATED)
    public io.github.opencivilizationplatform.modules.contribution.domain.Project proposeProject(@PathVariable Long id, @RequestBody ProposeProjectRequest request) {
        Civilization civ = service.getCivilization(id);
        io.github.opencivilizationplatform.modules.contribution.domain.Project proj = new io.github.opencivilizationplatform.modules.contribution.domain.Project();
        proj.setTitle(request.title());
        proj.setDescription(request.description());
        proj.setCategory(request.category());
        proj.setImpactArea(request.impactArea() != null ? request.impactArea() : io.github.opencivilizationplatform.modules.contribution.domain.ImpactArea.INFRASTRUCTURE);
        proj.setRequiredSkillNames(java.util.List.of("Engineering", "Science"));
        return contributionService.proposeProjectForCivilization(proj, civ);
    }

    @PostMapping("/{id}/projects/{projectId}/contribute")
    @Operation(summary = "Contribute skills to a cooperative project")
    public io.github.opencivilizationplatform.modules.contribution.domain.Contribution contributeProject(@PathVariable Long id, @PathVariable Long projectId, @RequestBody ContributeProjectRequest request) {
        return contributionService.contributeToProject(projectId, request.citizenId(), request.role());
    }

    @GetMapping("/{id}/incidents")
    @Operation(summary = "Get social and ecological incidents of a civilization")
    public java.util.List<io.github.opencivilizationplatform.modules.social.domain.Incident> getIncidents(@PathVariable Long id) {
        return socialService.getIncidentsForCivilization(id);
    }

    @PostMapping("/{id}/incidents/propose")
    @Operation(summary = "Report an incident for a civilization")
    @ResponseStatus(HttpStatus.CREATED)
    public io.github.opencivilizationplatform.modules.social.domain.Incident proposeIncident(@PathVariable Long id, @RequestBody ProposeIncidentRequest request) {
        Civilization civ = service.getCivilization(id);
        io.github.opencivilizationplatform.modules.social.domain.Incident inc = new io.github.opencivilizationplatform.modules.social.domain.Incident();
        inc.setType(request.type() != null ? request.type() : io.github.opencivilizationplatform.modules.social.domain.IncidentType.CONFLICT);
        inc.setLocation(civ.getHomeRegion() != null ? civ.getHomeRegion().getName() : "Local Settlement");
        inc.setDescription(request.description());
        inc.setRiskLevel(request.riskLevel() != null ? request.riskLevel() : io.github.opencivilizationplatform.modules.social.domain.RiskLevel.MEDIUM);
        inc.setParticipantIds(java.util.List.of("CIT-0001", "CIT-0002"));
        return socialService.createIncidentForCivilization(inc, civ);
    }

    @PostMapping("/{id}/incidents/{incidentId}/mediate")
    @Operation(summary = "Mediate and resolve an incident")
    public io.github.opencivilizationplatform.modules.social.domain.Incident mediateIncident(@PathVariable Long id, @PathVariable Long incidentId) {
        return socialService.mediateIncident(incidentId);
    }

    @PostMapping("/{id}/incidents/{incidentId}/assign-bots")
    @Operation(summary = "Assign Security and Eco bots to resolve an incident")
    public io.github.opencivilizationplatform.modules.social.domain.Incident assignBotsToIncident(
            @PathVariable Long id,
            @PathVariable Long incidentId,
            @RequestParam(defaultValue = "0") int ecoBots,
            @RequestParam(defaultValue = "0") int securityBots) {
        return socialService.assignBotsToIncident(incidentId, ecoBots, securityBots);
    }

    // ===== TREATIES =====

    @PostMapping("/{id}/treaties/propose")
    @Operation(summary = "Propose a formal treaty between civilizations")
    public io.github.opencivilizationplatform.modules.nexus.domain.Treaty proposeTreaty(
            @PathVariable Long id,
            @RequestBody java.util.Map<String, Object> body) {
        String title = (String) body.getOrDefault("title", "Unnamed Treaty");
        io.github.opencivilizationplatform.modules.nexus.domain.TreatyType type =
            io.github.opencivilizationplatform.modules.nexus.domain.TreatyType.valueOf((String) body.getOrDefault("type", "FREE_TRADE"));
        @SuppressWarnings("unchecked")
        java.util.List<Long> invited = ((java.util.List<Number>) body.getOrDefault("invitedCivIds", java.util.List.of()))
            .stream().map(Number::longValue).toList();
        return treatyService.proposeTreaty(title, type, id, invited);
    }

    @PostMapping("/{id}/treaties/{treatyId}/sign")
    @Operation(summary = "Sign a proposed treaty")
    public io.github.opencivilizationplatform.modules.nexus.domain.Treaty signTreaty(
            @PathVariable Long id,
            @PathVariable Long treatyId) {
        return treatyService.signTreaty(treatyId, id);
    }

    @GetMapping("/{id}/treaties")
    @Operation(summary = "Get all treaties involving a civilization")
    public java.util.List<io.github.opencivilizationplatform.modules.nexus.domain.Treaty> getTreaties(@PathVariable Long id) {
        return treatyService.getTreatiesForCiv(id);
    }

    // ===== ELECTIONS =====

    @PostMapping("/{id}/elections/open")
    @Operation(summary = "Manually open an election in a civilization")
    public io.github.opencivilizationplatform.modules.nexus.domain.Election openElection(@PathVariable Long id) {
        return nexusElectionService.openElection(id);
    }

    @PostMapping("/{id}/elections/{electionId}/vote")
    @Operation(summary = "Cast a vote in an open election")
    public io.github.opencivilizationplatform.modules.nexus.domain.ElectionVote castVote(
            @PathVariable Long id,
            @PathVariable Long electionId,
            @RequestParam String voter,
            @RequestParam String candidate) {
        return nexusElectionService.castVote(electionId, voter, candidate);
    }

    @GetMapping("/{id}/elections")
    @Operation(summary = "Get all elections for a civilization")
    public java.util.List<io.github.opencivilizationplatform.modules.nexus.domain.Election> getElections(@PathVariable Long id) {
        return nexusElectionService.getElectionsForCiv(id);
    }

    @GetMapping("/{id}/elections/{electionId}/votes")
    @Operation(summary = "Get all votes for a specific election")
    public java.util.List<io.github.opencivilizationplatform.modules.nexus.domain.ElectionVote> getElectionVotes(
            @PathVariable Long id,
            @PathVariable Long electionId) {
        return nexusElectionService.getVotesForElection(electionId);
    }

    // ===== NETWORK VIEWS =====

    @GetMapping("/network-summary")
    @Operation(summary = "Lightweight summary of all civilizations for network map")
    public java.util.List<java.util.Map<String, Object>> getNetworkSummary() {
        return civilizationRepository.findAll().stream().map(civ -> {
            java.util.Map<String, Object> m = new java.util.LinkedHashMap<>();
            m.put("id", civ.getId());
            m.put("name", civ.getName());
            m.put("population", civ.getPopulation());
            m.put("reputation", civ.getReputationScore());
            m.put("food", civ.getFood());
            m.put("water", civ.getWater());
            m.put("energy", civ.getEnergy());
            m.put("minerals", civ.getMinerals());
            m.put("status", civ.getStatus());
            if (civ.getHomeRegion() != null && civ.getHomeRegion().getLocation() != null) {
                m.put("latitude", civ.getHomeRegion().getLocation().getY());
                m.put("longitude", civ.getHomeRegion().getLocation().getX());
            } else {
                m.put("latitude", 0.0);
                m.put("longitude", 0.0);
            }
            long activeRules = ruleRepository.findByCivilizationId(civ.getId()).stream()
                .filter(r -> io.github.opencivilizationplatform.modules.participation.domain.RuleStatus.ACTIVE.equals(r.getStatus())).count();
            long openIncidents = incidentRepository.findByCivilizationId(civ.getId()).stream()
                .filter(i -> !io.github.opencivilizationplatform.modules.social.domain.IncidentStatus.RESOLVED.equals(i.getStatus())).count();
            long activeTrades = meshTradeRepository.findAll().stream()
                .filter(t -> (t.getSender() != null && t.getSender().getId().equals(civ.getId())) || 
                             (t.getReceiver() != null && t.getReceiver().getId().equals(civ.getId()))).count();
            m.put("activeRulesCount", activeRules);
            m.put("openIncidentsCount", openIncidents);
            m.put("activeTradesCount", activeTrades);
            return m;
        }).toList();
    }

    @GetMapping("/global-dashboard")
    @Operation(summary = "Aggregated global civilization stats for network dashboard")
    public java.util.Map<String, Object> getGlobalDashboard() {
        var civs = civilizationRepository.findAll();
        long totalPop = civs.stream().mapToLong(c -> c.getPopulation() == null ? 0 : c.getPopulation()).sum();
        double avgRep = civs.stream().mapToDouble(c -> c.getReputationScore() == null ? 0 : c.getReputationScore()).average().orElse(0);
        java.util.Map<String, Object> result = new java.util.LinkedHashMap<>();
        result.put("totalCivilizations", civs.size());
        result.put("totalPopulation", totalPop);
        result.put("averageReputation", avgRep);
        result.put("civilizations", civs.stream().map(civ -> {
            java.util.Map<String, Object> m = new java.util.LinkedHashMap<>();
            m.put("id", civ.getId());
            m.put("name", civ.getName());
            m.put("population", civ.getPopulation());
            m.put("reputation", civ.getReputationScore());
            m.put("food", civ.getFood());
            m.put("water", civ.getWater());
            m.put("energy", civ.getEnergy());
            m.put("minerals", civ.getMinerals());
            m.put("housing", civ.getHousing());
            m.put("status", civ.getStatus());
            long activeRules = ruleRepository.findByCivilizationId(civ.getId()).stream()
                .filter(r -> io.github.opencivilizationplatform.modules.participation.domain.RuleStatus.ACTIVE.equals(r.getStatus())).count();
            long openIncidents = incidentRepository.findByCivilizationId(civ.getId()).stream()
                .filter(i -> !io.github.opencivilizationplatform.modules.social.domain.IncidentStatus.RESOLVED.equals(i.getStatus())).count();
            m.put("activeRulesCount", activeRules);
            m.put("openIncidentsCount", openIncidents);
            return m;
        }).toList());
        return result;
    }

    private String resolveToken(HttpServletRequest request) {
        String clientId = (String) request.getAttribute("X-Client-Id");
        if (clientId != null) return clientId;
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            try {
                return jwtService.extractClientId(authHeader.substring(7));
            } catch (Exception e) {
                // fall through
            }
        }
        String token = request.getHeader("X-Client-Token");
        if (token == null || token.isBlank()) {
            token = request.getRemoteAddr() + ":" + (request.getRemotePort());
        }
        return token;
    }

    @GetMapping("/me")
    @Operation(summary = "Get current citizen details")
    public io.github.opencivilizationplatform.modules.contribution.domain.Citizen getMe(HttpServletRequest http) {
        String token = resolveToken(http);
        return citizenRepository.findByCitizenId(token)
            .orElseGet(() -> {
                io.github.opencivilizationplatform.modules.contribution.domain.Citizen c = new io.github.opencivilizationplatform.modules.contribution.domain.Citizen();
                c.setCitizenId(token);
                c.setName("Visitor " + token.substring(Math.max(0, token.length() - 6)));
                c.setReputationScore(0.0);
                return c;
            });
    }

    @PostMapping("/{id}/donate")
    @Operation(summary = "Donate resources from personal wallet to community silos")
    public void donateResources(@PathVariable Long id, @RequestBody DonateRequest request, HttpServletRequest http) {
        String token = resolveToken(http);
        contributionService.donateToCommunitySilos(token, request.resourceType(), request.amount());
    }

    @GetMapping("/{id}/delegates/candidates")
    @Operation(summary = "Get list of eligible delegate candidates")
    public java.util.List<io.github.opencivilizationplatform.modules.contribution.domain.Citizen> getCandidates(@PathVariable Long id) {
        return electionService.getEligibleCandidates(id);
    }

    @PostMapping("/{id}/delegates/vote")
    @Operation(summary = "Vote for a sectoral delegate")
    public io.github.opencivilizationplatform.modules.contribution.domain.DelegateVote voteForDelegate(
            @PathVariable Long id, @RequestBody VoteDelegateRequest request, HttpServletRequest http) {
        String token = resolveToken(http);
        return electionService.voteForDelegate(token, request.candidateCitizenId(), request.sector(), id);
    }
}

record CreateCivilizationRequest(String name, CivilizationScale scale, String region) {}
record UpdateStatusRequest(CivilizationStatus status) {}
record FoundCivilizationRequest(String name, CivilizationScale scale, Long regionId, String founderName) {}
record MapStatusResponse(long total, long claimed, long available, boolean allClaimed) {}

record ProposeRuleRequest(String title, String description, String logicCode) {}
record ProposeProjectRequest(String title, String description, io.github.opencivilizationplatform.modules.contribution.domain.ProjectCategory category, io.github.opencivilizationplatform.modules.contribution.domain.ImpactArea impactArea) {}
record ContributeProjectRequest(String citizenId, String role) {}
record ProposeIncidentRequest(io.github.opencivilizationplatform.modules.social.domain.IncidentType type, String description, io.github.opencivilizationplatform.modules.social.domain.RiskLevel riskLevel) {}

record DonateRequest(String resourceType, Double amount) {}
record VoteDelegateRequest(String candidateCitizenId, String sector) {}

