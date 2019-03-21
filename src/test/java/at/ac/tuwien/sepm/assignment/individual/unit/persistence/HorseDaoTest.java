package at.ac.tuwien.sepm.assignment.individual.unit.persistence;

import at.ac.tuwien.sepm.assignment.individual.entity.Horse;
import at.ac.tuwien.sepm.assignment.individual.exceptions.NotFoundException;
import at.ac.tuwien.sepm.assignment.individual.persistence.IHorseDao;
import at.ac.tuwien.sepm.assignment.individual.persistence.exceptions.PersistenceException;
import at.ac.tuwien.sepm.assignment.individual.persistence.util.DBConnectionManager;
import at.ac.tuwien.sepm.assignment.individual.service.exceptions.InvalidDataException;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = "test")
public class HorseDaoTest {

    @Autowired
    IHorseDao horseDao;
    @Autowired
    DBConnectionManager dbConnectionManager;

    /**
     * It is important to close the database connection after each test in order to clean the in-memory database
     */
    @After
    public void afterEachTest() throws PersistenceException {
        dbConnectionManager.closeConnection();
    }

    @Test(expected = NotFoundException.class)
    public void givenNothing_whenFindHorseByIdWhichNotExists_thenNotFoundException()
        throws PersistenceException, NotFoundException {
        horseDao.findOneById(1);
    }

    @Test
    public void WhenInsertHorse_giveHorseByIdAndFindHorseById() throws PersistenceException{
        Horse testerObj = new Horse(null, "Donald", "American", 50.0, 55.0, null, null);
        Horse expectedObj=horseDao.insertOne(testerObj);
        assertEquals(testerObj,expectedObj);
    }

    @Test(expected= NotFoundException.class)
    public void WhenDeleteNotExistingHorse_thenNotFoundException() throws PersistenceException, NotFoundException{
        horseDao.deleteOneById(1);
    }

    @Test(expected = InvalidDataException.class)
    public void WhenUpdatedMinSpeed_XOR_MaxSpeed_AndMAXSmallerMin_thengetInvalidDataException() throws PersistenceException, NotFoundException, InvalidDataException {
        horseDao.insertOne(new Horse(null, "Donald", "American", 50.0, 55.0, null, null));
        horseDao.updateOneById(1, new Horse(null,null,null,56.0,null,null,null));
    }

}

