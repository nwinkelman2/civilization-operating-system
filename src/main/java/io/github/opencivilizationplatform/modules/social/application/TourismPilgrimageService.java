package io.github.opencivilizationplatform.modules.social.application;

import io.github.opencivilizationplatform.modules.social.domain.Landmark;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TourismPilgrimageService {

    private static final Logger log = LoggerFactory.getLogger(TourismPilgrimageService.class);
    private final Map<String, Landmark> landmarkMap = new ConcurrentHashMap<>();

    public Landmark registerLandmark(String landmarkId, String name, String regionId, double attractionPower) {
        Landmark landmark = new Landmark(landmarkId, name, regionId, attractionPower);
        landmarkMap.put(landmarkId, landmark);
        log.info("Registered landmark: {} in region {} with attraction power {}", name, regionId, attractionPower);
        return landmark;
    }

    public Landmark registerLandmark(Landmark landmark) {
        if (landmark == null || landmark.landmarkId() == null) {
            throw new IllegalArgumentException("Landmark and landmarkId must not be null");
        }
        landmarkMap.put(landmark.landmarkId(), landmark);
        log.info("Registered landmark: {} in region {}", landmark.name(), landmark.regionId());
        return landmark;
    }

    public Optional<Landmark> getLandmarkById(String landmarkId) {
        if (landmarkId == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(landmarkMap.get(landmarkId));
    }

    public List<Landmark> getAllLandmarks() {
        return new ArrayList<>(landmarkMap.values());
    }

    public List<Landmark> getLandmarksByRegion(String regionId) {
        if (regionId == null) {
            return Collections.emptyList();
        }
        return landmarkMap.values().stream()
                .filter(l -> regionId.equalsIgnoreCase(l.regionId()))
                .toList();
    }

    public double calculateTotalAttractionPower(String regionId) {
        return getLandmarksByRegion(regionId).stream()
                .mapToDouble(Landmark::attractionPower)
                .sum();
    }

    public double calculateTotalAttractionPower() {
        return landmarkMap.values().stream()
                .mapToDouble(Landmark::attractionPower)
                .sum();
    }

    public int calculateTouristInflux(String regionId, double baseMultiplier) {
        double totalAttraction = calculateTotalAttractionPower(regionId);
        return (int) Math.round(totalAttraction * baseMultiplier);
    }

    public int calculateTouristInflux(String regionId) {
        return calculateTouristInflux(regionId, 1.5);
    }

    public int calculateTotalTouristInflux() {
        double totalAttraction = calculateTotalAttractionPower();
        return (int) Math.round(totalAttraction * 1.5);
    }

    public void processTourismTick() {
        double totalAttraction = calculateTotalAttractionPower();
        int totalInflux = calculateTotalTouristInflux();
        log.info("[TOURISM TICK] Active Landmarks: {}, Total Attraction Power: {}, Tourist Influx: {}",
                landmarkMap.size(), totalAttraction, totalInflux);
    }
}
