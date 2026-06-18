package io.github.opencivilizationplatform.modules.voxtex.infrastructure;
import io.github.opencivilizationplatform.modules.voxtex.domain.VoxtexConnection;
import io.github.opencivilizationplatform.modules.voxtex.domain.VoxtexNode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface VoxtexConnectionRepository extends JpaRepository<VoxtexConnection, Long> {
    List<VoxtexConnection> findByNodeAOrNodeB(VoxtexNode nodeA, VoxtexNode nodeB);
    Optional<VoxtexConnection> findByNodeAAndNodeB(VoxtexNode nodeA, VoxtexNode nodeB);
    long countByNodeAOrNodeB(VoxtexNode nodeA, VoxtexNode nodeB);
}
