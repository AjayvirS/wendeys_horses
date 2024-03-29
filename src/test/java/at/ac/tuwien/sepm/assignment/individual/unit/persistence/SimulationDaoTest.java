package at.ac.tuwien.sepm.assignment.individual.unit.persistence;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import static org.junit.Assert.*;

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
    private static final ArrayList<SimulationParticipant> SIMULATION_PARTICIPANTS= new ArrayList<>();
    private static final ArrayList<SimulationParticipantOutput> TEST_SIMPARTCOMP = new ArrayList<>();
    private static final SimulationParticipantOutput SIMULATION_PARTICIPANT_OUTPUT1 =new SimulationParticipantOutput(1,1,"Don", "Jake", 60.65,
        60.0, 35.5, 1.03f);
    private static final SimulationParticipantOutput SIMULATION_PARTICIPANT_OUTPUT2 =new SimulationParticipantOutput(2,2,"Cat", "Thomas", 88.56,
            55.4, 38.55, 0.99f);
    private static final SimulationParticipantOutput SIMULATION_PARTICIPANT_OUTPUT3 = new SimulationParticipantOutput(3,3,"Cradle", "Shane", 75.23,
        43.4, 48.52, 0.96f);
    private static final SimulationParticipant SIMULATION_PARTICIPANT1=new SimulationParticipant(1,1, 1.03f);
    private static final SimulationParticipant SIMULATION_PARTICIPANT2=new SimulationParticipant(2,2, 0.99f);
    private static final SimulationParticipant SIMULATION_PARTICIPANT3=new SimulationParticipant(2,2, 0.96f);



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


        TEST_SIMPARTCOMP.add(SIMULATION_PARTICIPANT_OUTPUT1);
        TEST_SIMPARTCOMP.add(SIMULATION_PARTICIPANT_OUTPUT2);
        TEST_SIMPARTCOMP.add(SIMULATION_PARTICIPANT_OUTPUT3);

        Simulation tester=new Simulation();

        SIMULATION_PARTICIPANTS.add(SIMULATION_PARTICIPANT1);
        SIMULATION_PARTICIPANTS.add(SIMULATION_PARTICIPANT2);
        SIMULATION_PARTICIPANTS.add(SIMULATION_PARTICIPANT3);
        tester.setId(1);
        tester.setName("Simulation 1");
        tester.setCreated(LocalDateTime.now());
        tester.setSimulationParticipants(SIMULATION_PARTICIPANTS);
        Simulation expected = simulationDao.insertOne(tester, TEST_SIMPARTCOMP);
        assertEquals(expected, tester);
    }

    @Test (expected = NullPointerException.class)
    public void addSimulationDataWithNullParticipantlist() throws PersistenceException, NotFoundException {
        Simulation simulation=new Simulation();
        simulation.setId(1);
        simulation.setName("Simulation 1");
        simulation.setCreated(LocalDateTime.now());
        simulationDao.insertOne(simulation, null);
    }



}
