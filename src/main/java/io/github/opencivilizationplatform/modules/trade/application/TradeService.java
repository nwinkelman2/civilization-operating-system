package io.github.opencivilizationplatform.modules.trade.application;

import io.github.opencivilizationplatform.core.eventbus.EventBus;
import io.github.opencivilizationplatform.core.eventbus.events.TradeAgreementCreatedEvent;
import io.github.opencivilizationplatform.modules.trade.domain.TradeAgreement;
import io.github.opencivilizationplatform.modules.trade.domain.TradeStatus;
import io.github.opencivilizationplatform.modules.trade.infrastructure.TradeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TradeService {

    private final TradeRepository repository;
    private final EventBus eventBus;

    public TradeService(TradeRepository repository, EventBus eventBus) {
        this.repository = repository;
        this.eventBus = eventBus;
    }

    @Transactional
    public TradeAgreement proposeTrade(Long fromCivId, Long toCivId, String resourceType, Double quantity) {
        TradeAgreement trade = new TradeAgreement();
        trade.setFromCivilizationId(fromCivId);
        trade.setToCivilizationId(toCivId);
        trade.setResourceType(resourceType);
        trade.setQuantity(quantity);
        trade.setStatus(TradeStatus.PROPOSED);
        trade.setExpiresAt(LocalDateTime.now().plusDays(7));
        TradeAgreement saved = repository.save(trade);

        eventBus.publish(new TradeAgreementCreatedEvent(
            "TradeService",
            saved.getId(),
            saved.getFromCivilizationId(),
            saved.getToCivilizationId(),
            saved.getResourceType(),
            saved.getQuantity() != null ? saved.getQuantity() : 0.0
        ));
        return saved;
    }

    @Transactional
    public TradeAgreement acceptTrade(Long tradeId) {
        TradeAgreement trade = repository.findById(tradeId).orElseThrow();
        trade.setStatus(TradeStatus.ACTIVE);
        return repository.save(trade);
    }

    @Transactional
    public TradeAgreement completeTrade(Long tradeId) {
        TradeAgreement trade = repository.findById(tradeId).orElseThrow();
        trade.setStatus(TradeStatus.COMPLETED);
        return repository.save(trade);
    }

    @Transactional
    public void cancelTrade(Long tradeId) {
        TradeAgreement trade = repository.findById(tradeId).orElseThrow();
        trade.setStatus(TradeStatus.CANCELLED);
        repository.save(trade);
    }

    @Transactional(readOnly = true)
    public List<TradeAgreement> getTradesForCivilization(Long civId) {
        return repository.findByFromCivilizationIdOrToCivilizationId(civId, civId);
    }

    @Transactional(readOnly = true)
    public List<TradeAgreement> getPendingTrades(Long civId) {
        return repository.findByToCivilizationIdAndStatus(civId, TradeStatus.PROPOSED);
    }

    @Transactional(readOnly = true)
    public List<TradeAgreement> getAllTrades() {
        return repository.findAll();
    }

    @Transactional
    public TradeAgreement rejectTrade(Long tradeId) {
        TradeAgreement trade = repository.findById(tradeId).orElseThrow();
        trade.setStatus(TradeStatus.REJECTED);
        return repository.save(trade);
    }
}
