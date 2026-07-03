package io.github.opencivilizationplatform.modules.events.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "global_events")
public class GlobalEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private GlobalEventType type;

    @Column(name = "affected_civ_ids", columnDefinition = "TEXT")
    private String affectedCivIds;

    @Column(nullable = false)
    private Double severity = 100.0;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "ends_at")
    private LocalDateTime endsAt;

    @Column(nullable = false)
    private Boolean active = true;

    @Column(name = "ticks_remaining")
    private Integer ticksRemaining = 3;

    @PrePersist
    protected void onCreate() {
        startedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public GlobalEventType getType() { return type; }
    public void setType(GlobalEventType type) { this.type = type; }
    public String getAffectedCivIds() { return affectedCivIds; }
    public void setAffectedCivIds(String a) { this.affectedCivIds = a; }
    public Double getSeverity() { return severity; }
    public void setSeverity(Double s) { this.severity = s; }
    public String getDescription() { return description; }
    public void setDescription(String d) { this.description = d; }
    public LocalDateTime getStartedAt() { return startedAt; }
    public void setStartedAt(LocalDateTime t) { this.startedAt = t; }
    public LocalDateTime getEndsAt() { return endsAt; }
    public void setEndsAt(LocalDateTime t) { this.endsAt = t; }
    public Boolean getActive() { return active != null && active; }
    public void setActive(Boolean a) { this.active = a; }
    public Integer getTicksRemaining() { return ticksRemaining == null ? 0 : ticksRemaining; }
    public void setTicksRemaining(Integer t) { this.ticksRemaining = t; }
}
