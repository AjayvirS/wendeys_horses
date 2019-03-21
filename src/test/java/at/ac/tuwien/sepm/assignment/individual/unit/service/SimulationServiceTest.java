package at.ac.tuwien.sepm.assignment.individual.unit.service;

import at.ac.tuwien.sepm.assignment.individual.entity.*;
import at.ac.tuwien.sepm.assignment.individual.exceptions.NotFoundException;
import at.ac.tuwien.sepm.assignment.individual.persistence.exceptions.PersistenceException;
import at.ac.tuwien.sepm.assignment.individual.persistence.util.DBConnectionManager;
import at.ac.tuwien.sepm.assignment.individual.service.IHorseService;
import at.ac.tuwien.sepm.assignment.individual.service.IJockeyService;
import at.ac.tuwien.sepm.assignment.individual.service.ISimulationService;
import at.ac.tuwien.sepm.assignment.individual.service.exceptions.InvalidDataException;
import at.ac.tuwien.sepm.assignment.individual.service.exceptions.OutofRangeException;
import at.ac.tuwien.sepm.assignment.individual.service.exceptions.ServiceException;
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
public class SimulationServiceTest {

    @Autowired
    ISimulationService simulationService;

    @Autowired
    IHorseService horseService;

    @Autowired
    IJockeyService jockeyService;

    @Autowired
    DBConnectionManager dbConnectionManager;

    /**
     * It is important to close the database connection after each test in order to clean the in-memory database
     */
    @After
    public void afterEachTest() throws PersistenceException {
        dbConnectionManager.closeConnection();
    }

    @Test(expected = OutofRangeException.class)
    public void giveOutOfRangeLuckFactor_getOutOfRangeException()
        throws NotFoundException, InvalidDataException, ServiceException, OutofRangeException {
        Simulation simulation= new Simulation();
        simulation.setName("Simulation 1");
        ArrayList<SimulationParticipant> simulationParticipants= new ArrayList<>();
        simulationParticipants.add(new SimulationParticipant(1, 1, 1.06f));
        simulation.setSimulationParticipants(simulationParticipants);
        simulationService.insertOne(simulation);
    }

    @Test (expected=InvalidDataException.class)
    public void givenInsertedSimulationWithNullParticipantList_getInvalidDataException() throws ServiceException, OutofRangeException, InvalidDataException, NotFoundException {
        Simulation simulation= new Simulation();
        simulation.setSimulationParticipants(null);
        simulation.setName("Simulation1");
        simulationService.insertOne(simulation);

    }

    @Test
    public void insertSimulationData_AssertRoundedTo4Digits() throws InvalidDataException, ServiceException, OutofRangeException, NotFoundException {
        Horse testerHorse= new Horse(null, "Don", "American", 40.0, 60.0, null, null);
        Jockey testerJockey=new Jockey(null, "Marco", 55.0, null, null);
        horseService.insertOne(testerHorse);
        jockeyService.insertOne(testerJockey);

        ArrayList<SimulationParticipant> simulationParticipants= new ArrayList<>();
        simulationParticipants.add(new SimulationParticipant(1,1,1.02f));
        Simulation simulation= new Simulation("SimulationTest", simulationParticipants);

        simulation=simulationService.insertOne(simulation);
        SimulationParticipantOutput compParts;
        for (int i = 0; i < simulation.getSimulationParticipantsCompleted().size(); i++) {
            compParts=simulation.getSimulationParticipantsCompleted().get(i);
            assertEquals("avgSpeed rounded HALF UP to 4 digits", 4, String.valueOf(compParts.getAvgSpeed()).split("\\.")[1].length());
            assertEquals("skill rounded HALF UP to 4 digits", 4, String.valueOf(compParts.getSkill()).split("\\.")[1].length());
        }
    }

    @Test
    public void getAnySimulationWithJockeySkill0_calculatedSkillAlways1() throws InvalidDataException, ServiceException, OutofRangeException, NotFoundException {
        final double skill=0.0;
        Horse testerHorse= new Horse(null, "Don", "American", 45.0, 55.3, null, null);
        Jockey testerJockey=new Jockey(null, "Marco", skill, null, null);

        Horse testerHorse2= new Horse(null, "Dragon", "Arabian", 48.21, 52.33, null, null);
        Jockey testerJockey2=new Jockey(null, "Eric", skill, null, null);

        Horse testerHorse3= new Horse(null, "Bullet", "Arabian", 40.0, 60.0, null, null);
        Jockey testerJockey3=new Jockey(null, "Marco", skill, null, null);


        horseService.insertOne(testerHorse);
        jockeyService.insertOne(testerJockey);
        horseService.insertOne(testerHorse2);
        jockeyService.insertOne(testerJockey2);
        horseService.insertOne(testerHorse3);
        jockeyService.insertOne(testerJockey3);

        ArrayList<SimulationParticipant> simulationParticipants= new ArrayList<>();
        simulationParticipants.add(new SimulationParticipant(1,1,1.02f));
        simulationParticipants.add(new SimulationParticipant(2,2,1.01f));
        simulationParticipants.add(new SimulationParticipant(3,3,1.0f));
        Simulation simulation= new Simulation("SimulationTest", simulationParticipants);

        simulation=simulationService.insertOne(simulation);
        SimulationParticipantOutput compParts;
        Double expected=1.0;
        for (int i = 0; i < simulation.getSimulationParticipantsCompleted().size(); i++) {
            compParts=simulation.getSimulationParticipantsCompleted().get(i);
            assertEquals("Calculated Skill is always 1", expected, compParts.getSkill());

        }

    }

}
