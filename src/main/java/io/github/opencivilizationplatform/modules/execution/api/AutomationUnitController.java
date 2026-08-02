package io.github.opencivilizationplatform.modules.execution.api;

import io.github.opencivilizationplatform.modules.execution.application.AutomationUnitService;
import io.github.opencivilizationplatform.modules.execution.domain.AutomationUnit;
import io.github.opencivilizationplatform.modules.execution.domain.AutomationUnitStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/automation")
@Tag(name = "Automation Units", description = "Automation unit management endpoints")
public class AutomationUnitController {

    private final AutomationUnitService automationUnitService;

    public AutomationUnitController(AutomationUnitService automationUnitService) {
        this.automationUnitService = automationUnitService;
    }

    @GetMapping
    @Operation(summary = "List all automation units", description = "Returns a paginated list of automation units")
    public Page<AutomationUnit> getAllUnits(Pageable pageable) {
        return automationUnitService.getAllUnits(pageable);
    }

    @PostMapping("/{id}/status")
    @Operation(summary = "Update automation unit status", description = "Updates the status of an automation unit")
    public AutomationUnit updateStatus(@PathVariable Long id, @RequestParam AutomationUnitStatus status) {
        return automationUnitService.updateStatus(id, status);
    }

    @PostMapping
    @Operation(summary = "Create an automation unit", description = "Creates a new automation unit")
    public AutomationUnit saveUnit(@Valid @RequestBody AutomationUnit unit) {
        return automationUnitService.saveUnit(unit);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an automation unit", description = "Deletes an automation unit by ID")
    public void deleteUnit(@PathVariable Long id) {
        automationUnitService.deleteUnit(id);
    }
}
