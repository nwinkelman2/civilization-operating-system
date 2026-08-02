package io.github.opencivilizationplatform.modules.governance.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "scientific_committees")
public class ScientificCommittee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull
    private CommitteeArea area;

    @Column(nullable = false)
    @NotBlank
    private String name;

    @Column(columnDefinition = "TEXT")
    private String mandate;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull
    private ValidationLevel validationLevel;

    @Column(name = "last_audit")
    private LocalDateTime lastAudit;

    public ScientificCommittee() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public CommitteeArea getArea() { return area; }
    public void setArea(CommitteeArea area) { this.area = area; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getMandate() { return mandate; }
    public void setMandate(String mandate) { this.mandate = mandate; }
    public ValidationLevel getValidationLevel() { return validationLevel; }
    public void setValidationLevel(ValidationLevel validationLevel) { this.validationLevel = validationLevel; }
    public LocalDateTime getLastAudit() { return lastAudit; }
    public void setLastAudit(LocalDateTime lastAudit) { this.lastAudit = lastAudit; }

    @PrePersist
    @PreUpdate
    protected void onAudit() {
        lastAudit = LocalDateTime.now();
    }
}
