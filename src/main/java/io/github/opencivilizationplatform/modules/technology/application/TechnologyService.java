package io.github.opencivilizationplatform.modules.technology.application;

import io.github.opencivilizationplatform.modules.technology.domain.Technology;
import io.github.opencivilizationplatform.modules.technology.domain.TechnologyCategory;
import io.github.opencivilizationplatform.modules.technology.domain.TechnologyStatus;
import io.github.opencivilizationplatform.modules.technology.domain.LicensedTechnology;
import io.github.opencivilizationplatform.modules.technology.infrastructure.TechnologyRepository;
import io.github.opencivilizationplatform.modules.technology.infrastructure.LicensedTechnologyRepository;
import io.github.opencivilizationplatform.modules.civilization.infrastructure.CivilizationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class TechnologyService {

    private final TechnologyRepository repository;
    private final io.github.opencivilizationplatform.modules.nexus.infrastructure.TreatyRepository treatyRepository;
    private final CivilizationRepository civilizationRepository;
    private final LicensedTechnologyRepository licensedTechnologyRepository;

    public TechnologyService(TechnologyRepository repository,
                             io.github.opencivilizationplatform.modules.nexus.infrastructure.TreatyRepository treatyRepository,
                             CivilizationRepository civilizationRepository,
                             LicensedTechnologyRepository licensedTechnologyRepository) {
        this.repository = repository;
        this.treatyRepository = treatyRepository;
        this.civilizationRepository = civilizationRepository;
        this.licensedTechnologyRepository = licensedTechnologyRepository;
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

    @Transactional
    public Technology contributeCoins(Long techId, Long civilizationId, Double coins) {
        Technology tech = repository.findById(techId).orElseThrow(() -> new IllegalArgumentException("Technology not found"));
        io.github.opencivilizationplatform.modules.civilization.domain.Civilization civ = 
            civilizationRepository.findById(civilizationId).orElseThrow(() -> new IllegalArgumentException("Civilization not found"));
        
        if (tech.getStatus() == TechnologyStatus.COMPLETED) {
            throw new IllegalStateException("Technology is already completed");
        }
        
        double currentCoins = civ.getConsensusCoins() != null ? civ.getConsensusCoins() : 0.0;
        if (currentCoins < coins) {
            throw new IllegalArgumentException("Insufficient Consensus Coins");
        }
        
        civ.setConsensusCoins(currentCoins - coins);
        civilizationRepository.save(civ);
        
        int progressIncrease = (int) (coins * 2.0);
        tech.setResearchProgress(tech.getResearchProgress() + progressIncrease);
        if (tech.getResearchProgress() >= tech.getResearchCost()) {
            tech.setStatus(TechnologyStatus.COMPLETED);
            tech.setResearchProgress(tech.getResearchCost());
        }
        
        return repository.save(tech);
    }

    @Transactional
    public LicensedTechnology licenseTechnology(Long techId, Long licenseeId, Double feePerTick) {
        Technology sourceTech = repository.findById(techId).orElseThrow(() -> new IllegalArgumentException("Technology not found"));
        if (sourceTech.getStatus() != TechnologyStatus.COMPLETED) {
            throw new IllegalStateException("Only completed technologies can be licensed");
        }
        
        io.github.opencivilizationplatform.modules.civilization.domain.Civilization licensor = 
            civilizationRepository.findById(sourceTech.getCivilizationId()).orElseThrow(() -> new IllegalArgumentException("Licensor civilization not found"));
        io.github.opencivilizationplatform.modules.civilization.domain.Civilization licensee = 
            civilizationRepository.findById(licenseeId).orElseThrow(() -> new IllegalArgumentException("Licensee civilization not found"));
        
        if (licensor.getId().equals(licensee.getId())) {
            throw new IllegalArgumentException("Cannot license technology to yourself");
        }
        
        boolean alreadyLicensed = licensedTechnologyRepository.findByLicenseeId(licenseeId).stream()
            .anyMatch(lt -> lt.getTechName().equals(sourceTech.getName()));
        if (alreadyLicensed) {
            throw new IllegalStateException("Technology is already licensed by this civilization");
        }
        
        LicensedTechnology lt = new LicensedTechnology();
        lt.setLicensor(licensor);
        lt.setLicensee(licensee);
        lt.setTechName(sourceTech.getName());
        lt.setFeePerTick(feePerTick);
        
        return licensedTechnologyRepository.save(lt);
    }

    @Transactional(readOnly = true)
    public List<LicensedTechnology> getLicensedTechnologies(Long licenseeId) {
        return licensedTechnologyRepository.findByLicenseeId(licenseeId);
    }

    @Transactional(readOnly = true)
    public List<Technology> getLicensableTechnologies(Long civilizationId) {
        return repository.findAll().stream()
            .filter(t -> !t.getCivilizationId().equals(civilizationId) && t.getStatus() == TechnologyStatus.COMPLETED)
            .toList();
    }
}
