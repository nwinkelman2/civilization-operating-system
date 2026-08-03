package io.github.opencivilizationplatform.modules.trade.application;

import io.github.opencivilizationplatform.core.eventbus.EventBus;
import io.github.opencivilizationplatform.core.eventbus.events.TradeAgreementCreatedEvent;
import io.github.opencivilizationplatform.modules.trade.domain.TradeAgreement;
import io.github.opencivilizationplatform.modules.trade.domain.TradeStatus;
import io.github.opencivilizationplatform.modules.trade.infrastructure.TradeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TradeServiceTest {

    @Mock
    private TradeRepository tradeRepository;

    @Mock
    private EventBus eventBus;

    @InjectMocks
    private TradeService tradeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testProposeTradePublishesEvent() {
        when(tradeRepository.save(any(TradeAgreement.class))).thenAnswer(inv -> {
            TradeAgreement ta = inv.getArgument(0);
            ta.setId(10L);
            return ta;
        });

        TradeAgreement result = tradeService.proposeTrade(1L, 2L, "FOOD", 50.0);

        assertNotNull(result);
        assertEquals(TradeStatus.PROPOSED, result.getStatus());
        verify(eventBus, times(1)).publish(any(TradeAgreementCreatedEvent.class));
    }
}
