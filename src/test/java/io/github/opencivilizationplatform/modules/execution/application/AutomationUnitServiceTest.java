package io.github.opencivilizationplatform.modules.execution.application;

import io.github.opencivilizationplatform.modules.execution.domain.AutomationUnit;
import io.github.opencivilizationplatform.modules.execution.domain.AutomationUnitStatus;
import io.github.opencivilizationplatform.modules.execution.infrastructure.AutomationUnitRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AutomationUnitServiceTest {

    @Mock
    private AutomationUnitRepository repository;

    @InjectMocks
    private AutomationUnitService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllUnits() {
        when(repository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(List.of(new AutomationUnit(), new AutomationUnit())));
        Page<AutomationUnit> result = service.getAllUnits(Pageable.unpaged());
        assertEquals(2, result.getContent().size());
        verify(repository).findAll(any(Pageable.class));
    }

    @Test
    void testUpdateStatus() {
        AutomationUnit unit = new AutomationUnit();
        unit.setId(1L);
        unit.setStatus(AutomationUnitStatus.IDLE);
        when(repository.findById(1L)).thenReturn(Optional.of(unit));
        when(repository.save(any(AutomationUnit.class))).thenReturn(unit);

        AutomationUnit updated = service.updateStatus(1L, AutomationUnitStatus.ACTIVE);

        assertEquals(AutomationUnitStatus.ACTIVE, updated.getStatus());
        verify(repository).save(unit);
    }

    @Test
    void testSaveUnit() {
        AutomationUnit unit = new AutomationUnit();
        unit.setName("Drone Alpha");
        when(repository.save(any(AutomationUnit.class))).thenReturn(unit);

        AutomationUnit saved = service.saveUnit(unit);

        assertEquals("Drone Alpha", saved.getName());
        verify(repository).save(unit);
    }

    @Test
    void testDeleteUnit() {
        service.deleteUnit(1L);
        verify(repository).deleteById(1L);
    }
}
