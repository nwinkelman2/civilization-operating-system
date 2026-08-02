package io.github.opencivilizationplatform.modules.leaderboard.application;

import io.github.opencivilizationplatform.modules.civilization.infrastructure.CivilizationRepository;
import io.github.opencivilizationplatform.modules.leaderboard.domain.CivilizationScore;
import io.github.opencivilizationplatform.modules.technology.infrastructure.TechnologyRepository;
import io.github.opencivilizationplatform.modules.technology.domain.TechnologyStatus;
import io.github.opencivilizationplatform.modules.trade.infrastructure.TradeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
public class LeaderboardService {

    private final CivilizationRepository civRepository;
    private final TechnologyRepository techRepository;
    private final TradeRepository tradeRepository;

    public LeaderboardService(CivilizationRepository civRepository,
                               TechnologyRepository techRepository,
                               TradeRepository tradeRepository) {
        this.civRepository = civRepository;
        this.techRepository = techRepository;
        this.tradeRepository = tradeRepository;
    }

    @Transactional(readOnly = true)
    public List<CivilizationScore> getLeaderboard() {
        return civRepository.findAll().stream()
            .map(civ -> {
                int techCount = techRepository
                    .findByCivilizationIdAndStatus(civ.getId(), TechnologyStatus.COMPLETED)
                    .size();
                int tradeCount = tradeRepository
                    .findByFromCivilizationIdOrToCivilizationId(civ.getId(), civ.getId())
                    .size();
                return new CivilizationScore(
                    civ.getId(), civ.getName(),
                    civ.getReputationScore(), civ.getPopulation(),
                    techCount, tradeCount
                );
            })
            .sorted(Comparator.comparing(CivilizationScore::getTotalScore).reversed())
            .toList();
    }
}
