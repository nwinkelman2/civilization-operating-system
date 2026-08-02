package io.github.opencivilizationplatform.modules.social.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "social_incidents")
public class Incident {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull
    private IncidentType type;

    @Column(nullable = false)
    @NotBlank
    private String location;

    @Column(nullable = false, length = 1000)
    @NotBlank
    private String description;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull
    private RiskLevel riskLevel;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull
    private IncidentStatus status;

    @ElementCollection
    private List<String> participantIds;

    @Column(name = "reported_at")
    private LocalDateTime reportedAt;

    @Column(name = "assigned_eco_bots")
    private Integer assignedEcoBots = 0;

    @Column(name = "assigned_security_bots")
    private Integer assignedSecurityBots = 0;

    @Column(name = "severity")
    private Double severity = 100.0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "civilization_id")
    private io.github.opencivilizationplatform.modules.civilization.domain.Civilization civilization;

    public io.github.opencivilizationplatform.modules.civilization.domain.Civilization getCivilization() { return civilization; }
    public void setCivilization(io.github.opencivilizationplatform.modules.civilization.domain.Civilization civilization) { this.civilization = civilization; }

    public Incident() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public IncidentType getType() { return type; }
    public void setType(IncidentType type) { this.type = type; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public RiskLevel getRiskLevel() { return riskLevel; }
    public void setRiskLevel(RiskLevel riskLevel) { this.riskLevel = riskLevel; }
    public IncidentStatus getStatus() { return status; }
    public void setStatus(IncidentStatus status) { this.status = status; }
    public List<String> getParticipantIds() { return participantIds; }
    public void setParticipantIds(List<String> participantIds) { this.participantIds = participantIds; }
    public LocalDateTime getReportedAt() { return reportedAt; }
    public void setReportedAt(LocalDateTime reportedAt) { this.reportedAt = reportedAt; }

    public Integer getAssignedEcoBots() { return assignedEcoBots == null ? 0 : assignedEcoBots; }
    public void setAssignedEcoBots(Integer assignedEcoBots) { this.assignedEcoBots = assignedEcoBots; }
    public Integer getAssignedSecurityBots() { return assignedSecurityBots == null ? 0 : assignedSecurityBots; }
    public void setAssignedSecurityBots(Integer assignedSecurityBots) { this.assignedSecurityBots = assignedSecurityBots; }
    public Double getSeverity() { return severity == null ? 100.0 : severity; }
    public void setSeverity(Double severity) { this.severity = severity; }

    @PrePersist
    protected void onCreate() {
        reportedAt = LocalDateTime.now();
    }
}
