package io.github.opencivilizationplatform.modules.needs.application;

import io.github.opencivilizationplatform.modules.needs.domain.Need;
import io.github.opencivilizationplatform.modules.needs.infrastructure.NeedRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class NeedServiceTest {

    @Mock
    private NeedRepository needRepository;

    @InjectMocks
    private NeedService needService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllNeeds() {
        Need need1 = new Need();
        Need need2 = new Need();
        when(needRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(Arrays.asList(need1, need2)));

        Page<Need> result = needService.getAllNeeds(Pageable.unpaged());

        assertEquals(2, result.getContent().size());
        verify(needRepository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    void testSaveNeed() {
        Need need = new Need();
        need.setDescription("Clean Water Access");
        when(needRepository.save(any(Need.class))).thenReturn(need);

        Need saved = needService.saveNeed(need);

        assertNotNull(saved);
        assertEquals("Clean Water Access", saved.getDescription());
        verify(needRepository, times(1)).save(need);
    }
}
