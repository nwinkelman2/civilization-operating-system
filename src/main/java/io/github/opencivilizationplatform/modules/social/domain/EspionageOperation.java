package io.github.opencivilizationplatform.modules.social.domain;

import io.github.opencivilizationplatform.modules.civilization.domain.Civilization;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "espionage_operations")
public class EspionageOperation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "initiator_id")
    private Civilization initiator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_id")
    private Civilization target;

    @Column(nullable = false)
    private String type; // STEAL_TECH, SABOTAGE_BOTS

    @Column(name = "spy_bots_count", nullable = false)
    private Integer spyBotsCount;

    @Column(name = "risk_level", nullable = false)
    private Double riskLevel;

    @Column(nullable = false)
    private String status; // IN_PROGRESS, SUCCESS, FAILED

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "ticks_remaining", nullable = false)
    private Integer ticksRemaining;

    public EspionageOperation() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Civilization getInitiator() { return initiator; }
    public void setInitiator(Civilization initiator) { this.initiator = initiator; }

    public Civilization getTarget() { return target; }
    public void setTarget(Civilization target) { this.target = target; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Integer getSpyBotsCount() { return spyBotsCount; }
    public void setSpyBotsCount(Integer spyBotsCount) { this.spyBotsCount = spyBotsCount; }

    public Double getRiskLevel() { return riskLevel; }
    public void setRiskLevel(Double riskLevel) { this.riskLevel = riskLevel; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Integer getTicksRemaining() { return ticksRemaining; }
    public void setTicksRemaining(Integer ticksRemaining) { this.ticksRemaining = ticksRemaining; }
}
