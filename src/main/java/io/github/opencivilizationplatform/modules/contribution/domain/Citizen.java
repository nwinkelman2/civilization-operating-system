package io.github.opencivilizationplatform.modules.contribution.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.util.List;

@Entity
@Table(name = "citizens")
public class Citizen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    @NotBlank
    private String citizenId;

    @Column(nullable = false)
    @NotBlank
    private String name;

    @Enumerated(EnumType.STRING)
    private Role role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "civilization_id")
    @com.fasterxml.jackson.annotation.JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private io.github.opencivilizationplatform.modules.civilization.domain.Civilization civilization;

    @OneToOne(mappedBy = "citizen", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @com.fasterxml.jackson.annotation.JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private CitizenWallet wallet;

    @ManyToMany
    @JoinTable(
        name = "citizen_skills",
        joinColumns = @JoinColumn(name = "citizen_id"),
        inverseJoinColumns = @JoinColumn(name = "skill_id")
    )
    private List<Skill> skills;

    @ElementCollection
    private List<String> interests;

    private Double reputationScore;

    @Column(columnDefinition = "TEXT")
    private String biographicalNote;

    public Citizen() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCitizenId() { return citizenId; }
    public void setCitizenId(String citizenId) { this.citizenId = citizenId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
    public io.github.opencivilizationplatform.modules.civilization.domain.Civilization getCivilization() { return civilization; }
    public void setCivilization(io.github.opencivilizationplatform.modules.civilization.domain.Civilization civilization) { this.civilization = civilization; }
    public CitizenWallet getWallet() { return wallet; }
    public void setWallet(CitizenWallet wallet) { this.wallet = wallet; }
    public List<Skill> getSkills() { return skills; }
    public void setSkills(List<Skill> skills) { this.skills = skills; }
    public List<String> getInterests() { return interests; }
    public void setInterests(List<String> interests) { this.interests = interests; }
    public Double getReputationScore() { return reputationScore; }
    public void setReputationScore(Double reputationScore) { this.reputationScore = reputationScore; }
    public String getBiographicalNote() { return biographicalNote; }
    public void setBiographicalNote(String biographicalNote) { this.biographicalNote = biographicalNote; }
}
