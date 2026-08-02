package io.github.opencivilizationplatform.modules.logistics.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "shipments")
public class Shipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotBlank
    private String cargo;

    @Column(nullable = false)
    @NotBlank
    private String origin;

    @Column(nullable = false)
    @NotBlank
    private String destination;

    @Column(nullable = false)
    private Double quantity;

    @Column(nullable = false)
    @NotBlank
    private String unit;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull
    private ShipmentStatus status;

    private LocalDateTime eta;

    public Shipment() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCargo() { return cargo; }
    public void setCargo(String cargo) { this.cargo = cargo; }
    public String getOrigin() { return origin; }
    public void setOrigin(String origin) { this.origin = origin; }
    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }
    public Double getQuantity() { return quantity; }
    public void setQuantity(Double quantity) { this.quantity = quantity; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    public ShipmentStatus getStatus() { return status; }
    public void setStatus(ShipmentStatus status) { this.status = status; }
    public LocalDateTime getEta() { return eta; }
    public void setEta(LocalDateTime eta) { this.eta = eta; }
}
