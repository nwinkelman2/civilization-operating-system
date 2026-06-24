package io.github.opencivilizationplatform.modules.contribution.application;

import io.github.opencivilizationplatform.modules.contribution.domain.Citizen;
import io.github.opencivilizationplatform.modules.contribution.domain.Contribution;
import io.github.opencivilizationplatform.modules.contribution.domain.Project;
import io.github.opencivilizationplatform.modules.contribution.domain.ProjectStatus;
import io.github.opencivilizationplatform.modules.contribution.infrastructure.CitizenRepository;
import io.github.opencivilizationplatform.modules.contribution.infrastructure.ContributionRepository;
import io.github.opencivilizationplatform.modules.contribution.infrastructure.ProjectRepository;
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

    public ContributionService(CitizenRepository citizenRepository,
                               ProjectRepository projectRepository,
                               ContributionRepository contributionRepository) {
        this.citizenRepository = citizenRepository;
        this.projectRepository = projectRepository;
        this.contributionRepository = contributionRepository;
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
}
