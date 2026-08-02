package io.github.opencivilizationplatform.modules.nexus.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.github.opencivilizationplatform.modules.civilization.domain.Civilization;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "nexus_nodes")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class NexusNode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotBlank
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "civilization_id", nullable = false)
    @NotNull
    @JsonIgnore
    private Civilization civilization;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull
    private NexusNodeType type;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull
    private NexusNodeStatus status;

    @Column(name = "region")
    private String region;

    @Column(name = "knowledge_base", columnDefinition = "TEXT")
    private String knowledgeBase;

    @Column(name = "last_active_at")
    private LocalDateTime lastActiveAt;

    @Column(name = "message_count")
    private Integer messageCount;

    public NexusNode() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Civilization getCivilization() { return civilization; }
    public void setCivilization(Civilization civilization) { this.civilization = civilization; }
    public NexusNodeType getType() { return type; }
    public void setType(NexusNodeType type) { this.type = type; }
    public NexusNodeStatus getStatus() { return status; }
    public void setStatus(NexusNodeStatus status) { this.status = status; }
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
        if (status == null) status = NexusNodeStatus.BOOTING;
        if (messageCount == null) messageCount = 0;
        lastActiveAt = LocalDateTime.now();
    }
}

