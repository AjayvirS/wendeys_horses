package at.ac.tuwien.sepm.assignment.individual.service.impl;

import at.ac.tuwien.sepm.assignment.individual.entity.Horse;
import at.ac.tuwien.sepm.assignment.individual.entity.Jockey;
import at.ac.tuwien.sepm.assignment.individual.persistence.IHorseDao;
import at.ac.tuwien.sepm.assignment.individual.persistence.IJockeyDao;
import at.ac.tuwien.sepm.assignment.individual.persistence.exceptions.PersistenceException;
import at.ac.tuwien.sepm.assignment.individual.service.IJockeyService;
import at.ac.tuwien.sepm.assignment.individual.service.exceptions.InvalidDataException;
import at.ac.tuwien.sepm.assignment.individual.service.exceptions.OutofRangeException;
import at.ac.tuwien.sepm.assignment.individual.service.exceptions.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JockeyService implements IJockeyService {

    private static final Logger LOGGER = LoggerFactory.getLogger(HorseService.class);
    private final IJockeyDao jockeyDao;

    @Autowired
    public JockeyService(IJockeyDao jockeyDao) {
        this.jockeyDao = jockeyDao;
    }


    @Override
    public Jockey insertOne(Jockey jockey) throws ServiceException, OutofRangeException, InvalidDataException {


        try {
            LOGGER.info("Validate data of jockey to be inserted");
            invalidJockeyInputData(jockey);
            LOGGER.info("Insert jockey with following data: " + jockey.printOptionals());
            return jockeyDao.insertOne(jockey);
        } catch (PersistenceException e) {
            LOGGER.error("Problem while processing jockey");
            throw new ServiceException(e.getMessage(), e);
        }
    }



    private void invalidJockeyInputData(Jockey jockey) throws OutofRangeException, InvalidDataException {
        if(jockey.getName().isBlank()){
            throw new InvalidDataException("Name must be set.");
        }
    }
}
