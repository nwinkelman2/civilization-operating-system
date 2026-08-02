package io.github.opencivilizationplatform.modules.needs.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Entity
@Table(name = "needs")
public class Need {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull
    private NeedCategory category;

    @Column(nullable = false)
    @NotBlank
    private String region;

    @Column(nullable = false)
    @NotBlank
    private String description;

    @Column(nullable = false)
    private Double quantity;

    @Column(nullable = false)
    @NotBlank
    private String unit;

    @Column(nullable = false)
    @Positive
    private Integer priority;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull
    private NeedStatus status;

    public Need() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public NeedCategory getCategory() { return category; }
    public void setCategory(NeedCategory category) { this.category = category; }
    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Double getQuantity() { return quantity; }
    public void setQuantity(Double quantity) { this.quantity = quantity; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    public Integer getPriority() { return priority; }
    public void setPriority(Integer priority) { this.priority = priority; }
    public NeedStatus getStatus() { return status; }
    public void setStatus(NeedStatus status) { this.status = status; }
}
