package io.github.opencivilizationplatform.modules.trade.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "trade_agreements")
public class TradeAgreement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "from_civilization_id", nullable = false)
    @NotNull
    private Long fromCivilizationId;

    @Column(name = "to_civilization_id", nullable = false)
    @NotNull
    private Long toCivilizationId;

    @Column(name = "resource_type")
    private String resourceType;

    @Column(name = "quantity")
    private Double quantity;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TradeStatus status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    public TradeAgreement() {}

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (status == null) status = TradeStatus.PROPOSED;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getFromCivilizationId() { return fromCivilizationId; }
    public void setFromCivilizationId(Long id) { this.fromCivilizationId = id; }
    public Long getToCivilizationId() { return toCivilizationId; }
    public void setToCivilizationId(Long id) { this.toCivilizationId = id; }
    public String getResourceType() { return resourceType; }
    public void setResourceType(String t) { this.resourceType = t; }
    public Double getQuantity() { return quantity; }
    public void setQuantity(Double q) { this.quantity = q; }
    public TradeStatus getStatus() { return status; }
    public void setStatus(TradeStatus s) { this.status = s; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime t) { this.expiresAt = t; }
}
