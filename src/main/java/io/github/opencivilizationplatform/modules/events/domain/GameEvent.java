package io.github.opencivilizationplatform.modules.events.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "game_events")
public class GameEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotBlank
    private String title;

    @Column(length = 2000, nullable = false)
    @NotBlank
    private String description;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull
    private EventType type;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull
    private EventSeverity severity;

    @Column(name = "target_civilization_id")
    private Long targetCivilizationId;

    @Column(name = "effect_json")
    private String effectJson;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "resolved")
    private Boolean resolved;

    public GameEvent() {}

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (resolved == null) resolved = false;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String t) { this.title = t; }
    public String getDescription() { return description; }
    public void setDescription(String d) { this.description = d; }
    public EventType getType() { return type; }
    public void setType(EventType t) { this.type = t; }
    public EventSeverity getSeverity() { return severity; }
    public void setSeverity(EventSeverity s) { this.severity = s; }
    public Long getTargetCivilizationId() { return targetCivilizationId; }
    public void setTargetCivilizationId(Long id) { this.targetCivilizationId = id; }
    public String getEffectJson() { return effectJson; }
    public void setEffectJson(String j) { this.effectJson = j; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public Boolean getResolved() { return resolved; }
    public void setResolved(Boolean r) { this.resolved = r; }
}
