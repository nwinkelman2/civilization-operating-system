package io.github.opencivilizationplatform.modules.governance.application;

import io.github.opencivilizationplatform.modules.governance.domain.ScientificCommittee;
import io.github.opencivilizationplatform.modules.governance.infrastructure.ScientificCommitteeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ScientificCommitteeServiceTest {

    @Mock
    private ScientificCommitteeRepository repository;

    @InjectMocks
    private ScientificCommitteeService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllCommittees() {
        when(repository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(List.of(new ScientificCommittee())));
        Page<ScientificCommittee> result = service.getAllCommittees(Pageable.unpaged());
        assertEquals(1, result.getContent().size());
        verify(repository).findAll(any(Pageable.class));
    }

    @Test
    void testSaveCommittee() {
        ScientificCommittee committee = new ScientificCommittee();
        committee.setName("Bioethics Council");
        when(repository.save(any(ScientificCommittee.class))).thenReturn(committee);

        ScientificCommittee saved = service.saveCommittee(committee);

        assertEquals("Bioethics Council", saved.getName());
        verify(repository).save(committee);
    }
}
