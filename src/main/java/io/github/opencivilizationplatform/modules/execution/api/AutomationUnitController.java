package io.github.opencivilizationplatform.modules.execution.api;

import io.github.opencivilizationplatform.modules.execution.domain.AutomationUnit;
import io.github.opencivilizationplatform.modules.execution.domain.AutomationUnitStatus;
import io.github.opencivilizationplatform.modules.execution.infrastructure.AutomationUnitRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/automation")
public class AutomationUnitController {

    private final AutomationUnitRepository automationUnitRepository;

    public AutomationUnitController(AutomationUnitRepository automationUnitRepository) {
        this.automationUnitRepository = automationUnitRepository;
    }

    @GetMapping
    public List<AutomationUnit> getAllUnits() {
        return automationUnitRepository.findAll();
    }

    @PostMapping("/{id}/status")
    public AutomationUnit updateStatus(@PathVariable Long id, @RequestParam AutomationUnitStatus status) {
        AutomationUnit unit = automationUnitRepository.findById(id).orElseThrow();
        unit.setStatus(status);
        return automationUnitRepository.save(unit);
    }

    @PostMapping
    public AutomationUnit saveUnit(@RequestBody AutomationUnit unit) {
        return automationUnitRepository.save(unit);
    }

    @DeleteMapping("/{id}")
    public void deleteUnit(@PathVariable Long id) {
        automationUnitRepository.deleteById(id);
    }
}
