package io.github.opencivilizationplatform.modules.voxtex.domain;

import io.github.opencivilizationplatform.modules.civilization.domain.Civilization;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "voxtex_nodes")
public class VoxtexNode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotBlank
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "civilization_id", nullable = false)
    @NotNull
    private Civilization civilization;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull
    private VoxtexNodeType type;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull
    private VoxtexNodeStatus status;

    @Column(name = "region")
    private String region;

    @Column(name = "knowledge_base", columnDefinition = "TEXT")
    private String knowledgeBase;

    @Column(name = "last_active_at")
    private LocalDateTime lastActiveAt;

    @Column(name = "message_count")
    private Integer messageCount;

    public VoxtexNode() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Civilization getCivilization() { return civilization; }
    public void setCivilization(Civilization civilization) { this.civilization = civilization; }
    public VoxtexNodeType getType() { return type; }
    public void setType(VoxtexNodeType type) { this.type = type; }
    public VoxtexNodeStatus getStatus() { return status; }
    public void setStatus(VoxtexNodeStatus status) { this.status = status; }
    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }
    public String getKnowledgeBase() { return knowledgeBase; }
    public void setKnowledgeBase(String knowledgeBase) { this.knowledgeBase = knowledgeBase; }
    public LocalDateTime getLastActiveAt() { return lastActiveAt; }
    public void setLastActiveAt(LocalDateTime lastActiveAt) { this.lastActiveAt = lastActiveAt; }
    public Integer getMessageCount() { return messageCount; }
    public void setMessageCount(Integer messageCount) { this.messageCount = messageCount; }

    @PrePersist
    protected void onCreate() {
        if (status == null) status = VoxtexNodeStatus.BOOTING;
        if (messageCount == null) messageCount = 0;
        lastActiveAt = LocalDateTime.now();
    }
}
