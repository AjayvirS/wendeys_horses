package at.ac.tuwien.sepm.assignment.individual.rest;

import at.ac.tuwien.sepm.assignment.individual.rest.dto.HorseDto;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

public class JockeyEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(HorseEndpoint.class);
    private static final String BASE_URL = "/api/v1/horses";
    private final IJockeyService jockeyService;
    private final JockeyMapper jockeyMapper;

    @Autowired
    public JockeyEndpoint(IJockeyService jockeyService, JockeyMapper jockeyMapper) {
        this.jockeyService = jockeyService;
        this.jockeyMapper = jockeyMapper;
    }


    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(method = RequestMethod.POST)
    public JockeyDto insertOne(@RequestBody JockeyDto jockeyDto) {
        try {
            return jockeyMapper.entityToDto(jockeyService.insertOne(jockeyMapper.dtoToEntity(jockeyDto)));

        } catch (ServiceException | OutofRangeException | InvalidDataException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error during inserting horse: " + e.getMessage(), e);
        }
    }
}
