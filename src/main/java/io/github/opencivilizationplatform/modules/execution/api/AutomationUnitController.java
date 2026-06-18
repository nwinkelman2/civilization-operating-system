package io.github.opencivilizationplatform.modules.execution.api;

import io.github.opencivilizationplatform.modules.execution.application.AutomationUnitService;
import io.github.opencivilizationplatform.modules.execution.domain.AutomationUnit;
import io.github.opencivilizationplatform.modules.execution.domain.AutomationUnitStatus;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/automation")
public class AutomationUnitController {

    private final AutomationUnitService automationUnitService;

    public AutomationUnitController(AutomationUnitService automationUnitService) {
        this.automationUnitService = automationUnitService;
    }

    @GetMapping
    public Page<AutomationUnit> getAllUnits(Pageable pageable) {
        return automationUnitService.getAllUnits(pageable);
    }

    @PostMapping("/{id}/status")
    public AutomationUnit updateStatus(@PathVariable Long id, @RequestParam AutomationUnitStatus status) {
        return automationUnitService.updateStatus(id, status);
    }

    @PostMapping
    public AutomationUnit saveUnit(@Valid @RequestBody AutomationUnit unit) {
        return automationUnitService.saveUnit(unit);
    }

    @DeleteMapping("/{id}")
    public void deleteUnit(@PathVariable Long id) {
        automationUnitService.deleteUnit(id);
    }
}
