package at.ac.tuwien.sepm.assignment.individual.rest;

import at.ac.tuwien.sepm.assignment.individual.exceptions.NotFoundException;
import at.ac.tuwien.sepm.assignment.individual.rest.dto.JockeyDto;
import at.ac.tuwien.sepm.assignment.individual.service.IJockeyService;
import at.ac.tuwien.sepm.assignment.individual.service.exceptions.InvalidDataException;
import at.ac.tuwien.sepm.assignment.individual.service.exceptions.OutofRangeException;
import at.ac.tuwien.sepm.assignment.individual.service.exceptions.ServiceException;
import at.ac.tuwien.sepm.assignment.individual.util.mapper.JockeyMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


@RestController
@RequestMapping("/api/v1/jockeys")
public class JockeyEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(JockeyEndpoint.class);
    private static final String BASE_URL = "/api/v1/jockeys";
    private final IJockeyService jockeyService;
    private final JockeyMapper jockeyMapper;

    @Autowired
    public JockeyEndpoint(IJockeyService jockeyService, JockeyMapper jockeyMapper) {
        this.jockeyService = jockeyService;
        this.jockeyMapper = jockeyMapper;
        LOGGER.debug("Defined Endpoint");
    }


    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(method = RequestMethod.POST)
    public JockeyDto insertOne(@RequestBody JockeyDto jockeyDto) {
        LOGGER.info("POST "+ BASE_URL+", Jockey: " + jockeyDto.toString());

        try {
            return jockeyMapper.entityToDto(jockeyService.insertOne(jockeyMapper.dtoToEntity(jockeyDto)));
        } catch ( OutofRangeException | InvalidDataException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error during inserting jockey: " + e.getMessage(), e);
        } catch (ServiceException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error during processing jockey" , e);
        }
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public JockeyDto updateOneById(@PathVariable("id") Integer id, @RequestBody JockeyDto jockeyDto) {
        LOGGER.info("PUT "+BASE_URL+", Horse: " + (jockeyDto.getName() == null ? "" : "Name: " + jockeyDto.getName()) +
            (jockeyDto.getSkill() == null ? "" : ", Skill: " + jockeyDto.getSkill()));
        try {
            return jockeyMapper.entityToDto(jockeyService.updateOneById(id, jockeyMapper.dtoToEntity(jockeyDto)));
        } catch (ServiceException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error during processing jockey with id " + id, e);
        } catch (InvalidDataException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error during updating jockey: " + e.getMessage(), e);
        } catch (NotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Error during updating jockey: " + e.getMessage(), e);
        }
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public void deleteOneById(@PathVariable("id") Integer id) {
        LOGGER.info("DELETE " + BASE_URL + "/" + id);
        try {
            jockeyService.deleteOneById(id);
        } catch (NotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Error during deleting jockey: " + e.getMessage(), e);
        } catch (ServiceException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error during processing jockey with id " + id, e);
        }
    }


    @RequestMapping(method = RequestMethod.GET)
    public JockeyDto[] getAllorFiltered(JockeyDto jockeyDto) {

        LOGGER.info("GET ALL "+BASE_URL+(allNull(jockeyDto) ?":":"Filtered: ")+ (jockeyDto.getName() == null ? "" : "Name: " + jockeyDto.getName()) +
            (jockeyDto.getSkill() == null ? "" : ", Skill: " + jockeyDto.getSkill()));
        try {
            return jockeyMapper.entitiesToDto(jockeyService.getAllOrFiltered(jockeyMapper.dtoToEntity(jockeyDto)));
        } catch (ServiceException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error during processing jockeys with following optional " + jockeyMapper.dtoToEntity(jockeyDto).printOptionals());
        }
    }

    private boolean allNull(JockeyDto jockeyDto) {
        return (jockeyDto.getSkill()==null && jockeyDto.getName()==null);
    }


    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public JockeyDto getOneById(@PathVariable("id") Integer id) {
        LOGGER.info("GET Jockey: " + BASE_URL + "/" + id);
        try {
            return jockeyMapper.entityToDto(jockeyService.findOneById(id));
        } catch (ServiceException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error during processing jockey with id " + id, e);
        } catch (NotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Error during reading jockey: " + e.getMessage(), e);
        }
    }
}