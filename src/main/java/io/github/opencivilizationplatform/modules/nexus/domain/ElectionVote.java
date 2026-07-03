package io.github.opencivilizationplatform.modules.nexus.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "election_votes")
public class ElectionVote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "election_id", nullable = false)
    private Long electionId;

    @Column(name = "voter_name")
    private String voterName;

    @Column(name = "candidate_name")
    private String candidateName;

    @Column(name = "voted_at")
    private LocalDateTime votedAt;

    @PrePersist
    protected void onCreate() { votedAt = LocalDateTime.now(); }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getElectionId() { return electionId; }
    public void setElectionId(Long electionId) { this.electionId = electionId; }
    public String getVoterName() { return voterName; }
    public void setVoterName(String voterName) { this.voterName = voterName; }
    public String getCandidateName() { return candidateName; }
    public void setCandidateName(String candidateName) { this.candidateName = candidateName; }
    public LocalDateTime getVotedAt() { return votedAt; }
    public void setVotedAt(LocalDateTime votedAt) { this.votedAt = votedAt; }
}
