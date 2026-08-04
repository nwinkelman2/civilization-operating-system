package io.github.opencivilizationplatform.modules.trade.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class MonetaryEconomyService {

    private static final Logger log = LoggerFactory.getLogger(MonetaryEconomyService.class);

    private double moneySupplyM1 = 1_000_000.0;
    private double interestRate = 0.05; // 5%
    private double inflationIndex = 100.0; // Base 100.0
    private String monetaryPolicy = "NEUTRAL";

    public synchronized double getMoneySupplyM1() {
        return moneySupplyM1;
    }

    public synchronized void setMoneySupplyM1(double moneySupplyM1) {
        this.moneySupplyM1 = Math.max(0.0, moneySupplyM1);
    }

    public synchronized double getInterestRate() {
        return interestRate;
    }

    public synchronized void setInterestRate(double interestRate) {
        this.interestRate = Math.max(0.0, interestRate);
    }

    public synchronized double getInflationIndex() {
        return inflationIndex;
    }

    public synchronized void setInflationIndex(double inflationIndex) {
        this.inflationIndex = Math.max(0.0, inflationIndex);
    }

    public synchronized String getMonetaryPolicy() {
        return monetaryPolicy;
    }

    public synchronized void setMonetaryPolicy(String monetaryPolicy) {
        this.monetaryPolicy = (monetaryPolicy != null && !monetaryPolicy.isBlank()) ? monetaryPolicy : "NEUTRAL";
    }

    public synchronized double adjustMoneySupply(double delta) {
        this.moneySupplyM1 = Math.max(0.0, this.moneySupplyM1 + delta);
        return this.moneySupplyM1;
    }

    public synchronized double adjustInterestRate(double delta) {
        this.interestRate = Math.max(0.0, this.interestRate + delta);
        return this.interestRate;
    }

    public synchronized double adjustInflationIndex(double delta) {
        this.inflationIndex = Math.max(0.0, this.inflationIndex + delta);
        return this.inflationIndex;
    }

    public synchronized void applyMonetaryPolicyAdjustment(String policy, double interestRateChange, double moneySupplyChange) {
        setMonetaryPolicy(policy);
        adjustInterestRate(interestRateChange);
        adjustMoneySupply(moneySupplyChange);
        log.info("Applied Monetary Policy [{}]: interestRate updated to {}, M1 updated to {}",
                this.monetaryPolicy, this.interestRate, this.moneySupplyM1);
    }

    public synchronized void processMonetaryTick() {
        if ("EXPANSIONARY".equalsIgnoreCase(monetaryPolicy)) {
            moneySupplyM1 *= 1.001;
            inflationIndex += 0.05;
        } else if ("CONTRACTIONARY".equalsIgnoreCase(monetaryPolicy)) {
            moneySupplyM1 = Math.max(0.0, moneySupplyM1 * 0.999);
            inflationIndex = Math.max(0.0, inflationIndex - 0.02);
        }
        log.info("[MONETARY ECONOMY TICK] Policy: {}, M1: {}, Interest Rate: {}%, Inflation Index: {}",
                monetaryPolicy, String.format("%.2f", moneySupplyM1), String.format("%.2f", interestRate * 100), String.format("%.2f", inflationIndex));
    }
}
