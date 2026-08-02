package io.github.opencivilizationplatform.modules.production.application;

import io.github.opencivilizationplatform.modules.production.domain.Facility;
import io.github.opencivilizationplatform.modules.production.infrastructure.FacilityRepository;
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

class FacilityServiceTest {

    @Mock
    private FacilityRepository facilityRepository;

    @InjectMocks
    private FacilityService facilityService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllFacilities() {
        Facility f1 = new Facility();
        Facility f2 = new Facility();
        when(facilityRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(Arrays.asList(f1, f2)));

        Page<Facility> result = facilityService.getAllFacilities(Pageable.unpaged());

        assertEquals(2, result.getContent().size());
        verify(facilityRepository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    void testSaveFacility() {
        Facility f = new Facility();
        f.setName("Automated Vertical Farm Alpha");
        when(facilityRepository.save(any(Facility.class))).thenReturn(f);

        Facility saved = facilityService.saveFacility(f);

        assertNotNull(saved);
        assertEquals("Automated Vertical Farm Alpha", saved.getName());
    }
}
