package io.github.opencivilizationplatform.modules.diplomacy.application;

import io.github.opencivilizationplatform.modules.diplomacy.domain.DiplomaticRelation;
import io.github.opencivilizationplatform.modules.diplomacy.domain.DiplomaticStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class DiplomacyEngineService {

    private static final Logger log = LoggerFactory.getLogger(DiplomacyEngineService.class);

    public static final double WAR_THRESHOLD = 80.0;
    public static final double HOSTILE_THRESHOLD = 60.0;
    public static final double NEUTRAL_THRESHOLD = 40.0;
    public static final double NON_AGGRESSION_THRESHOLD = 20.0;

    private final ConcurrentMap<String, DiplomaticRelation> relationStore = new ConcurrentHashMap<>();

    private String buildKey(String sourceCivId, String targetCivId) {
        return sourceCivId + ":" + targetCivId;
    }

    public DiplomaticStatus determineStatusForTension(double tensionIndex) {
        if (tensionIndex >= WAR_THRESHOLD) {
            return DiplomaticStatus.WAR;
        } else if (tensionIndex >= HOSTILE_THRESHOLD) {
            return DiplomaticStatus.HOSTILE;
        } else if (tensionIndex >= NEUTRAL_THRESHOLD) {
            return DiplomaticStatus.NEUTRAL;
        } else if (tensionIndex >= NON_AGGRESSION_THRESHOLD) {
            return DiplomaticStatus.NON_AGGRESSION_PACT;
        } else {
            return DiplomaticStatus.ALLIED;
        }
    }

    public DiplomaticRelation evaluateTension(DiplomaticRelation relation, double tensionDelta) {
        if (relation == null) {
            return null;
        }
        double newTension = relation.tensionIndex() + tensionDelta;
        DiplomaticStatus autoStatus = determineStatusForTension(newTension);
        DiplomaticRelation updated = new DiplomaticRelation(
            relation.sourceCivId(),
            relation.targetCivId(),
            autoStatus,
            newTension
        );
        relationStore.put(buildKey(relation.sourceCivId(), relation.targetCivId()), updated);
        log.info("Evaluated tension between {} and {}: new tension = {}, status = {}",
            relation.sourceCivId(), relation.targetCivId(), updated.tensionIndex(), updated.status());
        return updated;
    }

    public DiplomaticRelation updateDiplomaticStatus(DiplomaticRelation relation, DiplomaticStatus newStatus) {
        if (relation == null || newStatus == null) {
            return relation;
        }
        DiplomaticRelation updated = relation.withStatus(newStatus);
        relationStore.put(buildKey(relation.sourceCivId(), relation.targetCivId()), updated);
        log.info("Updated diplomatic status between {} and {} to {}",
            relation.sourceCivId(), relation.targetCivId(), newStatus);
        return updated;
    }

    public DiplomaticRelation proposeAlliance(DiplomaticRelation relation) {
        if (relation == null) {
            return null;
        }
        if (relation.tensionIndex() <= 30.0) {
            DiplomaticRelation alliance = new DiplomaticRelation(
                relation.sourceCivId(),
                relation.targetCivId(),
                DiplomaticStatus.ALLIED,
                Math.min(relation.tensionIndex(), 15.0)
            );
            relationStore.put(buildKey(relation.sourceCivId(), relation.targetCivId()), alliance);
            log.info("Alliance accepted between {} and {}", relation.sourceCivId(), relation.targetCivId());
            return alliance;
        } else {
            log.info("Alliance proposal rejected between {} and {} due to high tension ({})",
                relation.sourceCivId(), relation.targetCivId(), relation.tensionIndex());
            return relation;
        }
    }

    public DiplomaticRelation signPeaceAgreement(DiplomaticRelation relation) {
        if (relation == null) {
            return null;
        }
        double reducedTension = Math.min(relation.tensionIndex(), 35.0);
        DiplomaticRelation peaceRelation = new DiplomaticRelation(
            relation.sourceCivId(),
            relation.targetCivId(),
            DiplomaticStatus.NEUTRAL,
            reducedTension
        );
        relationStore.put(buildKey(relation.sourceCivId(), relation.targetCivId()), peaceRelation);
        log.info("Peace agreement signed between {} and {}, tension reduced to {}",
            relation.sourceCivId(), relation.targetCivId(), reducedTension);
        return peaceRelation;
    }

    public DiplomaticRelation signNonAggressionPact(DiplomaticRelation relation) {
        if (relation == null) {
            return null;
        }
        if (relation.tensionIndex() <= 50.0) {
            DiplomaticRelation updated = new DiplomaticRelation(
                relation.sourceCivId(),
                relation.targetCivId(),
                DiplomaticStatus.NON_AGGRESSION_PACT,
                Math.min(relation.tensionIndex(), 30.0)
            );
            relationStore.put(buildKey(relation.sourceCivId(), relation.targetCivId()), updated);
            log.info("Non-Aggression Pact signed between {} and {}", relation.sourceCivId(), relation.targetCivId());
            return updated;
        } else {
            log.info("Non-Aggression Pact rejected between {} and {} (tension = {})",
                relation.sourceCivId(), relation.targetCivId(), relation.tensionIndex());
            return relation;
        }
    }

    public List<DiplomaticRelation> processDiplomacyCycle() {
        log.info("Processing diplomacy cycle across {} active relations...", relationStore.size());
        List<DiplomaticRelation> results = new ArrayList<>();
        for (DiplomaticRelation relation : relationStore.values()) {
            DiplomaticRelation evaluated = evaluateTension(relation, 0.0);
            results.add(evaluated);
        }
        return results;
    }

    public DiplomaticRelation getRelation(String sourceCivId, String targetCivId) {
        return relationStore.get(buildKey(sourceCivId, targetCivId));
    }

    public void registerRelation(DiplomaticRelation relation) {
        if (relation != null) {
            relationStore.put(buildKey(relation.sourceCivId(), relation.targetCivId()), relation);
        }
    }
}
