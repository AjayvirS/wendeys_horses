package at.ac.tuwien.sepm.assignment.individual.rest.dto;

import at.ac.tuwien.sepm.assignment.individual.entity.Simulation;
import at.ac.tuwien.sepm.assignment.individual.entity.SimulationParticipant;

import java.util.ArrayList;
import java.util.Objects;

public class SimulationInputDto {
    private String name;
    private ArrayList<SimulationParticipant> simulationParticipants;

    public SimulationInputDto(){

    }

    public SimulationInputDto(String name, ArrayList<SimulationParticipant> simulationParticipants){
        this.name=name;
        this.simulationParticipants=simulationParticipants;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<SimulationParticipant> getSimulationParticipants() {
        return simulationParticipants;
    }

    public void setSimulationParticipants(ArrayList<SimulationParticipant> simulationParticipants) {
        this.simulationParticipants = simulationParticipants;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SimulationInputDto)) return false;
        SimulationInputDto simulationInputDto = (SimulationInputDto) o;
        return Objects.equals(name, simulationInputDto.name) &&
            Objects.equals(simulationParticipants, simulationInputDto.simulationParticipants);
    }

    @Override
    public int hashCode() {

        return Objects.hash(name, simulationParticipants);
    }

    @Override
    public String toString() {
        return "SimulationInputDto{"+
            "name='" + name + '\'' +
            ", Simulation Participants=" + simulationParticipants.toString() +
            '}';
    }
}
