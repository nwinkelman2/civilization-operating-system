package io.github.opencivilizationplatform.modules.contribution.application;

import io.github.opencivilizationplatform.modules.contribution.domain.Citizen;
import io.github.opencivilizationplatform.modules.contribution.domain.Contribution;
import io.github.opencivilizationplatform.modules.contribution.domain.Project;
import io.github.opencivilizationplatform.modules.contribution.domain.ProjectStatus;
import io.github.opencivilizationplatform.modules.contribution.infrastructure.CitizenRepository;
import io.github.opencivilizationplatform.modules.contribution.infrastructure.ContributionRepository;
import io.github.opencivilizationplatform.modules.contribution.infrastructure.ProjectRepository;
import io.github.opencivilizationplatform.modules.region.infrastructure.ResourceRegionRepository;
import io.github.opencivilizationplatform.modules.civilization.domain.Civilization;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ContributionService {

    private final CitizenRepository citizenRepository;
    private final ProjectRepository projectRepository;
    private final ContributionRepository contributionRepository;
    private final ResourceRegionRepository resourceRegionRepository;

    public ContributionService(CitizenRepository citizenRepository,
                               ProjectRepository projectRepository,
                               ContributionRepository contributionRepository,
                               ResourceRegionRepository resourceRegionRepository) {
        this.citizenRepository = citizenRepository;
        this.projectRepository = projectRepository;
        this.contributionRepository = contributionRepository;
        this.resourceRegionRepository = resourceRegionRepository;
    }

    public Page<Citizen> getAllCitizens(Pageable pageable) {
        return citizenRepository.findAll(pageable);
    }

    public List<Project> getActiveProjects() {
        return projectRepository.findByStatus(ProjectStatus.ACTIVE);
    }

    @Transactional
    public Contribution recordContribution(Contribution contribution) {
        Contribution saved = contributionRepository.save(contribution);
        if (contribution.getCitizen() != null) {
            citizenRepository.findByCitizenId(contribution.getCitizen().getCitizenId()).ifPresent(citizen -> {
                citizen.setReputationScore(citizen.getReputationScore() + contribution.getImpactScore());
                citizenRepository.save(citizen);
            });
        }
        return saved;
    }

    public List<Contribution> getCitizenContributions(String citizenId) {
        return contributionRepository.findByCitizen_CitizenId(citizenId);
    }

    public Page<Contribution> getAllContributions(Pageable pageable) {
        return contributionRepository.findAll(pageable);
    }

    public List<Project> getProjectsForCivilization(Long civId) {
        return projectRepository.findByCivilizationId(civId);
    }

    @Transactional
    public Project proposeProjectForCivilization(Project project, Civilization civ) {
        project.setCivilization(civ);
        project.setStatus(ProjectStatus.ACTIVE); // Seed it directly as active so players can contribute
        return projectRepository.save(project);
    }

    @Transactional
    public Contribution contributeToProject(Long projectId, String citizenId, String role) {
        Project proj = projectRepository.findById(projectId).orElseThrow(() -> new IllegalArgumentException("Project not found: " + projectId));
        Citizen cit = citizenRepository.findByCitizenId(citizenId).orElseGet(() -> {
            // Create a default citizen if it doesn't exist
            Citizen c = new Citizen();
            c.setCitizenId(citizenId);
            c.setName(citizenId.replace("CIT-", "Agent "));
            c.setReputationScore(10.0);
            c.setBiographicalNote("Collaborative agent of the society.");
            return citizenRepository.save(c);
        });

        Contribution contr = new Contribution();
        contr.setProject(proj);
        contr.setCitizen(cit);
        contr.setRole(role != null && !role.isBlank() ? role : "Engineering Collaborator");
        contr.setImpactScore(25.0);

        Contribution saved = contributionRepository.save(contr);

        // Update citizen reputation
        cit.setReputationScore((cit.getReputationScore() == null ? 0 : cit.getReputationScore()) + contr.getImpactScore());
        citizenRepository.save(cit);

        // Count contributions for this project
        long contributionCount = contributionRepository.findAll().stream()
                .filter(c -> projectId.equals(c.getProject().getId()))
                .count();

        if (ProjectStatus.ACTIVE.equals(proj.getStatus()) && contributionCount >= 3) {
            proj.setStatus(ProjectStatus.COMPLETED);
            projectRepository.save(proj);

            // Boost civilization home region resources
            if (proj.getCivilization() != null && proj.getCivilization().getHomeRegion() != null) {
                var region = proj.getCivilization().getHomeRegion();
                double boost = 15.0; // 15% boost
                switch (proj.getCategory()) {
                    case AGRICULTURE -> region.setFoodAvailability(Math.min(100.0, (region.getFoodAvailability() == null ? 50.0 : region.getFoodAvailability()) + boost));
                    case ENVIRONMENTAL -> region.setHousingAvailability(Math.min(100.0, (region.getHousingAvailability() == null ? 50.0 : region.getHousingAvailability()) + boost));
                    case ENERGY -> region.setEnergyAvailability(Math.min(100.0, (region.getEnergyAvailability() == null ? 50.0 : region.getEnergyAvailability()) + boost));
                    default -> region.setWaterAvailability(Math.min(100.0, (region.getWaterAvailability() == null ? 50.0 : region.getWaterAvailability()) + boost));
                }
                resourceRegionRepository.save(region);
            }
        }

        return saved;
    }
}
