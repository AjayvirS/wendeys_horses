package at.ac.tuwien.sepm.assignment.individual.rest;

import at.ac.tuwien.sepm.assignment.individual.rest.dto.HorseDto;
import at.ac.tuwien.sepm.assignment.individual.exceptions.NotFoundException;
import at.ac.tuwien.sepm.assignment.individual.service.IHorseService;
import at.ac.tuwien.sepm.assignment.individual.service.exceptions.InvalidDataException;
import at.ac.tuwien.sepm.assignment.individual.service.exceptions.OutofRangeException;
import at.ac.tuwien.sepm.assignment.individual.service.exceptions.ServiceException;
import at.ac.tuwien.sepm.assignment.individual.util.mapper.HorseMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Base64;

@RestController
@RequestMapping("/api/v1/horses")
public class HorseEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(HorseEndpoint.class);
    private static final String BASE_URL = "/api/v1/horses";
    private final IHorseService horseService;
    private final HorseMapper horseMapper;

    @Autowired
    public HorseEndpoint(IHorseService horseService, HorseMapper horseMapper) {
        this.horseService = horseService;
        this.horseMapper = horseMapper;
        LOGGER.debug("Defined Endpoint");

    }


    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public HorseDto getOneById(@PathVariable("id") Integer id) {
        LOGGER.info("GET Horse: " + BASE_URL + "/" + id);
        try {
            return horseMapper.entityToDto(horseService.findOneById(id));
        } catch (ServiceException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error during processing horse with id " + id, e);
        } catch (NotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Error during reading horse: " + e.getMessage(), e);
        }
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(method = RequestMethod.POST)
    public HorseDto insertOne(@RequestBody HorseDto horseDto) {
        LOGGER.info("POST "+ BASE_URL+", Horse: " + horseDto.toString());
        try {
            return horseMapper.entityToDto(horseService.insertOne(horseMapper.dtoToEntity(horseDto)));
        } catch (OutofRangeException | InvalidDataException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error during inserting horse: " + e.getMessage(), e);
        } catch (ServiceException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error during processing horse", e);
        }
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public HorseDto updateOneById(@PathVariable("id") Integer id, @RequestBody HorseDto horseDto) {
        LOGGER.info("PUT "+BASE_URL+", Horse: " + (horseDto.getName() == null ? "" : "Name: " + horseDto.getName()) +
            (horseDto.getBreed() == null ? "" : ", Breed: " + horseDto.getBreed()) + (horseDto.getMinSpeed() == null ? "" : ", min. Speed: " + horseDto.getMinSpeed()) + (horseDto.getMaxSpeed() == null ? "" : "max. Speed: " + horseDto.getMaxSpeed()));
        try {
            return horseMapper.entityToDto(horseService.updateOneById(id, horseMapper.dtoToEntity(horseDto)));
        } catch (ServiceException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error during processing horse with id " + id, e);
        } catch (OutofRangeException | InvalidDataException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error during updating horse: " + e.getMessage(), e);
        } catch (NotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Error during updating horse: " + e.getMessage(), e);
        }
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public void deleteOneById(@PathVariable("id") Integer id) {
        LOGGER.info("DELETE " + BASE_URL + "/" + id);
        try {
            horseService.deleteOneById(id);
        } catch (NotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Error during deleting horse: " + e.getMessage(), e);
        } catch (ServiceException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error during processing horse with id " + id, e);
        }
    }

    @RequestMapping(method = RequestMethod.GET)
    public HorseDto[] getAllorFiltered(HorseDto horseDto) {

        LOGGER.info("GET ALL "+BASE_URL+(allNull(horseDto) ?"":"Filtered: ")+ (horseDto.getName() == null ? "" : "Name: " + horseDto.getName()) +
            (horseDto.getBreed() == null ? "" : ", Breed: " + horseDto.getBreed()) + (horseDto.getMinSpeed() == null ? "" : ", min. Speed: " + horseDto.getMinSpeed()) + (horseDto.getMaxSpeed() == null ? "" : "max. Speed: " + horseDto.getMaxSpeed()));
        try {
            return horseMapper.entitiesToDto(horseService.getAllOrFiltered(horseMapper.dtoToEntity(horseDto)));
        } catch (ServiceException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error during processing horses with following optional " + horseMapper.dtoToEntity(horseDto).printOptionals());
        }
    }

    private boolean allNull(HorseDto horseDto) {
        return (horseDto.getName()==null && horseDto.getBreed()==null && horseDto.getMinSpeed()==null && horseDto.getMaxSpeed()==null);
    }


}
