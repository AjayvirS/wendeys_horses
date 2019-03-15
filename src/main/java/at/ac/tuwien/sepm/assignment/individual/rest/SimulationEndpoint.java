package at.ac.tuwien.sepm.assignment.individual.rest;
import at.ac.tuwien.sepm.assignment.individual.rest.dto.SimulationDto;
import at.ac.tuwien.sepm.assignment.individual.service.ISimulationService;
import at.ac.tuwien.sepm.assignment.individual.service.exceptions.InvalidDataException;
import at.ac.tuwien.sepm.assignment.individual.service.exceptions.OutofRangeException;
import at.ac.tuwien.sepm.assignment.individual.service.exceptions.ServiceException;
import at.ac.tuwien.sepm.assignment.individual.util.mapper.SimulationMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


@RestController
@RequestMapping("/api/v1/simulations")
public class SimulationEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimulationEndpoint.class);
    private static final String BASE_URL = "/api/v1/simulations";
    private final ISimulationService simulationService;
    private final SimulationMapper simulationMapper;


    @Autowired
    public SimulationEndpoint(ISimulationService simulationService, SimulationMapper simulationMapper){
        this.simulationService=simulationService;
        this.simulationMapper=simulationMapper;
    }


    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(method = RequestMethod.POST)
    public SimulationDto insertOne(@RequestBody SimulationDto simulationDto) {
        try {
            return simulationMapper.entityToDto(simulationService.insertOne(simulationMapper.DtoToEntity(simulationDto)));
        } catch (ServiceException e) {
            e.printStackTrace();
        }
        return null;
    }

}
