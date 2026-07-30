package io.github.opencivilizationplatform.modules.contribution.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "delegate_votes")
public class DelegateVote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "voter_citizen_id", nullable = false)
    private Citizen voter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "candidate_citizen_id", nullable = false)
    private Citizen candidate;

    @Column(nullable = false)
    private String sector;

    @Column(name = "civilization_id", nullable = false)
    private Long civilizationId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public DelegateVote() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Citizen getVoter() { return voter; }
    public void setVoter(Citizen voter) { this.voter = voter; }
    public Citizen getCandidate() { return candidate; }
    public void setCandidate(Citizen candidate) { this.candidate = candidate; }
    public String getSector() { return sector; }
    public void setSector(String sector) { this.sector = sector; }
    public Long getCivilizationId() { return civilizationId; }
    public void setCivilizationId(Long civilizationId) { this.civilizationId = civilizationId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
