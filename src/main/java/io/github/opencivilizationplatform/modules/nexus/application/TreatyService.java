package io.github.opencivilizationplatform.modules.nexus.application;

import io.github.opencivilizationplatform.modules.nexus.domain.Treaty;
import io.github.opencivilizationplatform.modules.nexus.domain.TreatyStatus;
import io.github.opencivilizationplatform.modules.nexus.domain.TreatyType;
import io.github.opencivilizationplatform.modules.nexus.infrastructure.TreatyRepository;
import io.github.opencivilizationplatform.modules.civilization.domain.Civilization;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Arrays;

@Service
public class TreatyService {

    private final TreatyRepository treatyRepository;

    public TreatyService(TreatyRepository treatyRepository) {
        this.treatyRepository = treatyRepository;
    }

    @Transactional
    public Treaty proposeTreaty(String title, TreatyType type, Long proposerCivId, List<Long> invitedCivIds) {
        Treaty treaty = new Treaty();
        treaty.setTitle(title);
        treaty.setType(type);
        treaty.setProposerCivId(proposerCivId);
        String invitedJson = "[" + invitedCivIds.stream().map(String::valueOf).collect(Collectors.joining(",")) + "]";
        treaty.setInvitedCivIds(invitedJson);
        treaty.setSignatoryCivIds("[" + proposerCivId + "]");
        treaty.setStatus(TreatyStatus.PROPOSED);
        treaty.setExpiresAt(LocalDateTime.now().plusDays(7));
        return treatyRepository.save(treaty);
    }

    @Transactional
    public Treaty signTreaty(Long treatyId, Long civId) {
        Treaty treaty = treatyRepository.findById(treatyId)
            .orElseThrow(() -> new IllegalArgumentException("Treaty not found: " + treatyId));

        if (treaty.getStatus() != TreatyStatus.PROPOSED) {
            throw new IllegalStateException("Treaty is not open for signatures.");
        }

        String current = treaty.getSignatoryCivIds();
        if (current == null || current.equals("[]")) {
            treaty.setSignatoryCivIds("[" + civId + "]");
        } else {
            String updated = current.substring(0, current.length() - 1) + "," + civId + "]";
            treaty.setSignatoryCivIds(updated);
        }

        List<Long> invited = parseIds(treaty.getInvitedCivIds());
        List<Long> signatories = parseIds(treaty.getSignatoryCivIds());
        if (signatories.containsAll(invited) && signatories.contains(treaty.getProposerCivId())) {
            treaty.setStatus(TreatyStatus.ACTIVE);
        }

        return treatyRepository.save(treaty);
    }

    public List<Treaty> getActiveTreaties() {
        return treatyRepository.findByStatus(TreatyStatus.ACTIVE);
    }

    public List<Treaty> getTreatiesForCiv(Long civId) {
        return treatyRepository.findByProposerCivIdOrInvitedCivIdsContaining(civId, String.valueOf(civId));
    }

    public boolean isCivSignatory(Treaty treaty, Long civId) {
        if (treaty.getSignatoryCivIds() == null) return false;
        return parseIds(treaty.getSignatoryCivIds()).contains(civId);
    }

    public double[] computeTreatyModifiers(Long civId) {
        double scienceBonus = 0;
        double tradeMult = 1.0;
        double repDelta = 0;
        double scienceBotMult = 1.0;

        for (Treaty treaty : getActiveTreaties()) {
            if (!isCivSignatory(treaty, civId)) continue;
            switch (treaty.getType()) {
                case FREE_TRADE -> tradeMult += 0.15;
                case NON_AGGRESSION -> repDelta += 2.0;
                case RESEARCH_ALLIANCE -> {
                    scienceBotMult *= 2.0;
                    scienceBonus += 0.5;
                }
            }
        }
        return new double[]{scienceBonus, tradeMult, repDelta, scienceBotMult};
    }

    private List<Long> parseIds(String json) {
        if (json == null || json.equals("[]") || json.isBlank()) return List.of();
        String inner = json.replace("[", "").replace("]", "").trim();
        if (inner.isEmpty()) return List.of();
        return Arrays.stream(inner.split(","))
            .map(String::trim)
            .filter(s -> !s.isEmpty())
            .map(Long::parseLong)
            .collect(Collectors.toList());
    }
}
