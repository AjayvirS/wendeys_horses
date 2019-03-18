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

        SimulationParticipantOutput[]simPartsComp= new SimulationParticipantOutput[simulation.getSimulationParticipantsCompleted().size()];

        for (int i = 0; i < simPartsComp.length; i++) {

            simPartsComp[i]=simulation.getSimulationParticipantsCompleted().get(i);

        }

        return new SimulationOutputDto(simulation.getId(),simulation.getName(),simulation.getCreated(), simPartsComp);
    }
    public Simulation DtoToEntity(SimulationInputDto simulationInputDto){
        return new Simulation(simulationInputDto.getName(), simulationInputDto.getSimulationParticipants());
    }
    public SimulationOutputDto[] entitiesToDto(ArrayList<Simulation> simulations){
        SimulationOutputDto[] simulationOutputDtos = new SimulationOutputDto[simulations.size()];

        for (int i = 0; i < simulations.size(); i++) {
            simulationOutputDtos[i]= new SimulationOutputDto(simulations.get(i).getId(),simulations.get(i).getName(), simulations.get(i).getCreated(),null);
        }
        return simulationOutputDtos;
    }

    public Simulation dtoToEntity(SimulationInputDto simulationInputDto) {
        return new Simulation(simulationInputDto.getName(), simulationInputDto.getSimulationParticipants());
    }
}
