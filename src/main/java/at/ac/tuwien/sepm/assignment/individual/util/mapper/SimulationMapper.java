package at.ac.tuwien.sepm.assignment.individual.util.mapper;
import at.ac.tuwien.sepm.assignment.individual.entity.Simulation;
import at.ac.tuwien.sepm.assignment.individual.entity.SimulationParticipantOutput;
import at.ac.tuwien.sepm.assignment.individual.rest.dto.SimulationInputDto;
import at.ac.tuwien.sepm.assignment.individual.rest.dto.SimulationOutputDto;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class SimulationMapper {


    public SimulationOutputDto entityToDto(Simulation simulation) {


        return new SimulationOutputDto(simulation.getId(),simulation.getName(),simulation.getCreated(), simulation.getSimulationParticipantsCompleted());
    }
    public Simulation DtoToEntity(SimulationInputDto simulationInputDto){
        return new Simulation(simulationInputDto.getName(), simulationInputDto.getSimulationParticipants());
    }
    public ArrayList<SimulationOutputDto> entitiesToDto(ArrayList<Simulation> simulations){
        ArrayList<SimulationOutputDto> simulationOutputDtos= new ArrayList<>();

        for (int i = 0; i < simulations.size(); i++) {
                simulationOutputDtos.add(entityToDto(simulations.get(i)));
        }
        return simulationOutputDtos;
    }

    public Simulation dtoToEntity(SimulationInputDto simulationInputDto) {
        return new Simulation(simulationInputDto.getName(), simulationInputDto.getSimulationParticipants());
    }
}
