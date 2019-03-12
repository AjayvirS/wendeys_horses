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
            LOGGER.info("Check credentials of horse to be inserted");
            checkCredentials(horse);
            invalidHorseInputData(horse);
            LOGGER.info("Insert Horse with following name: " + horse.getName());
            return horseDao.insertOne(horse);
        } catch (PersistenceException e) {
            LOGGER.error("Problem while processing horse");
            throw new ServiceException(e.getMessage(), e);
        }
    }



    @Override
    public Horse updateOneById(Integer id, Horse horse) throws ServiceException, OutofRangeException, InvalidDataException, NotFoundException {
        try {
            LOGGER.info("Check credentials of horse to be updated");
            checkCredentials(horse);
            LOGGER.info("Update horse with id " + id);
            return horseDao.updateOneById(id, horse);
        } catch (PersistenceException e) {
            LOGGER.error("Error while processing horse with id "+id);
            throw new ServiceException(e.getMessage(), e);
        }

    }

    @Override
    public void deleteOneById(Integer id) throws ServiceException, NotFoundException {
        try {
            LOGGER.info("Delete horse with id " + id);
            horseDao.deleteOneById(id);
        } catch (PersistenceException e) {
            LOGGER.error("Error while processing horse");
            throw new ServiceException(e.getMessage(), e);
        }
    }


    @Override
    public ArrayList getAllOrFiltered(String name, String breed, Double minSpeed, Double maxSpeed) throws ServiceException, NotFoundException {
        LOGGER.info("Get horse/s with following optional parameters: " +(name==null?"":"Name: "+name)+
            (breed==null?"":", Breed: "+breed)+(minSpeed==null?"":", min. Speed: "+minSpeed)+(maxSpeed==null?"":"max. Speed: "+maxSpeed));
        try {
            return horseDao.getAllOrFiltered(name, breed, minSpeed, maxSpeed);
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

    private boolean invalidHorseInputData(Horse horse) throws InvalidDataException {
        if(horse.getName() == null){
            throw new InvalidDataException("Name must be set!");
        }
        if(horse.getMinSpeed()==null){
            throw new InvalidDataException("Min speed must be set!");
        }
        if(horse.getMaxSpeed()==null){
            throw new InvalidDataException("Max speed must be set!");
        }
        return true;
    }

    private boolean isMaxSmallerMin(Horse horse){
        if (horse.getMinSpeed()!=null && horse.getMaxSpeed()!=null){
            return horse.getMinSpeed()<=horse.getMaxSpeed();
        } else return false;
    }

    private void checkCredentials(Horse horse) throws OutofRangeException, InvalidDataException {
        if (isSpeedOutOfRange(horse)) {
            LOGGER.error("Speed is out of range.");
            throw new OutofRangeException("The minimum and maximum speed needs to be between 40 and 60 km/h!");
        } else if (invalidHorseInputData(horse)) {
            LOGGER.error("Invalid name.");
            throw new InvalidDataException("Name must be set.");
        } else if(isMaxSmallerMin(horse)){
            LOGGER.error("Maximum speed is larger than minimum speed");
            throw new InvalidDataException("Maximum speed needs to be smaller or equal to minimum speed!");
        }
    }
}
