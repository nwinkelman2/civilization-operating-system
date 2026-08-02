package io.github.opencivilizationplatform.modules.social.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Entity
@Table(name = "behavior_assessments")
public class BehaviorAssessment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotBlank
    private String citizenId;

    @Column(columnDefinition = "TEXT")
    private String psychologicalProfile;

    private Double riskScore;

    @Column(columnDefinition = "TEXT")
    private String socialFactors;

    @Column(name = "assessed_at")
    private LocalDateTime assessedAt;

    public BehaviorAssessment() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCitizenId() { return citizenId; }
    public void setCitizenId(String citizenId) { this.citizenId = citizenId; }
    public String getPsychologicalProfile() { return psychologicalProfile; }
    public void setPsychologicalProfile(String psychologicalProfile) { this.psychologicalProfile = psychologicalProfile; }
    public Double getRiskScore() { return riskScore; }
    public void setRiskScore(Double riskScore) { this.riskScore = riskScore; }
    public String getSocialFactors() { return socialFactors; }
    public void setSocialFactors(String socialFactors) { this.socialFactors = socialFactors; }
    public LocalDateTime getAssessedAt() { return assessedAt; }
    public void setAssessedAt(LocalDateTime assessedAt) { this.assessedAt = assessedAt; }

    @PrePersist
    protected void onCreate() {
        assessedAt = LocalDateTime.now();
    }
}
