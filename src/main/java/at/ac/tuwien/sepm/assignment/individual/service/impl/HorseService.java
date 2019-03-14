package at.ac.tuwien.sepm.assignment.individual.service.impl;

import at.ac.tuwien.sepm.assignment.individual.entity.Horse;
import at.ac.tuwien.sepm.assignment.individual.exceptions.NotFoundException;
import at.ac.tuwien.sepm.assignment.individual.persistence.IHorseDao;
import at.ac.tuwien.sepm.assignment.individual.persistence.exceptions.PersistenceException;
import at.ac.tuwien.sepm.assignment.individual.service.IHorseService;
import at.ac.tuwien.sepm.assignment.individual.service.exceptions.InvalidDataException;
import at.ac.tuwien.sepm.assignment.individual.service.exceptions.OutofRangeException;
import at.ac.tuwien.sepm.assignment.individual.service.exceptions.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class HorseService implements IHorseService {

    private static final Logger LOGGER = LoggerFactory.getLogger(HorseService.class);
    private final IHorseDao horseDao;

    @Autowired
    public HorseService(IHorseDao horseDao) {
        this.horseDao = horseDao;
    }

    @Override
    public Horse findOneById(Integer id) throws ServiceException, NotFoundException {
        LOGGER.info("Get horse with id " + id);
        try {
            return horseDao.findOneById(id);
        } catch (PersistenceException e) {
            throw new ServiceException(e.getMessage(), e);
        }
    }

    @Override
    public Horse insertOne(Horse horse) throws ServiceException, OutofRangeException, InvalidDataException {

        try {
            LOGGER.info("Validate data of horse to be inserted");
            invalidHorseInputData(horse);
            LOGGER.info("Insert Horse with following data: " + horse.printOptionals());
            return horseDao.insertOne(horse);
        } catch (PersistenceException e) {
            LOGGER.error("Problem while processing horse");
            throw new ServiceException(e.getMessage(), e);
        }
    }

    @Override
    public Horse updateOneById(Integer id, Horse horse) throws ServiceException, OutofRangeException, InvalidDataException, NotFoundException {
        try {
            LOGGER.info("Validate of horse to be updated");
            invalidHorseUpdateData(horse);
            LOGGER.info("Update horse with id " + id);
            return horseDao.updateOneById(id, horse);
        } catch (PersistenceException e) {
            LOGGER.error("Error while processing horse with id " + id);
            throw new ServiceException(e.getMessage(), e);
        }

    }

    @Override
    public void deleteOneById(Integer id) throws ServiceException, NotFoundException {
        try {
            LOGGER.info("Delete horse with id " + id);
            horseDao.deleteOneById(id);
        } catch (PersistenceException e) {
            LOGGER.error("Error while processing horse with id "+id);
            throw new ServiceException(e.getMessage(), e);
        }
    }

    @Override
    public ArrayList<Horse> getAllOrFiltered(Horse horse) throws ServiceException, NotFoundException {
        LOGGER.info("Get horse/s with following optional parameters: " + horse.printOptionals());
        try {
            return horseDao.getAllOrFiltered(horse);
        } catch (PersistenceException e) {

            throw new ServiceException(e.getMessage(), e);
        }
    }

    private boolean isSpeedOutOfRange(Horse horse) {

        if (horse.getMinSpeed() != null && horse.getMaxSpeed() != null) {
            return (horse.getMinSpeed() < 40.0 || horse.getMaxSpeed() > 60);
        } else if (horse.getMinSpeed() != null) {
            return horse.getMinSpeed() < 40.0;
        } else if (horse.getMaxSpeed() != null) {
            return horse.getMaxSpeed() > 60.0;
        } else return false;

    }

    private boolean isMaxSmallerMin(Horse horse) {
        if (horse.getMinSpeed() != null && horse.getMaxSpeed() != null) {
            return horse.getMinSpeed() <= horse.getMaxSpeed();
        } else return false;
    }

    private void invalidHorseInputData(Horse horse) throws OutofRangeException, InvalidDataException {
        if (isSpeedOutOfRange(horse)) {
            LOGGER.error("Speed is out of range.");
            throw new OutofRangeException("The minimum and maximum speed needs to be between 40 and 60 km/h!");
        } else if (horse.getName().isBlank()) {
            LOGGER.error("Name is not set.");
            throw new InvalidDataException("Name must be set!");
        }
        if (horse.getMinSpeed() == null) {
            LOGGER.error("Min speed is not set.");
            throw new InvalidDataException("Min speed must be set!");
        }
        if (horse.getMaxSpeed() == null) {
            LOGGER.error("Max speed is not set.");
            throw new InvalidDataException("Max speed must be set!");
        } else if (isMaxSmallerMin(horse)) {
            LOGGER.error("Maximum speed is smaller than minimum speed");
            throw new InvalidDataException("Maximum speed needs to be smaller or equal to minimum speed!");
        }
    }

    private void invalidHorseUpdateData(Horse horse) throws InvalidDataException, OutofRangeException {

        if(isSpeedOutOfRange(horse)){
            LOGGER.error("Speed is out of range.");
            throw new OutofRangeException("The minimum and maximum speed needs to be between 40 and 60 km/h!");
        }
        isMaxSmallerMin(horse);
        if(horse.getName()!=null && horse.getName().isBlank()){
            LOGGER.error("Name cannot be empty");
            throw new InvalidDataException("Name cannot be empty!");
        }
    }
}
