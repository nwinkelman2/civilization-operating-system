package io.github.opencivilizationplatform.modules.logistics.application;

import io.github.opencivilizationplatform.modules.logistics.domain.Shipment;
import io.github.opencivilizationplatform.modules.logistics.infrastructure.ShipmentRepository;
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

class ShipmentServiceTest {

    @Mock
    private ShipmentRepository shipmentRepository;

    @Mock
    private io.github.opencivilizationplatform.core.eventbus.EventBus eventBus;

    @InjectMocks
    private ShipmentService shipmentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllShipments() {
        Shipment s1 = new Shipment();
        Shipment s2 = new Shipment();
        when(shipmentRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(Arrays.asList(s1, s2)));

        Page<Shipment> result = shipmentService.getAllShipments(Pageable.unpaged());

        assertEquals(2, result.getContent().size());
        verify(shipmentRepository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    void testSaveShipment() {
        Shipment s = new Shipment();
        s.setCargo("Medical Supplies");
        s.setStatus(io.github.opencivilizationplatform.modules.logistics.domain.ShipmentStatus.DELIVERED);
        when(shipmentRepository.save(any(Shipment.class))).thenReturn(s);

        Shipment saved = shipmentService.saveShipment(s);

        assertNotNull(saved);
        assertEquals("Medical Supplies", saved.getCargo());
        verify(eventBus, times(1)).publish(any(io.github.opencivilizationplatform.core.eventbus.events.ShipmentDeliveredEvent.class));
    }
}
