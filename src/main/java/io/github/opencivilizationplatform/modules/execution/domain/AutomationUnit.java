package io.github.opencivilizationplatform.modules.execution.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "automation_units")
public class AutomationUnit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotBlank
    private String name;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull
    private AutomationUnitType type;

    @Column(nullable = false)
    @NotBlank
    private String region;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull
    private AutomationUnitStatus status;

    @Column(nullable = false)
    @NotBlank
    private String currentTask;

    @Column(name = "last_ping")
    private LocalDateTime lastPing;

    public AutomationUnit() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public AutomationUnitType getType() { return type; }
    public void setType(AutomationUnitType type) { this.type = type; }
    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }
    public AutomationUnitStatus getStatus() { return status; }
    public void setStatus(AutomationUnitStatus status) { this.status = status; }
    public String getCurrentTask() { return currentTask; }
    public void setCurrentTask(String currentTask) { this.currentTask = currentTask; }
    public LocalDateTime getLastPing() { return lastPing; }
    public void setLastPing(LocalDateTime lastPing) { this.lastPing = lastPing; }

    @PrePersist
    @PreUpdate
    protected void onPing() {
        lastPing = LocalDateTime.now();
    }
}
