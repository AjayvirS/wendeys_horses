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
        LOGGER.info("Service-Layer: Get horse with id " + id);
        try {
            return horseDao.findOneById(id);
        } catch (PersistenceException e) {
            throw new ServiceException(e.getMessage(), e);
        }
    }

    @Override
    public Horse insertOne(Horse horse) throws ServiceException, OutofRangeException, InvalidDataException {

        try {
            if(horse.getMinSpeed()<40.0 || horse.getMaxSpeed()>60){
                LOGGER.error("Service-Layer: Speed is out of range.");
                throw new OutofRangeException("The minimum and maximum speed needs to be between 40 and 60 km/h!");
            } else if(horse.getName()==null || horse.getName().isBlank()){
                LOGGER.error("Service-Layer: Invalid name.");
                throw new InvalidDataException();

            }
            LOGGER.info("Service-Layer: Store Horse with following name: "+horse.getName());
            return horseDao.insertOne(horse);
        } catch (PersistenceException e) {
            LOGGER.error("Problem while processing horse");
            throw new ServiceException(e.getMessage(), e);
        }
    }
}
