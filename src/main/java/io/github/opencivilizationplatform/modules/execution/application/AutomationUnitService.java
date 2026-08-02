package io.github.opencivilizationplatform.modules.execution.application;

import io.github.opencivilizationplatform.modules.execution.domain.AutomationUnit;
import io.github.opencivilizationplatform.modules.execution.domain.AutomationUnitStatus;
import io.github.opencivilizationplatform.modules.execution.infrastructure.AutomationUnitRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class AutomationUnitService {

    private final AutomationUnitRepository automationUnitRepository;

    public AutomationUnitService(AutomationUnitRepository automationUnitRepository) {
        this.automationUnitRepository = automationUnitRepository;
    }

    public Page<AutomationUnit> getAllUnits(Pageable pageable) {
        return automationUnitRepository.findAll(pageable);
    }

    public AutomationUnit updateStatus(Long id, AutomationUnitStatus status) {
        AutomationUnit unit = automationUnitRepository.findById(id).orElseThrow();
        unit.setStatus(status);
        return automationUnitRepository.save(unit);
    }

    public AutomationUnit saveUnit(AutomationUnit unit) {
        return automationUnitRepository.save(unit);
    }

    public void deleteUnit(Long id) {
        automationUnitRepository.deleteById(id);
    }
}
