package io.github.opencivilizationplatform.modules.trade;

import io.github.opencivilizationplatform.modules.trade.application.MonetaryEconomyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MonetaryEconomyServiceTest {

    private MonetaryEconomyService monetaryEconomyService;

    @BeforeEach
    void setUp() {
        monetaryEconomyService = new MonetaryEconomyService();
    }

    @Test
    void testInitialState() {
        assertEquals(1_000_000.0, monetaryEconomyService.getMoneySupplyM1());
        assertEquals(0.05, monetaryEconomyService.getInterestRate());
        assertEquals(100.0, monetaryEconomyService.getInflationIndex());
        assertEquals("NEUTRAL", monetaryEconomyService.getMonetaryPolicy());
    }

    @Test
    void testAdjustments() {
        monetaryEconomyService.adjustMoneySupply(500_000.0);
        assertEquals(1_500_000.0, monetaryEconomyService.getMoneySupplyM1());

        monetaryEconomyService.adjustInterestRate(0.02);
        assertEquals(0.07, monetaryEconomyService.getInterestRate(), 0.0001);

        monetaryEconomyService.adjustInflationIndex(2.5);
        assertEquals(102.5, monetaryEconomyService.getInflationIndex());
    }

    @Test
    void testApplyMonetaryPolicyAdjustment() {
        monetaryEconomyService.applyMonetaryPolicyAdjustment("EXPANSIONARY", -0.01, 200_000.0);
        assertEquals("EXPANSIONARY", monetaryEconomyService.getMonetaryPolicy());
        assertEquals(0.04, monetaryEconomyService.getInterestRate(), 0.0001);
        assertEquals(1_200_000.0, monetaryEconomyService.getMoneySupplyM1());
    }

    @Test
    void testProcessMonetaryTickExpansionary() {
        monetaryEconomyService.setMonetaryPolicy("EXPANSIONARY");
        double initialM1 = monetaryEconomyService.getMoneySupplyM1();
        double initialInflation = monetaryEconomyService.getInflationIndex();

        monetaryEconomyService.processMonetaryTick();

        assertTrue(monetaryEconomyService.getMoneySupplyM1() > initialM1);
        assertTrue(monetaryEconomyService.getInflationIndex() > initialInflation);
    }

    @Test
    void testProcessMonetaryTickContractionary() {
        monetaryEconomyService.setMonetaryPolicy("CONTRACTIONARY");
        double initialM1 = monetaryEconomyService.getMoneySupplyM1();
        double initialInflation = monetaryEconomyService.getInflationIndex();

        monetaryEconomyService.processMonetaryTick();

        assertTrue(monetaryEconomyService.getMoneySupplyM1() < initialM1);
        assertTrue(monetaryEconomyService.getInflationIndex() < initialInflation);
    }
}
