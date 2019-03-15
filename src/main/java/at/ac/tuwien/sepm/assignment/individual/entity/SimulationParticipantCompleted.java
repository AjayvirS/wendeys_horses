package at.ac.tuwien.sepm.assignment.individual.entity;

public class SimulationParticipantCompleted {

    private Integer id, rank, simulationId;
    private Float luckFactor;
    private Double avgSpeed, jockeySkill, horseSpeed;
    private String horseName, jockeyName;

    public SimulationParticipantCompleted(Integer id, Integer rank, Float luckFactor, Double avgSpeed, Double jockeySkill, Double horseSpeed
        , String horseName, String jockeyName, Integer simulationId) {
        this.id = id;
        this.rank=rank;
        this.luckFactor=luckFactor;
        this.avgSpeed=avgSpeed;
        this.jockeySkill=jockeySkill;
        this.jockeyName=jockeyName;
        this.horseSpeed=horseSpeed;
        this.horseName=horseName;
        this.simulationId=simulationId;

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

    public Double getJockeySkill() {
        return jockeySkill;
    }

    public void setJockeySkill(Double jockeySkill) {
        this.jockeySkill = jockeySkill;
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

    public Integer getSimulationId() {
        return simulationId;
    }

    public void setSimulationId(Integer simulationId) {
        this.simulationId = simulationId;
    }
}
