package io.github.opencivilizationplatform.modules.diplomacy.domain;

public record DiplomaticRelation(
    String sourceCivId,
    String targetCivId,
    DiplomaticStatus status,
    double tensionIndex
) {
    public DiplomaticRelation {
        if (tensionIndex < 0.0) {
            tensionIndex = 0.0;
        } else if (tensionIndex > 100.0) {
            tensionIndex = 100.0;
        }
        if (status == null) {
            status = DiplomaticStatus.NEUTRAL;
        }
    }

    public DiplomaticRelation withTension(double newTensionIndex) {
        return new DiplomaticRelation(sourceCivId, targetCivId, status, newTensionIndex);
    }

    public DiplomaticRelation withStatus(DiplomaticStatus newStatus) {
        return new DiplomaticRelation(sourceCivId, targetCivId, newStatus, tensionIndex);
    }
}
