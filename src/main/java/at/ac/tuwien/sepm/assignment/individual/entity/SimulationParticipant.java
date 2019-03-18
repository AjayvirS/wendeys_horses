package at.ac.tuwien.sepm.assignment.individual.entity;

public class SimulationParticipant {

    private Integer horseId, jockeyId;
    private Float luckFactor;
    private  Integer id;

    public SimulationParticipant(Integer horseId, Integer jockeyId, Float luckFactor){
        this.horseId=horseId;
        this.jockeyId=jockeyId;
        this.luckFactor=luckFactor;
    }

    public SimulationParticipant(){

    }


    public SimulationParticipant(Integer id, Integer horseId, Integer jockeyId, Float luckFactor){
        this.id=id;
        this.horseId=horseId;
        this.jockeyId=jockeyId;
        this.luckFactor=luckFactor;
    }

    public Integer getHorseId() {
        return horseId;
    }

    public void setHorseId(Integer horseId) {
        this.horseId = horseId;
    }

    public Float getLuckFactor() {
        return luckFactor;
    }

    public void setLuckFactor(Float luckFactor) {
        this.luckFactor = luckFactor;
    }

    public Integer getJockeyId() {
        return jockeyId;
    }

    public void setJockeyId(Integer jockeyId) {
        this.jockeyId = jockeyId;
    }

    public Integer getId() {
        return id;
    }

    @Override
    public String toString() {
        return "{Horse ID: "+horseId+", Jockey ID: "+jockeyId+", Luck factor: "+luckFactor+"}";
    }
}
