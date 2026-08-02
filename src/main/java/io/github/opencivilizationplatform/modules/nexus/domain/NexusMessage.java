package io.github.opencivilizationplatform.modules.nexus.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "nexus_messages")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class NexusMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "source_node_id", nullable = false)
    @NotNull
    private NexusNode sourceNode;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "target_node_id", nullable = false)
    @NotNull
    private NexusNode targetNode;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull
    private NexusMessageType messageType;

    @Column(nullable = false, length = 4000)
    @NotBlank
    private String content;

    @Column(name = "sent_at", nullable = false)
    private LocalDateTime sentAt;

    @Column(name = "delivered_at")
    private LocalDateTime deliveredAt;

    @Column(name = "hop_count")
    private Integer hopCount;

    @Column(name = "delivered")
    private Boolean delivered;

    public NexusMessage() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public NexusNode getSourceNode() { return sourceNode; }
    public void setSourceNode(NexusNode sourceNode) { this.sourceNode = sourceNode; }
    public NexusNode getTargetNode() { return targetNode; }
    public void setTargetNode(NexusNode targetNode) { this.targetNode = targetNode; }
    public NexusMessageType getMessageType() { return messageType; }
    public void setMessageType(NexusMessageType messageType) { this.messageType = messageType; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public LocalDateTime getSentAt() { return sentAt; }
    public void setSentAt(LocalDateTime sentAt) { this.sentAt = sentAt; }
    public LocalDateTime getDeliveredAt() { return deliveredAt; }
    public void setDeliveredAt(LocalDateTime deliveredAt) { this.deliveredAt = deliveredAt; }
    public Integer getHopCount() { return hopCount; }
    public void setHopCount(Integer hopCount) { this.hopCount = hopCount; }
    public Boolean getDelivered() { return delivered; }
    public void setDelivered(Boolean delivered) { this.delivered = delivered; }

    @PrePersist
    protected void onCreate() {
        sentAt = LocalDateTime.now();
        if (hopCount == null) hopCount = 0;
        if (delivered == null) delivered = false;
    }
}

