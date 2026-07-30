package io.github.opencivilizationplatform.modules.contribution.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "citizen_wallets")
public class CitizenWallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "citizen_id", nullable = false)
    @JsonIgnore
    private Citizen citizen;

    @Column(nullable = false)
    @NotNull
    private Double food = 0.0;

    @Column(nullable = false)
    @NotNull
    private Double water = 0.0;

    @Column(nullable = false)
    @NotNull
    private Double minerals = 0.0;

    @Column(nullable = false)
    @NotNull
    private Double energy = 0.0;

    public CitizenWallet() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Citizen getCitizen() { return citizen; }
    public void setCitizen(Citizen citizen) { this.citizen = citizen; }

    public Double getFood() { return food; }
    public void setFood(Double food) { this.food = food; }

    public Double getWater() { return water; }
    public void setWater(Double water) { this.water = water; }

    public Double getMinerals() { return minerals; }
    public void setMinerals(Double minerals) { this.minerals = minerals; }

    public Double getEnergy() { return energy; }
    public void setEnergy(Double energy) { this.energy = energy; }
}
