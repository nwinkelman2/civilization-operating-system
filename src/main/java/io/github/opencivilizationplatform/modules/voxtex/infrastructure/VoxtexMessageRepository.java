package io.github.opencivilizationplatform.modules.voxtex.infrastructure;
import io.github.opencivilizationplatform.modules.voxtex.domain.VoxtexMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface VoxtexMessageRepository extends JpaRepository<VoxtexMessage, Long> {
    List<VoxtexMessage> findBySourceNodeIdOrTargetNodeIdOrderBySentAtDesc(Long sourceId, Long targetId);
    List<VoxtexMessage> findByDeliveredFalseOrderBySentAtAsc();
    List<VoxtexMessage> findByTargetNodeIdAndDeliveredFalse(Long targetNodeId);
    long countByTargetNodeIdAndDeliveredFalse(Long targetNodeId);
}
