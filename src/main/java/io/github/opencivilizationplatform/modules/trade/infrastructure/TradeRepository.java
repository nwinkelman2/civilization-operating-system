package io.github.opencivilizationplatform.modules.trade.infrastructure;
import io.github.opencivilizationplatform.modules.trade.domain.TradeAgreement;
import io.github.opencivilizationplatform.modules.trade.domain.TradeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TradeRepository extends JpaRepository<TradeAgreement, Long> {
    List<TradeAgreement> findByFromCivilizationIdOrToCivilizationId(Long from, Long to);
    List<TradeAgreement> findByFromCivilizationIdAndStatus(Long from, TradeStatus status);
    List<TradeAgreement> findByToCivilizationIdAndStatus(Long to, TradeStatus status);
}
