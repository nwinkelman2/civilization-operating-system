package io.github.opencivilizationplatform.modules.strategy.api;

import io.github.opencivilizationplatform.dto.BalanceDTO;
import io.github.opencivilizationplatform.modules.strategy.application.BalanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/strategy")
@Tag(name = "Balance", description = "Balance report endpoints")
public class BalanceController {

    private final BalanceService balanceService;

    public BalanceController(BalanceService balanceService) {
        this.balanceService = balanceService;
    }

    @GetMapping("/balance")
    @Operation(summary = "Get balance report", description = "Returns the current balance report")
    public List<BalanceDTO> getBalance() {
        return balanceService.getBalanceReport();
    }
}
