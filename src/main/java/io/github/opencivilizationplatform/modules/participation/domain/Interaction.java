package io.github.opencivilizationplatform.modules.participation.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "interactions")
public class Interaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private InteractionType type;

    @Column(nullable = false, length = 2000)
    private String content;

    @Column(nullable = false)
    private String region;

    @Column(nullable = false)
    private String citizenId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private InteractionStatus status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public Interaction() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public InteractionType getType() { return type; }
    public void setType(InteractionType type) { this.type = type; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }
    public String getCitizenId() { return citizenId; }
    public void setCitizenId(String citizenId) { this.citizenId = citizenId; }
    public InteractionStatus getStatus() { return status; }
    public void setStatus(InteractionStatus status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
