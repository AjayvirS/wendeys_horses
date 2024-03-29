package at.ac.tuwien.sepm.assignment.individual.rest.dto;

import at.ac.tuwien.sepm.assignment.individual.entity.SimulationParticipantOutput;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class SimulationOutputDto{

    private Integer id;
    private String name;
    private LocalDateTime created;
    private ArrayList<SimulationParticipantOutput> horseJockeyCombinations;
    public SimulationOutputDto(){

    }
    public SimulationOutputDto(Integer id, String name, LocalDateTime created, ArrayList<SimulationParticipantOutput> horseJockeyCombinations){
        this.horseJockeyCombinations = horseJockeyCombinations;
        this.id=id;
        this.name=name;
        this.created=created;
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


    public ArrayList<SimulationParticipantOutput> getHorseJockeyCombinations() {
        return horseJockeyCombinations;
    }

    public void setHorseJockeyCombinations(ArrayList<SimulationParticipantOutput> horseJockeyCombinations) {
        this.horseJockeyCombinations = horseJockeyCombinations;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SimulationOutputDto)) return false;
        SimulationOutputDto simulationOutputDto = (SimulationOutputDto) o;
        return Objects.equals(name, simulationOutputDto.name) &&
            horseJockeyCombinations.equals(simulationOutputDto.horseJockeyCombinations);
    }

    @Override
    public int hashCode() {

        return Objects.hash(name, horseJockeyCombinations);
    }

    @Override
    public String toString() {
        return "SimulationOutputDto{"+
            "id= "+id+
            "name='" + name + '\'' +
            "created= "+ created+
            ", Simulation Participants Completed=" + horseJockeyCombinations.toString() +
            '}';
    }
}
