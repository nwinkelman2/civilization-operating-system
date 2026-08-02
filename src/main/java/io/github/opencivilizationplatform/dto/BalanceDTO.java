package io.github.opencivilizationplatform.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;

@Schema(description = "Balance report data")
public class BalanceDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    @Schema(description = "Resource category name")
    private String category;
    @Schema(description = "Current supply amount")
    private Double supply;
    @Schema(description = "Current demand amount")
    private Double demand;
    @Schema(description = "Measurement unit")
    private String unit;
    @Schema(description = "Percentage of demand met by supply")
    private Double percentageMet;
    @Schema(description = "Balance status: STABLE, DEFICIT, or CRITICAL")
    private String status;

    public BalanceDTO() {}

    public BalanceDTO(String category, Double supply, Double demand, String unit, Double percentageMet, String status) {
        this.category = category;
        this.supply = supply;
        this.demand = demand;
        this.unit = unit;
        this.percentageMet = percentageMet;
        this.status = status;
    }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public Double getSupply() { return supply; }
    public void setSupply(Double supply) { this.supply = supply; }
    public Double getDemand() { return demand; }
    public void setDemand(Double demand) { this.demand = demand; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    public Double getPercentageMet() { return percentageMet; }
    public void setPercentageMet(Double percentageMet) { this.percentageMet = percentageMet; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
