package io.github.opencivilizationplatform.modules.resources.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.locationtech.jts.geom.Point;
import java.io.Serializable;

@Entity
@Table(name = "resources")
public class Resource implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull
    private ResourceType type;

    @Column(nullable = false, length = 1000)
    @NotBlank
    private String description;

    @Column(nullable = false)
    @NotBlank
    private String name;

    @Column(nullable = false)
    private Double quantity;

    @Column(nullable = false)
    @NotBlank
    private String unit;

    @Column(columnDefinition = "geometry(Point, 4326)")
    private Point location;

    public Resource() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public ResourceType getType() { return type; }
    public void setType(ResourceType type) { this.type = type; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Double getQuantity() { return quantity; }
    public void setQuantity(Double quantity) { this.quantity = quantity; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    public Point getLocation() { return location; }
    public void setLocation(Point location) { this.location = location; }

    public Double getLatitude() {
        return location != null ? location.getY() : null;
    }

    public Double getLongitude() {
        return location != null ? location.getX() : null;
    }
}
