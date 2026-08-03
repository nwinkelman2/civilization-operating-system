package io.github.opencivilizationplatform.modules.strategy.application;

import io.github.opencivilizationplatform.modules.civilization.domain.Civilization;
import io.github.opencivilizationplatform.modules.civilization.domain.CivilizationStatus;
import io.github.opencivilizationplatform.modules.civilization.infrastructure.CivilizationRepository;
import io.github.opencivilizationplatform.modules.nexus.infrastructure.NexusNodeRepository;
import io.github.opencivilizationplatform.modules.participation.infrastructure.RuleRepository;
import io.github.opencivilizationplatform.modules.strategy.domain.EmergentArchetypeReport;
import io.github.opencivilizationplatform.modules.trade.infrastructure.TradeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class EmergentCivilizationSearchService {

    private final CivilizationRepository civilizationRepository;
    private final NexusNodeRepository nodeRepository;
    private final TradeRepository tradeRepository;
    private final RuleRepository ruleRepository;

    public EmergentCivilizationSearchService(CivilizationRepository civilizationRepository,
                                            NexusNodeRepository nodeRepository,
                                            TradeRepository tradeRepository,
                                            RuleRepository ruleRepository) {
        this.civilizationRepository = civilizationRepository;
        this.nodeRepository = nodeRepository;
        this.tradeRepository = tradeRepository;
        this.ruleRepository = ruleRepository;
    }

    public List<EmergentArchetypeReport> evaluateEmergentArchetypes() {
        List<Civilization> activeCivs = civilizationRepository.findByStatus(CivilizationStatus.ACTIVE);
        if (activeCivs == null || activeCivs.isEmpty()) {
            activeCivs = civilizationRepository.findAll().stream()
                    .filter(c -> c.getStatus() != CivilizationStatus.FALLEN)
                    .toList();
        }

        List<EmergentArchetypeReport> reports = new ArrayList<>();
        for (Civilization civ : activeCivs) {
            long nodeCount = nodeRepository.findByCivilizationId(civ.getId()).size();
            long tradeCount = tradeRepository.findByFromCivilizationIdOrToCivilizationId(civ.getId(), civ.getId()).size();
            long ruleCount = ruleRepository.findByCivilizationId(civ.getId()).size();
            double foodReserves = civ.getFood() != null ? civ.getFood() : 0.0;
            int sciencePriority = civ.getScienceBotsPriority() != null ? civ.getScienceBotsPriority() : 0;
            int agriPriority = civ.getAgriBotsPriority() != null ? civ.getAgriBotsPriority() : 0;

            double meshScore = nodeCount * 25.0 + sciencePriority * 0.5;
            double traderScore = tradeCount * 30.0;
            double directorateScore = ruleCount * 25.0 + sciencePriority * 0.8;
            double agrarianScore = (foodReserves / 10.0) + agriPriority * 0.5;

            String archetype;
            String keyFeature;
            double emergenceScore;

            if (meshScore >= traderScore && meshScore >= directorateScore && meshScore >= agrarianScore && nodeCount > 0) {
                archetype = "TECHNOCRATIC_MESH";
                keyFeature = "High Mesh Connectivity & Autonomous Nodes";
                emergenceScore = Math.min(100.0, Math.max(20.0, meshScore));
            } else if (traderScore >= directorateScore && traderScore >= agrarianScore && tradeCount > 0) {
                archetype = "HIGH_VELOCITY_TRADER";
                keyFeature = "High Velocity Trade Network";
                emergenceScore = Math.min(100.0, Math.max(20.0, traderScore));
            } else if (directorateScore >= agrarianScore && ruleCount > 0) {
                archetype = "SCIENTIFIC_DIRECTORATE";
                keyFeature = "Constitutional Rules & Scientific Governance";
                emergenceScore = Math.min(100.0, Math.max(20.0, directorateScore));
            } else {
                archetype = "DECENTRALIZED_AGRARIAN";
                keyFeature = "Food Security & Decentralized Agrarian Base";
                emergenceScore = Math.min(100.0, Math.max(15.0, agrarianScore));
            }

            emergenceScore = Math.round(emergenceScore * 10.0) / 10.0;

            reports.add(new EmergentArchetypeReport(
                    archetype,
                    civ.getId(),
                    civ.getName(),
                    emergenceScore,
                    keyFeature
            ));
        }

        return reports;
    }
}
