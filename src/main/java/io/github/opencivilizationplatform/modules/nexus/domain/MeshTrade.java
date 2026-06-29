package io.github.opencivilizationplatform.modules.nexus.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.github.opencivilizationplatform.modules.civilization.domain.Civilization;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "mesh_trades")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class MeshTrade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    @NotNull
    private Civilization sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    @NotNull
    private Civilization receiver;

    @Column(name = "requested_resource", nullable = false)
    @NotBlank
    private String requestedResource;

    @Column(name = "requested_amount", nullable = false)
    @NotNull
    private Double requestedAmount;

    @Column(name = "offered_resource", nullable = false)
    @NotBlank
    private String offeredResource;

    @Column(name = "offered_amount", nullable = false)
    @NotNull
    private Double offeredAmount;

    @Column(name = "trade_type", nullable = false)
    @NotBlank
    private String tradeType;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public MeshTrade() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Civilization getSender() { return sender; }
    public void setSender(Civilization sender) { this.sender = sender; }

    public Civilization getReceiver() { return receiver; }
    public void setReceiver(Civilization receiver) { this.receiver = receiver; }

    public String getRequestedResource() { return requestedResource; }
    public void setRequestedResource(String requestedResource) { this.requestedResource = requestedResource; }

    public Double getRequestedAmount() { return requestedAmount; }
    public void setRequestedAmount(Double requestedAmount) { this.requestedAmount = requestedAmount; }

    public String getOfferedResource() { return offeredResource; }
    public void setOfferedResource(String offeredResource) { this.offeredResource = offeredResource; }

    public Double getOfferedAmount() { return offeredAmount; }
    public void setOfferedAmount(Double offeredAmount) { this.offeredAmount = offeredAmount; }

    public String getTradeType() { return tradeType; }
    public void setTradeType(String tradeType) { this.tradeType = tradeType; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
