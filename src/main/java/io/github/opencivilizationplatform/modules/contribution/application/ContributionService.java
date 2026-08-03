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

import io.github.opencivilizationplatform.core.eventbus.EventBus;
import io.github.opencivilizationplatform.core.eventbus.events.ContributionSubmittedEvent;

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
    private final io.github.opencivilizationplatform.modules.civilization.infrastructure.CivilizationRepository civilizationRepository;
    private final io.github.opencivilizationplatform.modules.contribution.infrastructure.CitizenWalletRepository citizenWalletRepository;
    private final EventBus eventBus;

    public ContributionService(CitizenRepository citizenRepository,
                               ProjectRepository projectRepository,
                               ContributionRepository contributionRepository,
                               ResourceRegionRepository resourceRegionRepository,
                               io.github.opencivilizationplatform.modules.civilization.infrastructure.CivilizationRepository civilizationRepository,
                               io.github.opencivilizationplatform.modules.contribution.infrastructure.CitizenWalletRepository citizenWalletRepository,
                               EventBus eventBus) {
        this.citizenRepository = citizenRepository;
        this.projectRepository = projectRepository;
        this.contributionRepository = contributionRepository;
        this.resourceRegionRepository = resourceRegionRepository;
        this.civilizationRepository = civilizationRepository;
        this.citizenWalletRepository = citizenWalletRepository;
        this.eventBus = eventBus;
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
        eventBus.publish(new ContributionSubmittedEvent(
            "ContributionService",
            saved.getId(),
            saved.getProject() != null ? saved.getProject().getId() : null,
            saved.getCitizen() != null ? saved.getCitizen().getId() : null,
            saved.getImpactScore()
        ));
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

        eventBus.publish(new ContributionSubmittedEvent(
            "ContributionService",
            saved.getId(),
            saved.getProject() != null ? saved.getProject().getId() : null,
            saved.getCitizen() != null ? saved.getCitizen().getId() : null,
            saved.getImpactScore()
        ));

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

    @Transactional
    public void donateToCommunitySilos(String citizenId, String resourceType, Double amount) {
        if (amount <= 0.0) {
            throw new IllegalArgumentException("Amount must be positive.");
        }
        Citizen citizen = citizenRepository.findByCitizenId(citizenId)
            .orElseThrow(() -> new IllegalArgumentException("Citizen not found: " + citizenId));
        
        if (citizen.getCivilization() == null) {
            throw new IllegalStateException("Citizen is not joined to any civilization.");
        }

        io.github.opencivilizationplatform.modules.contribution.domain.CitizenWallet wallet = citizen.getWallet();
        if (wallet == null) {
            throw new IllegalStateException("Citizen has no resource wallet.");
        }

        Civilization civ = citizen.getCivilization();

        // Check citizen wallet balance and subtract
        switch (resourceType.toUpperCase()) {
            case "FOOD":
                if (wallet.getFood() < amount) throw new IllegalStateException("Insufficient Food resources.");
                wallet.setFood(wallet.getFood() - amount);
                civ.setFood(Math.min(100.0, (civ.getFood() == null ? 0.0 : civ.getFood()) + amount));
                break;
            case "WATER":
                if (wallet.getWater() < amount) throw new IllegalStateException("Insufficient Water resources.");
                wallet.setWater(wallet.getWater() - amount);
                civ.setWater(Math.min(100.0, (civ.getWater() == null ? 0.0 : civ.getWater()) + amount));
                break;
            case "MINERALS":
                if (wallet.getMinerals() < amount) throw new IllegalStateException("Insufficient Minerals resources.");
                wallet.setMinerals(wallet.getMinerals() - amount);
                civ.setMinerals(Math.min(100.0, (civ.getMinerals() == null ? 0.0 : civ.getMinerals()) + amount));
                break;
            case "ENERGY":
                if (wallet.getEnergy() < amount) throw new IllegalStateException("Insufficient Energy resources.");
                wallet.setEnergy(wallet.getEnergy() - amount);
                civ.setEnergy(Math.min(100.0, (civ.getEnergy() == null ? 0.0 : civ.getEnergy()) + amount));
                break;
            default:
                throw new IllegalArgumentException("Invalid resource type: " + resourceType);
        }

        // Increase reputation by amount * 2.0 (e.g. donating 10 Food yields 20 Reputation score)
        citizen.setReputationScore((citizen.getReputationScore() == null ? 0.0 : citizen.getReputationScore()) + (amount * 2.0));
        
        citizenWalletRepository.save(wallet);
        citizenRepository.save(citizen);
        civilizationRepository.save(civ);
    }
}
