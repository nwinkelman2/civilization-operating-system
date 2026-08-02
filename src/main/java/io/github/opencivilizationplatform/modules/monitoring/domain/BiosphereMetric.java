package io.github.opencivilizationplatform.modules.monitoring.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "biosphere_metrics")
public class BiosphereMetric {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotBlank
    private String name;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    @Column(name = "metric_value", nullable = false)
    private Double value;

    @Column(nullable = false)
    @NotBlank
    private String unit;

    @Column(nullable = false)
    private Double safetyLimit;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull
    private BiosphereMetricStatus status;

    @Column(nullable = false)
    private Double drift;

    public BiosphereMetric() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public LocalDateTime getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }
    public Double getValue() { return value; }
    public void setValue(Double value) { this.value = value; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    public Double getSafetyLimit() { return safetyLimit; }
    public void setSafetyLimit(Double safetyLimit) { this.safetyLimit = safetyLimit; }
    public BiosphereMetricStatus getStatus() { return status; }
    public void setStatus(BiosphereMetricStatus status) { this.status = status; }
    public Double getDrift() { return drift; }
    public void setDrift(Double drift) { this.drift = drift; }

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        lastUpdated = LocalDateTime.now();
    }
}
