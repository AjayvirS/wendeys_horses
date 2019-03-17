package at.ac.tuwien.sepm.assignment.individual.util.mapper;
import at.ac.tuwien.sepm.assignment.individual.entity.Simulation;
import at.ac.tuwien.sepm.assignment.individual.rest.dto.SimulationInputDto;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class SimulationMapper {


    public SimulationInputDto entityToDto(Simulation simulation) {
        return new SimulationInputDto(simulation.getName(), simulation.getSimulationParticipants());
    }
    public Simulation DtoToEntity(SimulationInputDto simulationInputDto){
        return new Simulation(simulationInputDto.getName(), simulationInputDto.getSimulationParticipants());
    }
    public SimulationInputDto[] entitiesToDto(ArrayList<Simulation> simulations){
        SimulationInputDto[] simulationInputDtos = new SimulationInputDto[simulations.size()];

        for (int i = 0; i < simulations.size(); i++) {
            simulationInputDtos[i]= new SimulationInputDto(simulations.get(i).getName(), simulations.get(i).getSimulationParticipants());
        }
        return simulationInputDtos;
    }
}
