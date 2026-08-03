package io.github.opencivilizationplatform.modules.trade.api;

import io.github.opencivilizationplatform.modules.trade.application.TradeService;
import io.github.opencivilizationplatform.modules.trade.domain.TradeAgreement;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping({"/api/v1/trade", "/api/v1/trade/agreements"})
@Tag(name = "Trade", description = "Cross-civilization trade agreements")
public class TradeController {

    private final TradeService service;
    private final io.github.opencivilizationplatform.modules.trade.application.MarketPriceService marketPriceService;

    public TradeController(TradeService service, io.github.opencivilizationplatform.modules.trade.application.MarketPriceService marketPriceService) {
        this.service = service;
        this.marketPriceService = marketPriceService;
    }

    @GetMapping("/prices")
    @Operation(summary = "Get current market resource prices")
    public java.util.Map<String, Double> getPrices() {
        return marketPriceService.getAllPrices();
    }

    @GetMapping("/prices/{resource}/history")
    @Operation(summary = "Get price history for a resource type")
    public List<String> getHistory(@PathVariable String resource) {
        return marketPriceService.getPriceHistory(resource);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Propose a trade agreement")
    public TradeAgreement propose(@Valid @RequestBody TradeRequest request) {
        return service.proposeTrade(request.fromCivId(), request.toCivId(),
            request.resourceType(), request.quantity());
    }

    @GetMapping("/{civId}")
    @Operation(summary = "Get trades for a civilization")
    public List<TradeAgreement> getTrades(@PathVariable Long civId) {
        return service.getTradesForCivilization(civId);
    }

    @GetMapping("/pending/{civId}")
    @Operation(summary = "Get pending trade proposals")
    public List<TradeAgreement> getPending(@PathVariable Long civId) {
        return service.getPendingTrades(civId);
    }

    @PostMapping("/{tradeId}/accept")
    @Operation(summary = "Accept a trade proposal")
    public TradeAgreement accept(@PathVariable Long tradeId) {
        return service.acceptTrade(tradeId);
    }

    @PostMapping("/{tradeId}/complete")
    @Operation(summary = "Complete a trade")
    public TradeAgreement complete(@PathVariable Long tradeId) {
        return service.completeTrade(tradeId);
    }

    @PostMapping("/{tradeId}/cancel")
    @Operation(summary = "Cancel a trade")
    public void cancel(@PathVariable Long tradeId) {
        service.cancelTrade(tradeId);
    }

    @GetMapping
    @Operation(summary = "List all trade agreements")
    public List<TradeAgreement> getAll() {
        return service.getAllTrades();
    }

    @PostMapping("/{tradeId}/reject")
    @Operation(summary = "Reject a trade proposal")
    public TradeAgreement reject(@PathVariable Long tradeId) {
        return service.rejectTrade(tradeId);
    }
}

record TradeRequest(@NotNull Long fromCivId, @NotNull Long toCivId, String resourceType, @NotNull Double quantity) {}
