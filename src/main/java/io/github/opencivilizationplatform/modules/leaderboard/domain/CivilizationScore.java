package io.github.opencivilizationplatform.modules.leaderboard.domain;
public class CivilizationScore {
    private Long civilizationId;
    private String name;
    private Double reputationScore;
    private Integer population;
    private Integer techCount;
    private Integer tradeCount;
    private Double totalScore;

    public CivilizationScore(Long civilizationId, String name, Double reputationScore,
                              Integer population, Integer techCount, Integer tradeCount) {
        this.civilizationId = civilizationId;
        this.name = name;
        this.reputationScore = reputationScore;
        this.population = population;
        this.techCount = techCount;
        this.tradeCount = tradeCount;
        this.totalScore = (reputationScore != null ? reputationScore : 50)
            + (population != null ? population * 0.1 : 0)
            + (techCount != null ? techCount * 10 : 0)
            + (tradeCount != null ? tradeCount * 5 : 0);
    }

    public Long getCivilizationId() { return civilizationId; }
    public String getName() { return name; }
    public Double getReputationScore() { return reputationScore; }
    public Integer getPopulation() { return population; }
    public Integer getTechCount() { return techCount; }
    public Integer getTradeCount() { return tradeCount; }
    public Double getTotalScore() { return totalScore; }
}
