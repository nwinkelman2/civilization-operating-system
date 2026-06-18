package io.github.opencivilizationplatform.modules.voxtex.infrastructure;
import io.github.opencivilizationplatform.modules.voxtex.domain.VoxtexNode;
import io.github.opencivilizationplatform.modules.voxtex.domain.VoxtexNodeStatus;
import io.github.opencivilizationplatform.modules.voxtex.domain.VoxtexNodeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface VoxtexNodeRepository extends JpaRepository<VoxtexNode, Long> {
    List<VoxtexNode> findByCivilizationId(Long civilizationId);
    List<VoxtexNode> findByStatus(VoxtexNodeStatus status);
    List<VoxtexNode> findByCivilizationIdAndType(Long civilizationId, VoxtexNodeType type);
}
