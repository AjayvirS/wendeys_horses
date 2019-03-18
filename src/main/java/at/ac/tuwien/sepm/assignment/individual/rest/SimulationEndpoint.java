package at.ac.tuwien.sepm.assignment.individual.rest;

import at.ac.tuwien.sepm.assignment.individual.exceptions.NotFoundException;
import at.ac.tuwien.sepm.assignment.individual.persistence.exceptions.PersistenceException;
import at.ac.tuwien.sepm.assignment.individual.rest.dto.SimulationInputDto;
import at.ac.tuwien.sepm.assignment.individual.rest.dto.SimulationOutputDto;
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
    public SimulationEndpoint(ISimulationService simulationService, SimulationMapper simulationMapper) {
        this.simulationService = simulationService;
        this.simulationMapper = simulationMapper;
        LOGGER.info("Defined Endpoint");
    }


    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(method = RequestMethod.POST)
    public SimulationOutputDto insertOne(@RequestBody SimulationInputDto simulationInputDto) {
        LOGGER.info("POST simulation: " + BASE_URL);
        try {
            return simulationMapper.entityToDto(simulationService.insertOne(simulationMapper.DtoToEntity(simulationInputDto)));
        } catch (OutofRangeException | NotFoundException | InvalidDataException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error during inserting simulation: " + e.getMessage(), e);
        } catch (ServiceException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error during processing simulation", e);
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{id}")
    public SimulationOutputDto getOneById(@PathVariable Integer id) {
        LOGGER.info("GET Simulation: " + BASE_URL + "/" + id);
        try {
            return simulationMapper.entityToDto(simulationService.getOneById(id));
        } catch (ServiceException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error during processing simulation", e);
        } catch (NotFoundException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error during inserting simulation: " + e.getMessage(), e);
        }
    }


}
