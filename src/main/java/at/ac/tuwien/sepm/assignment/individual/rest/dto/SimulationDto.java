package at.ac.tuwien.sepm.assignment.individual.rest.dto;

import at.ac.tuwien.sepm.assignment.individual.entity.Simulation;
import at.ac.tuwien.sepm.assignment.individual.entity.SimulationParticipant;

import java.util.ArrayList;
import java.util.Objects;

public class SimulationDto {
    private String name;
    private ArrayList<SimulationParticipant> simulationParticipants;

    public SimulationDto(){

    }

    public SimulationDto(String name, ArrayList<SimulationParticipant> simulationParticipants){
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
        if (!(o instanceof Simulation)) return false;
        SimulationDto simulationDto = (SimulationDto) o;
        return Objects.equals(name, simulationDto.name) &&
            Objects.equals(simulationParticipants, simulationDto.simulationParticipants);
    }

    @Override
    public int hashCode() {

        return Objects.hash(name, simulationParticipants);
    }

    @Override
    public String toString() {
        return "SimulationDto{"+
            "name='" + name + '\'' +
            ", Simulation Participants=" + simulationParticipants.toString() +
            '}';
    }
}
