package io.github.opencivilizationplatform.modules.technology.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "technologies")
public class Technology {

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
    private TechnologyCategory category;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull
    private TechnologyStatus status;

    @Column(name = "research_cost")
    private Integer researchCost;

    @Column(name = "research_progress")
    private Integer researchProgress;

    @Column(name = "tier")
    private Integer tier;

    @Column(name = "unlocks_resource_bonus")
    private String unlocksResourceBonus; // JSON: {"type": "FOOD", "bonus": 0.15}

    @Column(name = "resource_bonus")
    private String resourceBonus;

    @Column(name = "bonus_amount")
    private Double bonusAmount = 0.0;

    // prerequisite tech IDs as comma-separated string
    @Column(name = "prerequisites")
    private String prerequisites;

    @Column(name = "civilization_id")
    private Long civilizationId;

    public Technology() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public TechnologyCategory getCategory() { return category; }
    public void setCategory(TechnologyCategory category) { this.category = category; }
    public TechnologyStatus getStatus() { return status; }
    public void setStatus(TechnologyStatus status) { this.status = status; }
    public Integer getResearchCost() { return researchCost; }
    public void setResearchCost(Integer researchCost) { this.researchCost = researchCost; }
    public Integer getResearchProgress() { return researchProgress; }
    public void setResearchProgress(Integer researchProgress) { this.researchProgress = researchProgress; }
    public Integer getTier() { return tier; }
    public void setTier(Integer tier) { this.tier = tier; }
    public String getUnlocksResourceBonus() { return unlocksResourceBonus; }
    public void setUnlocksResourceBonus(String unlocksResourceBonus) { this.unlocksResourceBonus = unlocksResourceBonus; }
    public String getResourceBonus() { return resourceBonus; }
    public void setResourceBonus(String resourceBonus) { this.resourceBonus = resourceBonus; }
    public Double getBonusAmount() { return bonusAmount; }
    public void setBonusAmount(Double bonusAmount) { this.bonusAmount = bonusAmount; }
    public String getPrerequisites() { return prerequisites; }
    public void setPrerequisites(String prerequisites) { this.prerequisites = prerequisites; }
    public Long getCivilizationId() { return civilizationId; }
    public void setCivilizationId(Long civilizationId) { this.civilizationId = civilizationId; }
}
