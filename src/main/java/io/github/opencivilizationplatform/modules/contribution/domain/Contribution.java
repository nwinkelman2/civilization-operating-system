package io.github.opencivilizationplatform.modules.contribution.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "contributions")
public class Contribution {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "citizen_id")
    @NotNull
    private Citizen citizen;

    @ManyToOne
    @JoinColumn(name = "project_id")
    @NotNull
    private Project project;

    @Column(nullable = false)
    @NotBlank
    private String role;

    @Column(nullable = false)
    private Double impactScore;

    @Column(name = "contribution_date")
    private LocalDateTime contributionDate;

    public Contribution() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Citizen getCitizen() { return citizen; }
    public void setCitizen(Citizen citizen) { this.citizen = citizen; }
    public Project getProject() { return project; }
    public void setProject(Project project) { this.project = project; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public Double getImpactScore() { return impactScore; }
    public void setImpactScore(Double impactScore) { this.impactScore = impactScore; }
    public LocalDateTime getContributionDate() { return contributionDate; }
    public void setContributionDate(LocalDateTime contributionDate) { this.contributionDate = contributionDate; }

    @PrePersist
    protected void onCreate() {
        contributionDate = LocalDateTime.now();
    }
}
