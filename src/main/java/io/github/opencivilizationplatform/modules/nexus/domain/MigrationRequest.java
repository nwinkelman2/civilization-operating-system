package io.github.opencivilizationplatform.modules.nexus.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.github.opencivilizationplatform.modules.civilization.domain.Civilization;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "migration_requests")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class MigrationRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "citizen_name", nullable = false)
    @NotBlank
    private String citizenName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_civilization_id", nullable = false)
    @NotNull
    private Civilization fromCivilization;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_civilization_id", nullable = false)
    @NotNull
    private Civilization toCivilization;

    @Column(nullable = false)
    @NotBlank
    private String status; // PENDING, APPROVED, REJECTED

    @Column
    private String reason;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public MigrationRequest() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCitizenName() { return citizenName; }
    public void setCitizenName(String citizenName) { this.citizenName = citizenName; }

    public Civilization getFromCivilization() { return fromCivilization; }
    public void setFromCivilization(Civilization fromCivilization) { this.fromCivilization = fromCivilization; }

    public Civilization getToCivilization() { return toCivilization; }
    public void setToCivilization(Civilization toCivilization) { this.toCivilization = toCivilization; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (status == null) status = "PENDING";
    }
}
