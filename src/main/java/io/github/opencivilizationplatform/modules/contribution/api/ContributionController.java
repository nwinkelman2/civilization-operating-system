package io.github.opencivilizationplatform.modules.contribution.api;

import io.github.opencivilizationplatform.modules.contribution.domain.Contribution;
import io.github.opencivilizationplatform.modules.contribution.application.ContributionService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/purpose")
public class ContributionController {

    private final ContributionService contributionService;

    public ContributionController(ContributionService contributionService) {
        this.contributionService = contributionService;
    }

    @PostMapping("/contribute")
    public Contribution recordContribution(@RequestBody Contribution contribution) {
        return contributionService.recordContribution(contribution);
    }

    @GetMapping("/contributions")
    public java.util.List<Contribution> getAllContributions() {
        return contributionService.getAllContributions();
    }

    @GetMapping("/citizens")
    public java.util.List<io.github.opencivilizationplatform.modules.contribution.domain.Citizen> getAllCitizens() {
        return contributionService.getAllCitizens();
    }

    @GetMapping("/projects")
    public java.util.List<io.github.opencivilizationplatform.modules.contribution.domain.Project> getAllProjects() {
        return contributionService.getActiveProjects();
    }

    @GetMapping("/citizens/{citizenId}/impact")
    public java.util.List<Contribution> getImpact(@PathVariable String citizenId) {
        return contributionService.getCitizenContributions(citizenId);
    }
}
