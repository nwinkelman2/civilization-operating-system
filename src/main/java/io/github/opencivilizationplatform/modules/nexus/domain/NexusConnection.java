package io.github.opencivilizationplatform.modules.nexus.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "nexus_connections")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class NexusConnection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "node_a_id", nullable = false)
    @NotNull
    private NexusNode nodeA;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "node_b_id", nullable = false)
    @NotNull
    private NexusNode nodeB;

    @Column(name = "strength")
    private Double strength;

    @Column(name = "latency_ms")
    private Long latencyMs;

    @Column(name = "messages_exchanged")
    private Integer messagesExchanged;

    @Column(name = "established_at")
    private LocalDateTime establishedAt;

    @Column(name = "last_activity_at")
    private LocalDateTime lastActivityAt;

    public NexusConnection() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public NexusNode getNodeA() { return nodeA; }
    public void setNodeA(NexusNode nodeA) { this.nodeA = nodeA; }
    public NexusNode getNodeB() { return nodeB; }
    public void setNodeB(NexusNode nodeB) { this.nodeB = nodeB; }
    public Double getStrength() { return strength; }
    public void setStrength(Double strength) { this.strength = strength; }
    public Long getLatencyMs() { return latencyMs; }
    public void setLatencyMs(Long latencyMs) { this.latencyMs = latencyMs; }
    public Integer getMessagesExchanged() { return messagesExchanged; }
    public void setMessagesExchanged(Integer messagesExchanged) { this.messagesExchanged = messagesExchanged; }
    public LocalDateTime getEstablishedAt() { return establishedAt; }
    public void setEstablishedAt(LocalDateTime establishedAt) { this.establishedAt = establishedAt; }
    public LocalDateTime getLastActivityAt() { return lastActivityAt; }
    public void setLastActivityAt(LocalDateTime lastActivityAt) { this.lastActivityAt = lastActivityAt; }

    @PrePersist
    protected void onCreate() {
        if (strength == null) strength = 0.3;
        if (latencyMs == null) latencyMs = 100L;
        if (messagesExchanged == null) messagesExchanged = 0;
        establishedAt = LocalDateTime.now();
        lastActivityAt = LocalDateTime.now();
    }
}

