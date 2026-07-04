package io.github.opencivilizationplatform.modules.technology.domain;

import io.github.opencivilizationplatform.modules.civilization.domain.Civilization;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "licensed_technologies")
public class LicensedTechnology {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "licensor_id", nullable = false)
    private Civilization licensor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "licensee_id", nullable = false)
    private Civilization licensee;

    @Column(name = "tech_name", nullable = false)
    @NotBlank
    private String techName;

    @Column(name = "fee_per_tick", nullable = false)
    @NotNull
    private Double feePerTick;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public LicensedTechnology() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Civilization getLicensor() { return licensor; }
    public void setLicensor(Civilization licensor) { this.licensor = licensor; }

    public Civilization getLicensee() { return licensee; }
    public void setLicensee(Civilization licensee) { this.licensee = licensee; }

    public String getTechName() { return techName; }
    public void setTechName(String techName) { this.techName = techName; }

    public Double getFeePerTick() { return feePerTick; }
    public void setFeePerTick(Double feePerTick) { this.feePerTick = feePerTick; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
