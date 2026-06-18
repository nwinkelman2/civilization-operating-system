package io.github.opencivilizationplatform.modules.region.domain;

import io.github.opencivilizationplatform.config.seed.CivilizationScale;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.locationtech.jts.geom.Point;

@Entity
@Table(name = "resource_regions")
public class ResourceRegion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotBlank
    private String name;

    @Column(length = 2000)
    private String description;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull
    private CivilizationScale scale;

    @Column(columnDefinition = "geometry(Point, 4326)")
    private Point location;

    // Resource availability 0-100
    @Column(name = "food_availability")
    private Double foodAvailability;
    @Column(name = "water_availability")
    private Double waterAvailability;
    @Column(name = "mineral_availability")
    private Double mineralAvailability;
    @Column(name = "energy_availability")
    private Double energyAvailability;
    @Column(name = "housing_availability")
    private Double housingAvailability;

    @Column(name = "dominant_resource")
    @Enumerated(EnumType.STRING)
    private ResourceType dominantResource;

    @Column(name = "radius_km")
    private Double radiusKm;

    @Column(name = "claimed")
    private Boolean claimed;

    @Column(name = "claimed_by_civilization_id")
    private Long claimedByCivilizationId;

    public ResourceRegion() {}

    // getters + setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public CivilizationScale getScale() { return scale; }
    public void setScale(CivilizationScale scale) { this.scale = scale; }
    public Point getLocation() { return location; }
    public void setLocation(Point location) { this.location = location; }
    public Double getFoodAvailability() { return foodAvailability; }
    public void setFoodAvailability(Double foodAvailability) { this.foodAvailability = foodAvailability; }
    public Double getWaterAvailability() { return waterAvailability; }
    public void setWaterAvailability(Double waterAvailability) { this.waterAvailability = waterAvailability; }
    public Double getMineralAvailability() { return mineralAvailability; }
    public void setMineralAvailability(Double mineralAvailability) { this.mineralAvailability = mineralAvailability; }
    public Double getEnergyAvailability() { return energyAvailability; }
    public void setEnergyAvailability(Double energyAvailability) { this.energyAvailability = energyAvailability; }
    public Double getHousingAvailability() { return housingAvailability; }
    public void setHousingAvailability(Double housingAvailability) { this.housingAvailability = housingAvailability; }
    public ResourceType getDominantResource() { return dominantResource; }
    public void setDominantResource(ResourceType dominantResource) { this.dominantResource = dominantResource; }
    public Double getRadiusKm() { return radiusKm; }
    public void setRadiusKm(Double radiusKm) { this.radiusKm = radiusKm; }
    public Boolean getClaimed() { return claimed; }
    public void setClaimed(Boolean claimed) { this.claimed = claimed; }
    public Long getClaimedByCivilizationId() { return claimedByCivilizationId; }
    public void setClaimedByCivilizationId(Long id) { this.claimedByCivilizationId = id; }

    @PrePersist
    protected void onCreate() {
        if (claimed == null) claimed = false;
    }
}
