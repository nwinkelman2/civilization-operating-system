package io.github.opencivilizationplatform.modules.social;

import io.github.opencivilizationplatform.modules.social.application.TourismPilgrimageService;
import io.github.opencivilizationplatform.modules.social.domain.Landmark;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class TourismPilgrimageServiceTest {

    private TourismPilgrimageService tourismPilgrimageService;

    @BeforeEach
    void setUp() {
        tourismPilgrimageService = new TourismPilgrimageService();
    }

    @Test
    void testRegisterLandmark() {
        Landmark landmark = tourismPilgrimageService.registerLandmark("landmark-1", "Ancient Temple", "region-A", 150.0);
        assertNotNull(landmark);
        assertEquals("landmark-1", landmark.landmarkId());
        assertEquals("Ancient Temple", landmark.name());
        assertEquals("region-A", landmark.regionId());
        assertEquals(150.0, landmark.attractionPower());

        Optional<Landmark> fetched = tourismPilgrimageService.getLandmarkById("landmark-1");
        assertTrue(fetched.isPresent());
    }

    @Test
    void testCalculateAttractionPowerAndTouristInflux() {
        tourismPilgrimageService.registerLandmark("lm-1", "Great Pyramid", "region-1", 100.0);
        tourismPilgrimageService.registerLandmark("lm-2", "Sacred Shrine", "region-1", 50.0);
        tourismPilgrimageService.registerLandmark("lm-3", "Coastal Citadel", "region-2", 80.0);

        double region1Attraction = tourismPilgrimageService.calculateTotalAttractionPower("region-1");
        assertEquals(150.0, region1Attraction);

        double totalAttraction = tourismPilgrimageService.calculateTotalAttractionPower();
        assertEquals(230.0, totalAttraction);

        int influxRegion1 = tourismPilgrimageService.calculateTouristInflux("region-1", 2.0);
        assertEquals(300, influxRegion1);

        int totalInflux = tourismPilgrimageService.calculateTotalTouristInflux();
        assertEquals(345, totalInflux); // 230 * 1.5 = 345
    }

    @Test
    void testProcessTourismTick() {
        tourismPilgrimageService.registerLandmark("lm-1", "Wonder Tower", "region-1", 100.0);
        assertDoesNotThrow(() -> tourismPilgrimageService.processTourismTick());
    }
}
