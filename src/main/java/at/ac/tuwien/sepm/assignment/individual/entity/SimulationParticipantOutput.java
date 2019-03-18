package at.ac.tuwien.sepm.assignment.individual.entity;

public class SimulationParticipantOutput {

    private Integer id, rank;
    private String horseName, jockeyName;
    private Double avgSpeed, horseSpeed, skill;
    private Float luckFactor;


    public SimulationParticipantOutput(Integer id, Integer rank, String horseName, String jockeyName, Double avgSpeed, Double horseSpeed, Double skill, Float luckFactor
        ) {
        this.id = id;
        this.rank=rank;
        this.luckFactor=luckFactor;
        this.avgSpeed=avgSpeed;
        this.skill = skill;
        this.jockeyName=jockeyName;
        this.horseSpeed=horseSpeed;
        this.horseName=horseName;
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    public Float getLuckFactor() {
        return luckFactor;
    }

    public void setLuckFactor(Float luckFactor) {
        this.luckFactor = luckFactor;
    }

    public Double getAvgSpeed() {
        return avgSpeed;
    }

    public void setAvgSpeed(Double avgSpeed) {
        this.avgSpeed = avgSpeed;
    }

    public Double getSkill() {
        return skill;
    }

    public void setSkill(Double skill) {
        this.skill = skill;
    }

    public Double getHorseSpeed() {
        return horseSpeed;
    }

    public void setHorseSpeed(Double horseSpeed) {
        this.horseSpeed = horseSpeed;
    }

    public String getHorseName() {
        return horseName;
    }

    public void setHorseName(String horseName) {
        this.horseName = horseName;
    }

    public String getJockeyName() {
        return jockeyName;
    }

    public void setJockeyName(String jockeyName) {
        this.jockeyName = jockeyName;
    }
}
