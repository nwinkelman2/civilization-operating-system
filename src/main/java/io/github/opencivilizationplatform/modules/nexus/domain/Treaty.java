package io.github.opencivilizationplatform.modules.nexus.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "treaties")
public class Treaty {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TreatyType type;

    @Column(name = "proposer_civ_id", nullable = false)
    private Long proposerCivId;

    @Column(name = "invited_civ_ids", columnDefinition = "TEXT")
    private String invitedCivIds;

    @Column(name = "signatory_civ_ids", columnDefinition = "TEXT")
    private String signatoryCivIds;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TreatyStatus status = TreatyStatus.PROPOSED;

    @Column(name = "proposed_at")
    private LocalDateTime proposedAt;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @PrePersist
    protected void onCreate() {
        proposedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public TreatyType getType() { return type; }
    public void setType(TreatyType type) { this.type = type; }
    public Long getProposerCivId() { return proposerCivId; }
    public void setProposerCivId(Long proposerCivId) { this.proposerCivId = proposerCivId; }
    public String getInvitedCivIds() { return invitedCivIds; }
    public void setInvitedCivIds(String invitedCivIds) { this.invitedCivIds = invitedCivIds; }
    public String getSignatoryCivIds() { return signatoryCivIds; }
    public void setSignatoryCivIds(String signatoryCivIds) { this.signatoryCivIds = signatoryCivIds; }
    public TreatyStatus getStatus() { return status; }
    public void setStatus(TreatyStatus status) { this.status = status; }
    public LocalDateTime getProposedAt() { return proposedAt; }
    public void setProposedAt(LocalDateTime proposedAt) { this.proposedAt = proposedAt; }
    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
}
