package io.github.opencivilizationplatform.modules.production.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "production_facilities")
public class Facility {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotBlank
    private String name;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull
    private FacilityType type;

    @Column(nullable = false)
    @NotBlank
    private String region;

    @Column(nullable = false)
    private Double efficiency;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull
    private FacilityStatus status;

    private String currentOutput;

    public Facility() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public FacilityType getType() { return type; }
    public void setType(FacilityType type) { this.type = type; }
    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }
    public Double getEfficiency() { return efficiency; }
    public void setEfficiency(Double efficiency) { this.efficiency = efficiency; }
    public FacilityStatus getStatus() { return status; }
    public void setStatus(FacilityStatus status) { this.status = status; }
    public String getCurrentOutput() { return currentOutput; }
    public void setCurrentOutput(String currentOutput) { this.currentOutput = currentOutput; }
}
