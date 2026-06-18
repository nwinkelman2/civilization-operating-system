package io.github.opencivilizationplatform.modules.voxtex.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "voxtex_messages")
public class VoxtexMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_node_id", nullable = false)
    @NotNull
    private VoxtexNode sourceNode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_node_id", nullable = false)
    @NotNull
    private VoxtexNode targetNode;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull
    private VoxtexMessageType messageType;

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

    public VoxtexMessage() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public VoxtexNode getSourceNode() { return sourceNode; }
    public void setSourceNode(VoxtexNode sourceNode) { this.sourceNode = sourceNode; }
    public VoxtexNode getTargetNode() { return targetNode; }
    public void setTargetNode(VoxtexNode targetNode) { this.targetNode = targetNode; }
    public VoxtexMessageType getMessageType() { return messageType; }
    public void setMessageType(VoxtexMessageType messageType) { this.messageType = messageType; }
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
