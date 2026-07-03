package io.github.opencivilizationplatform.modules.nexus.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "elections")
public class Election {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "civilization_id", nullable = false)
    private Long civilizationId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ElectionStatus status = ElectionStatus.OPEN;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "ends_at")
    private LocalDateTime endsAt;

    @Column(name = "winner_name")
    private String winnerName;

    @Column(name = "ticks_remaining")
    private Integer ticksRemaining = 5;

    @PrePersist
    protected void onCreate() { startedAt = LocalDateTime.now(); }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getCivilizationId() { return civilizationId; }
    public void setCivilizationId(Long civilizationId) { this.civilizationId = civilizationId; }
    public ElectionStatus getStatus() { return status; }
    public void setStatus(ElectionStatus status) { this.status = status; }
    public LocalDateTime getStartedAt() { return startedAt; }
    public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }
    public LocalDateTime getEndsAt() { return endsAt; }
    public void setEndsAt(LocalDateTime endsAt) { this.endsAt = endsAt; }
    public String getWinnerName() { return winnerName; }
    public void setWinnerName(String winnerName) { this.winnerName = winnerName; }
    public Integer getTicksRemaining() { return ticksRemaining == null ? 0 : ticksRemaining; }
    public void setTicksRemaining(Integer ticksRemaining) { this.ticksRemaining = ticksRemaining; }
}
