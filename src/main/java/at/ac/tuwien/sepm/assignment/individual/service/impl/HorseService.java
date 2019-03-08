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
            checkCredentials(horse);
            LOGGER.info("Store Horse with following name: "+horse.getName());
            return horseDao.insertOne(horse);
        } catch (PersistenceException e) {
            LOGGER.error("Problem while processing horse");
            throw new ServiceException(e.getMessage(), e);
        }
    }

    private void checkCredentials(Horse horse) throws OutofRangeException, InvalidDataException {
        if(speedOutOfRange(horse)){
            LOGGER.error("Speed is out of range.");
            throw new OutofRangeException("The minimum and maximum speed needs to be between 40 and 60 km/h!");
        } else if(invalidHorseName(horse)){
            LOGGER.error("Invalid name.");
            throw new InvalidDataException("Name must be set.");
        }
    }

    @Override
    public Horse updateOneById(Integer id, Horse horse) throws ServiceException, OutofRangeException, InvalidDataException, NotFoundException{
        try{
            checkCredentials(horse);
            LOGGER.info("Updating horse with id "+ id);
            return horseDao.updateOneById(id, horse);
        } catch (PersistenceException e){
            LOGGER.error("Error while processing horse");
            throw new ServiceException(e.getMessage(), e);
        }

    }

    private boolean speedOutOfRange(Horse horse){
        return (horse.getMinSpeed()<40.0 || horse.getMaxSpeed()>60);

    }
    private boolean invalidHorseName(Horse horse){
        return horse.getName()==null || horse.getName().isBlank();
    }
}
