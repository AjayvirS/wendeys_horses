package at.ac.tuwien.sepm.assignment.individual.util.mapper;
import at.ac.tuwien.sepm.assignment.individual.entity.Simulation;
import at.ac.tuwien.sepm.assignment.individual.rest.dto.SimulationDto;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class SimulationMapper {


    public SimulationDto entityToDto(Simulation simulation) {
        return new SimulationDto(simulation.getName(), simulation.getSimulationParticipants());
    }
    public Simulation DtoToEntity(SimulationDto simulationDto){
        return new Simulation(simulationDto.getName(), simulationDto.getSimulationParticipants());
    }
    public SimulationDto[] entitiesToDto(ArrayList<Simulation> simulations){
        SimulationDto[]simulationDtos= new SimulationDto[simulations.size()];

        for (int i = 0; i < simulations.size(); i++) {
            simulationDtos[i]= new SimulationDto(simulations.get(i).getName(), simulations.get(i).getSimulationParticipants());
        }
        return simulationDtos;
    }
}
