package io.github.opencivilizationplatform.modules.social.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "social_cases")
public class Case {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "incident_id")
    private Incident sourceIncident;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull
    private CaseStatus status;

    @Column(columnDefinition = "TEXT")
    private String resolutionPlan;

    @Column(columnDefinition = "TEXT")
    private String rehabilitationProgram;

    @Column(columnDefinition = "TEXT")
    private String monitoringPlan;

    @ElementCollection
    private List<String> panelExpertIds;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Case() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Incident getSourceIncident() { return sourceIncident; }
    public void setSourceIncident(Incident sourceIncident) { this.sourceIncident = sourceIncident; }
    public CaseStatus getStatus() { return status; }
    public void setStatus(CaseStatus status) { this.status = status; }
    public String getResolutionPlan() { return resolutionPlan; }
    public void setResolutionPlan(String resolutionPlan) { this.resolutionPlan = resolutionPlan; }
    public String getRehabilitationProgram() { return rehabilitationProgram; }
    public void setRehabilitationProgram(String rehabilitationProgram) { this.rehabilitationProgram = rehabilitationProgram; }
    public String getMonitoringPlan() { return monitoringPlan; }
    public void setMonitoringPlan(String monitoringPlan) { this.monitoringPlan = monitoringPlan; }
    public List<String> getPanelExpertIds() { return panelExpertIds; }
    public void setPanelExpertIds(List<String> panelExpertIds) { this.panelExpertIds = panelExpertIds; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
