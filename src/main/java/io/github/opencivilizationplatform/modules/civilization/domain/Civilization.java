package io.github.opencivilizationplatform.modules.civilization.domain;

import io.github.opencivilizationplatform.config.seed.CivilizationScale;
import io.github.opencivilizationplatform.modules.region.domain.ResourceRegion;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "civilizations")
public class Civilization {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    @NotBlank
    private String name;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull
    private CivilizationScale scale;

    @Column(nullable = false)
    @NotBlank
    private String region;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull
    private CivilizationStatus status;

    @Column(name = "owner_token", nullable = false)
    @NotBlank
    private String ownerToken;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "last_active_at")
    private LocalDateTime lastActiveAt;

    // reputation 0-100
    @Column(name = "reputation_score")
    private Double reputationScore;

    // population count
    @Column(name = "population")
    private Integer population;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "home_region_id")
    private ResourceRegion homeRegion;

    @Column(nullable = false)
    private Double food = 100.0;

    @Column(nullable = false)
    private Double water = 100.0;

    @Column(nullable = false)
    private Double minerals = 50.0;

    @Column(nullable = false)
    private Double energy = 75.0;

    @Column(nullable = false)
    private Double housing = 50.0;

    @Column(name = "resource_history", columnDefinition = "TEXT")
    private String resourceHistory = "[]";

    @Column(name = "agri_bots_priority", nullable = false)
    private Integer agriBotsPriority = 25;

    @Column(name = "aqua_bots_priority", nullable = false)
    private Integer aquaBotsPriority = 25;

    @Column(name = "explore_bots_priority", nullable = false)
    private Integer exploreBotsPriority = 25;

    @Column(name = "utility_bots_priority", nullable = false)
    private Integer utilityBotsPriority = 25;

    @Column(name = "eco_bots_priority", nullable = false)
    private Integer ecoBotsPriority = 0;

    @Column(name = "science_bots_priority", nullable = false)
    private Integer scienceBotsPriority = 0;

    @Column(name = "security_bots_priority", nullable = false)
    private Integer securityBotsPriority = 0;

    @Column(name = "spy_bots_priority", nullable = false)
    private Integer spyBotsPriority = 0;

    @Column(name = "consensus_coins", nullable = false)
    private Double consensusCoins = 100.0;

    public String getResourceHistory() { return resourceHistory; }
    public void setResourceHistory(String resourceHistory) { this.resourceHistory = resourceHistory; }

    public Integer getAgriBotsPriority() { return agriBotsPriority; }
    public void setAgriBotsPriority(Integer agriBotsPriority) { this.agriBotsPriority = agriBotsPriority; }
    public Integer getAquaBotsPriority() { return aquaBotsPriority; }
    public void setAquaBotsPriority(Integer aquaBotsPriority) { this.aquaBotsPriority = aquaBotsPriority; }
    public Integer getExploreBotsPriority() { return exploreBotsPriority; }
    public void setExploreBotsPriority(Integer exploreBotsPriority) { this.exploreBotsPriority = exploreBotsPriority; }
    public Integer getUtilityBotsPriority() { return utilityBotsPriority; }
    public void setUtilityBotsPriority(Integer utilityBotsPriority) { this.utilityBotsPriority = utilityBotsPriority; }
    public Integer getEcoBotsPriority() { return ecoBotsPriority; }
    public void setEcoBotsPriority(Integer ecoBotsPriority) { this.ecoBotsPriority = ecoBotsPriority; }
    public Integer getScienceBotsPriority() { return scienceBotsPriority; }
    public void setScienceBotsPriority(Integer scienceBotsPriority) { this.scienceBotsPriority = scienceBotsPriority; }
    public Integer getSecurityBotsPriority() { return securityBotsPriority; }
    public void setSecurityBotsPriority(Integer securityBotsPriority) { this.securityBotsPriority = securityBotsPriority; }

    public Civilization() {}

    // getters + setters for ALL fields
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public CivilizationScale getScale() { return scale; }
    public void setScale(CivilizationScale scale) { this.scale = scale; }
    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }
    public CivilizationStatus getStatus() { return status; }
    public void setStatus(CivilizationStatus status) { this.status = status; }
    public String getOwnerToken() { return ownerToken; }
    public void setOwnerToken(String ownerToken) { this.ownerToken = ownerToken; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getLastActiveAt() { return lastActiveAt; }
    public void setLastActiveAt(LocalDateTime lastActiveAt) { this.lastActiveAt = lastActiveAt; }
    public Double getReputationScore() { return reputationScore; }
    public void setReputationScore(Double reputationScore) { this.reputationScore = reputationScore; }
    public Integer getPopulation() { return population; }
    public void setPopulation(Integer population) { this.population = population; }
    public ResourceRegion getHomeRegion() { return homeRegion; }
    public void setHomeRegion(ResourceRegion homeRegion) { this.homeRegion = homeRegion; }
    public Long getHomeRegionId() { return homeRegion != null ? homeRegion.getId() : null; }
    public Double getFood() { return food; }
    public void setFood(Double food) { this.food = food; }
    public Double getWater() { return water; }
    public void setWater(Double water) { this.water = water; }
    public Double getMinerals() { return minerals; }
    public void setMinerals(Double minerals) { this.minerals = minerals; }
    public Double getEnergy() { return energy; }
    public void setEnergy(Double energy) { this.energy = energy; }
    public Double getHousing() { return housing; }
    public void setHousing(Double housing) { this.housing = housing; }
    public Integer getSpyBotsPriority() { return spyBotsPriority; }
    public void setSpyBotsPriority(Integer spyBotsPriority) { this.spyBotsPriority = spyBotsPriority; }
    public Double getConsensusCoins() { return consensusCoins; }
    public void setConsensusCoins(Double consensusCoins) { this.consensusCoins = consensusCoins; }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        lastActiveAt = LocalDateTime.now();
        if (status == null) status = CivilizationStatus.EMERGING;
        if (reputationScore == null) reputationScore = 50.0;
        if (population == null) population = 100;
        if (consensusCoins == null) consensusCoins = 100.0;
        if (spyBotsPriority == null) spyBotsPriority = 0;
    }
}
