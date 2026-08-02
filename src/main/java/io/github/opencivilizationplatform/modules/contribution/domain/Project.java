package io.github.opencivilizationplatform.modules.contribution.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "contribution_projects")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotBlank
    private String title;

    @Column(nullable = false, length = 1000)
    @NotBlank
    private String description;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull
    private ProjectCategory category;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull
    private ImpactArea impactArea;

    @ElementCollection
    private List<String> requiredSkillNames;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull
    private ProjectStatus status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "civilization_id")
    private io.github.opencivilizationplatform.modules.civilization.domain.Civilization civilization;

    public io.github.opencivilizationplatform.modules.civilization.domain.Civilization getCivilization() { return civilization; }
    public void setCivilization(io.github.opencivilizationplatform.modules.civilization.domain.Civilization civilization) { this.civilization = civilization; }

    public Project() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public ProjectCategory getCategory() { return category; }
    public void setCategory(ProjectCategory category) { this.category = category; }
    public ImpactArea getImpactArea() { return impactArea; }
    public void setImpactArea(ImpactArea impactArea) { this.impactArea = impactArea; }
    public List<String> getRequiredSkillNames() { return requiredSkillNames; }
    public void setRequiredSkillNames(List<String> requiredSkillNames) { this.requiredSkillNames = requiredSkillNames; }
    public ProjectStatus getStatus() { return status; }
    public void setStatus(ProjectStatus status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
