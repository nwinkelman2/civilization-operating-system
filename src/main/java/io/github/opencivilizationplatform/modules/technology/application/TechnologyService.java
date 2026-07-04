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
    private final io.github.opencivilizationplatform.modules.nexus.infrastructure.TreatyRepository treatyRepository;

    public TechnologyService(TechnologyRepository repository, io.github.opencivilizationplatform.modules.nexus.infrastructure.TreatyRepository treatyRepository) {
        this.repository = repository;
        this.treatyRepository = treatyRepository;
    }

    @Transactional(readOnly = true)
    public List<Technology> getTechTree(Long civilizationId) {
        List<Technology> techs = repository.findByCivilizationId(civilizationId);
        applySpilloverCostReduction(civilizationId, techs);
        return techs;
    }

    private void applySpilloverCostReduction(Long civilizationId, List<Technology> techs) {
        if (treatyRepository == null) return;
        List<io.github.opencivilizationplatform.modules.nexus.domain.Treaty> activeTreaties = 
            treatyRepository.findByStatus(io.github.opencivilizationplatform.modules.nexus.domain.TreatyStatus.ACTIVE);
        
        java.util.Set<Long> partnerCivIds = new java.util.HashSet<>();
        for (io.github.opencivilizationplatform.modules.nexus.domain.Treaty treaty : activeTreaties) {
            if (treaty.getType() == io.github.opencivilizationplatform.modules.nexus.domain.TreatyType.RESEARCH_ALLIANCE) {
                boolean involvesCiv = false;
                if (treaty.getProposerCivId().equals(civilizationId)) {
                    involvesCiv = true;
                } else if (treaty.getSignatoryCivIds() != null && treaty.getSignatoryCivIds().contains(String.valueOf(civilizationId))) {
                    involvesCiv = true;
                }
                
                if (involvesCiv) {
                    partnerCivIds.add(treaty.getProposerCivId());
                    if (treaty.getSignatoryCivIds() != null) {
                        for (String idStr : treaty.getSignatoryCivIds().split(",")) {
                            if (!idStr.trim().isEmpty()) {
                                try {
                                    partnerCivIds.add(Long.parseLong(idStr.trim()));
                                } catch (NumberFormatException ignored) {}
                            }
                        }
                    }
                }
            }
        }
        partnerCivIds.remove(civilizationId);
        if (partnerCivIds.isEmpty()) return;
        
        java.util.Set<String> completedPartnerTechNames = new java.util.HashSet<>();
        for (Long partnerId : partnerCivIds) {
            List<Technology> completedTechs = repository.findByCivilizationIdAndStatus(partnerId, TechnologyStatus.COMPLETED);
            for (Technology t : completedTechs) {
                completedPartnerTechNames.add(t.getName());
            }
        }
        if (completedPartnerTechNames.isEmpty()) return;
        
        for (Technology tech : techs) {
            if (tech.getStatus() != TechnologyStatus.COMPLETED && completedPartnerTechNames.contains(tech.getName())) {
                int originalCost = tech.getResearchCost();
                int reducedCost = (int) (originalCost * 0.70);
                tech.setResearchCost(reducedCost);
            }
        }
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
