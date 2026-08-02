package io.github.opencivilizationplatform.modules.nexus.infrastructure;
import io.github.opencivilizationplatform.modules.nexus.domain.NexusMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NexusMessageRepository extends JpaRepository<NexusMessage, Long> {
    List<NexusMessage> findBySourceNodeIdOrTargetNodeIdOrderBySentAtDesc(Long sourceId, Long targetId);
    List<NexusMessage> findByDeliveredFalseOrderBySentAtAsc();
    List<NexusMessage> findByTargetNodeIdAndDeliveredFalse(Long targetNodeId);
    long countByTargetNodeIdAndDeliveredFalse(Long targetNodeId);
}

