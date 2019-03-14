package at.ac.tuwien.sepm.assignment.individual.rest.dto;

import at.ac.tuwien.sepm.assignment.individual.entity.Jockey;
import at.ac.tuwien.sepm.assignment.individual.entity.Simulation;

import java.time.LocalDateTime;
import java.util.Objects;

public class SimulationDto {
    private Integer id;
    private String name;
    private LocalDateTime created;

    public SimulationDto(){

    }

    public SimulationDto(Integer id, String name, LocalDateTime created){
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Simulation)) return false;
        SimulationDto simulationDto = (SimulationDto) o;
        return Objects.equals(id, simulationDto.id) &&
            Objects.equals(name, simulationDto.name) &&
            Objects.equals(created, simulationDto.created);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, name, created);
    }

    @Override
    public String toString() {
        return "SimulationDto{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", created=" + created +
            '}';
    }


}
