package at.ac.tuwien.sepm.assignment.individual.rest.dto;

import at.ac.tuwien.sepm.assignment.individual.entity.SimulationParticipantInput;

import java.util.ArrayList;
import java.util.Objects;

public class SimulationInputDto {
    private String name;
    private ArrayList<SimulationParticipantInput> simulationParticipantInputs;

    public SimulationInputDto(){

    }

    public SimulationInputDto(String name, ArrayList<SimulationParticipantInput> simulationParticipantInputs){
        this.name=name;
        this.simulationParticipantInputs = simulationParticipantInputs;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<SimulationParticipantInput> getSimulationParticipantInputs() {
        return simulationParticipantInputs;
    }

    public void setSimulationParticipantInputs(ArrayList<SimulationParticipantInput> simulationParticipantInputs) {
        this.simulationParticipantInputs = simulationParticipantInputs;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SimulationInputDto)) return false;
        SimulationInputDto simulationInputDto = (SimulationInputDto) o;
        return Objects.equals(name, simulationInputDto.name) &&
            Objects.equals(simulationParticipantInputs, simulationInputDto.simulationParticipantInputs);
    }

    @Override
    public int hashCode() {

        return Objects.hash(name, simulationParticipantInputs);
    }

    @Override
    public String toString() {
        return "SimulationInputDto{"+
            "name='" + name + '\'' +
            ", Simulation Participants=" + simulationParticipantInputs.toString() +
            '}';
    }
}
