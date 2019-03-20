package at.ac.tuwien.sepm.assignment.individual.unit.persistence;

import at.ac.tuwien.sepm.assignment.individual.entity.Jockey;
import at.ac.tuwien.sepm.assignment.individual.entity.Simulation;
import at.ac.tuwien.sepm.assignment.individual.entity.SimulationParticipant;
import at.ac.tuwien.sepm.assignment.individual.entity.SimulationParticipantOutput;
import at.ac.tuwien.sepm.assignment.individual.exceptions.NotFoundException;
import at.ac.tuwien.sepm.assignment.individual.persistence.IHorseDao;
import at.ac.tuwien.sepm.assignment.individual.persistence.IJockeyDao;
import at.ac.tuwien.sepm.assignment.individual.persistence.ISimulationDao;
import at.ac.tuwien.sepm.assignment.individual.persistence.exceptions.PersistenceException;
import at.ac.tuwien.sepm.assignment.individual.persistence.util.DBConnectionManager;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.internal.matchers.Not;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.ArrayList;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = "test")
public class SimulationDaoTest {

    @Autowired
    ISimulationDao simulationDao;
    @Autowired
    IHorseDao horseDao;
    @Autowired
    IJockeyDao jockeyDao;
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
        simulationDao.getOneById(1);
    }

    @Test
    public void addSimulationData_whenInsertingSimulation_thenGetSimulationData() throws PersistenceException, NotFoundException {
        ArrayList<SimulationParticipantOutput> simulationParticipantsCompleted= new ArrayList<>();

        simulationParticipantsCompleted.add(new SimulationParticipantOutput(1,1,"Don", "Jake", 60.65,
        60.0, 35.5, 1.03f));

        simulationParticipantsCompleted.add(new SimulationParticipantOutput(2,2,"Cat", "Thomas", 88.56,
        55.4, 38.55, 0.99f));
        simulationParticipantsCompleted.add(new SimulationParticipantOutput(3,3,"Cradle", "Shane", 75.23,
        43.4, 48.52, 0.96f));

        Simulation simulation=new Simulation();
        ArrayList<SimulationParticipant> simulationParticipants= new ArrayList<>();
        simulationParticipants.add(new SimulationParticipant(1,1, 1.03f));
        simulationParticipants.add(new SimulationParticipant(2,2, 0.99f));
        simulationParticipants.add(new SimulationParticipant(2,2, 0.96f));

        simulation.setId(1);
        simulation.setName("Simulation 1");
        simulation.setCreated(LocalDateTime.now());
        simulation.setSimulationParticipants(simulationParticipants);
        simulationDao.insertOne(simulation, simulationParticipantsCompleted);
    }

    @Test (expected = NullPointerException.class)
    public void addSimulationDataWithNullParticipantlist() throws PersistenceException, NotFoundException {
        ArrayList<SimulationParticipantOutput> simulationParticipantsCompleted= new ArrayList<>();

        Simulation simulation=new Simulation();
        simulation.setId(1);
        simulation.setName("Simulation 1");
        simulation.setCreated(LocalDateTime.now());
        simulationDao.insertOne(simulation, simulationParticipantsCompleted);
    }



}
