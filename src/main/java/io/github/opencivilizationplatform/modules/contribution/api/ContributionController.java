package io.github.opencivilizationplatform.modules.contribution.api;

import io.github.opencivilizationplatform.modules.contribution.domain.Contribution;
import io.github.opencivilizationplatform.modules.contribution.application.ContributionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/purpose")
@Tag(name = "Contributions", description = "Contribution management endpoints")
public class ContributionController {

    private final ContributionService contributionService;

    public ContributionController(ContributionService contributionService) {
        this.contributionService = contributionService;
    }

    @PostMapping("/contribute")
    @Operation(summary = "Record a contribution", description = "Records a new contribution")
    public Contribution recordContribution(@Valid @RequestBody Contribution contribution) {
        return contributionService.recordContribution(contribution);
    }

    @GetMapping("/contributions")
    @Operation(summary = "List all contributions", description = "Returns a paginated list of contributions")
    public Page<Contribution> getAllContributions(Pageable pageable) {
        return contributionService.getAllContributions(pageable);
    }

    @GetMapping("/citizens")
    @Operation(summary = "List all citizens", description = "Returns a paginated list of citizens")
    public Page<io.github.opencivilizationplatform.modules.contribution.domain.Citizen> getAllCitizens(Pageable pageable) {
        return contributionService.getAllCitizens(pageable);
    }

    @GetMapping("/projects")
    @Operation(summary = "List all projects", description = "Returns a list of active projects")
    public java.util.List<io.github.opencivilizationplatform.modules.contribution.domain.Project> getAllProjects() {
        return contributionService.getActiveProjects();
    }

    @GetMapping("/citizens/{citizenId}/impact")
    @Operation(summary = "Get citizen impact", description = "Returns contributions for a specific citizen")
    public java.util.List<Contribution> getImpact(@PathVariable String citizenId) {
        return contributionService.getCitizenContributions(citizenId);
    }
}
