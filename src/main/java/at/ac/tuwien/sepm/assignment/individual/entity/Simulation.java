package at.ac.tuwien.sepm.assignment.individual.entity;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;

public class Simulation {
    private Integer id;
    private String name;
    private LocalDateTime created;
    private ArrayList<SimulationParticipant> simulationParticipants;
    private ArrayList<SimulationParticipantCompleted> simulationParticipantsCompleted;

    public Simulation(){

    }

    public Simulation(String name, ArrayList<SimulationParticipant>simulationParticipants){
        this.simulationParticipants=simulationParticipants;
        this.name=name;
    }

    public Simulation(Integer id, String name, LocalDateTime created){
        this.id=id;
        this.name=name;
        this.created=created;
    }

    public void setSimulationParticipantsCompleted(ArrayList<SimulationParticipantCompleted> simulationParticipantsCompleted) {
        this.simulationParticipantsCompleted = simulationParticipantsCompleted;
    }

    public ArrayList<SimulationParticipantCompleted> getSimulationParticipantsCompleted() {
        return simulationParticipantsCompleted;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public ArrayList<SimulationParticipant> getSimulationParticipants() {
        return simulationParticipants;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Simulation)) return false;
        Simulation simulation = (Simulation) o;
        return Objects.equals(id, simulation.id) &&
            Objects.equals(name, simulation.name) &&
            Objects.equals(created, simulation.created);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, name, created);
    }

    @Override
    public String toString() {
        return "Simulation{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", created=" + created +
            "Simulation Participants: "+simulationParticipants.toString()+
            '}';
    }
}
