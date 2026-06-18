package io.github.opencivilizationplatform.modules.technology.application;

import io.github.opencivilizationplatform.modules.technology.domain.Technology;
import io.github.opencivilizationplatform.modules.technology.domain.TechnologyCategory;
import io.github.opencivilizationplatform.modules.technology.domain.TechnologyStatus;
import io.github.opencivilizationplatform.modules.technology.infrastructure.TechnologyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class TechnologyService {

    private final TechnologyRepository repository;

    public TechnologyService(TechnologyRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<Technology> getTechTree(Long civilizationId) {
        return repository.findByCivilizationId(civilizationId);
    }

    @Transactional
    public Technology addTechnology(Technology tech) {
        if (tech.getStatus() == null) tech.setStatus(TechnologyStatus.LOCKED);
        if (tech.getResearchProgress() == null) tech.setResearchProgress(0);
        return repository.save(tech);
    }

    @Transactional
    public Technology startResearch(Long techId) {
        Technology tech = repository.findById(techId).orElseThrow();
        tech.setStatus(TechnologyStatus.RESEARCHING);
        return repository.save(tech);
    }

    @Transactional
    public Technology advanceResearch(Long techId, int amount) {
        Technology tech = repository.findById(techId).orElseThrow();
        tech.setResearchProgress(tech.getResearchProgress() + amount);
        if (tech.getResearchProgress() >= tech.getResearchCost()) {
            tech.setStatus(TechnologyStatus.COMPLETED);
            tech.setResearchProgress(tech.getResearchCost());
        }
        return repository.save(tech);
    }
}
