package io.github.opencivilizationplatform.modules.participation.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "constitutional_rules")
public class Rule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 2000)
    private String description;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String logicCode;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RuleStatus status;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ValidationStatus validationStatus;

    private String validatedBy;

    private Integer votesCount;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public Rule() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getLogicCode() { return logicCode; }
    public void setLogicCode(String logicCode) { this.logicCode = logicCode; }
    public RuleStatus getStatus() { return status; }
    public void setStatus(RuleStatus status) { this.status = status; }
    public ValidationStatus getValidationStatus() { return validationStatus; }
    public void setValidationStatus(ValidationStatus validationStatus) { this.validationStatus = validationStatus; }
    public String getValidatedBy() { return validatedBy; }
    public void setValidatedBy(String validatedBy) { this.validatedBy = validatedBy; }
    public Integer getVotesCount() { return votesCount; }
    public void setVotesCount(Integer votesCount) { this.votesCount = votesCount; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
