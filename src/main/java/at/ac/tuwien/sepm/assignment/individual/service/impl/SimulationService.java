package at.ac.tuwien.sepm.assignment.individual.service.impl;


import at.ac.tuwien.sepm.assignment.individual.entity.*;
import at.ac.tuwien.sepm.assignment.individual.exceptions.NotFoundException;
import at.ac.tuwien.sepm.assignment.individual.persistence.IHorseDao;
import at.ac.tuwien.sepm.assignment.individual.persistence.IJockeyDao;
import at.ac.tuwien.sepm.assignment.individual.persistence.ISimulationDao;
import at.ac.tuwien.sepm.assignment.individual.persistence.exceptions.PersistenceException;
import at.ac.tuwien.sepm.assignment.individual.persistence.impl.HorseDao;
import at.ac.tuwien.sepm.assignment.individual.persistence.impl.JockeyDao;
import at.ac.tuwien.sepm.assignment.individual.service.ISimulationService;
import at.ac.tuwien.sepm.assignment.individual.service.exceptions.InvalidDataException;
import at.ac.tuwien.sepm.assignment.individual.service.exceptions.OutofRangeException;
import at.ac.tuwien.sepm.assignment.individual.service.exceptions.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class SimulationService implements ISimulationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimulationService.class);
    private final ISimulationDao simulationDao;
    private final IJockeyDao jockeyDao;
    private final IHorseDao horseDao;

    @Autowired
    public SimulationService(ISimulationDao simulationDao, IJockeyDao jockeyDao, IHorseDao horseDao) {
        this.simulationDao = simulationDao;
        this.horseDao = horseDao;
        this.jockeyDao = jockeyDao;

    }

    @Override
    public Simulation insertOne(Simulation simulation) throws ServiceException, InvalidDataException, OutofRangeException, NotFoundException {
        LOGGER.info("Post Simulation with name: " + simulation.getName() + " and participants: " + simulation.getSimulationParticipants());

        try {
            LOGGER.info("Validate data of simulation to be inserted");
            validateData(simulation);
            LOGGER.info("Prepare Simulation and calculate values based on horse speed, luck and skill");
            DataHolder objs = getCorrectHorsesAndJockeys(simulation);

            ArrayList<SimulationParticipantOutput> completeds = calculateData(objs.getHorsesByID(), objs.getJockeysByID(), simulation);

            LOGGER.info("Insert simulation with following data: " + "Name: " + simulation.getName() + ", Participants: " + simulation.getSimulationParticipants());
            Simulation compSim = simulationDao.insertOne(simulation, completeds);


            sortByRank(completeds);

            compSim.setSimulationParticipantsCompleted(completeds);


            return compSim;
        } catch (PersistenceException e) {
            LOGGER.error("Problem while processing simulation");
            throw new ServiceException(e.getMessage(), e);
        }
    }



    @Override
    public Simulation getOneById(Integer id) throws NotFoundException, ServiceException {
        LOGGER.info("Get Simulation with id " + id);
        Simulation simulation;

        try {
            simulation = simulationDao.getOneById(id);
            ArrayList<SimulationParticipantOutput> completeds = completeSimulation(simulation);
            simulation.setSimulationParticipantsCompleted(completeds);
            return simulation;

        } catch (PersistenceException e) {
            LOGGER.error("Problem while processing simulation");
            throw new ServiceException(e.getMessage(), e);
        }

    }


    @Override
    public ArrayList<Simulation> getAllOrFiltered(Simulation simulation) throws ServiceException {
        LOGGER.info("Get simulation/s with following optional parameter: " + (simulation.getName() == null ? "" : simulation.getName()));
        try {
            return simulationDao.getAllOrFiltered(simulation);
        } catch (PersistenceException e) {
            LOGGER.error("Error while processing simulation with following optional parameters: " + simulation.getName());
            throw new ServiceException(e.getMessage(), e);
        }
    }


    /**
     *
     * @param simulation for filling simulation with participants and calculating values of completed race participants
     * @return list of values of horse/jockey combinations and respective calculated values
     * @throws PersistenceException if something goes wrong with database access, caught by parent method
     * @throws NotFoundException if simulation with id is not found
     */
    private ArrayList<SimulationParticipantOutput> completeSimulation(Simulation simulation) throws PersistenceException, NotFoundException {
        DataHolder objs = getCorrectHorsesAndJockeys(simulation);
        ArrayList<SimulationParticipantOutput> completeds = calculateData(objs.getHorsesByID(), objs.getJockeysByID(), simulation);
        sortByRank(completeds);

        return completeds;
    }


    /**
     *
     * @param simulation containing ids of horses and jockeys to get
     * @return Dcorrect version of horses and jockeys packed in dataholder object if changes were made to horse/jockey after simulation
     * @throws PersistenceException if something goes wrong with database access, caught by parent method
     * @throws NotFoundException if s
     */
    private DataHolder getCorrectHorsesAndJockeys(Simulation simulation) throws PersistenceException, NotFoundException {

        HashMap<Integer, Horse> horseByIDs;
        HashMap<Integer, Jockey> jockeyByIDs;
        Integer[] horseIDs = new Integer[simulation.getSimulationParticipants().size()];
        Integer[] jockeyIDs = new Integer[simulation.getSimulationParticipants().size()];
        for (int i = 0; i < simulation.getSimulationParticipants().size(); i++) {
            ArrayList<SimulationParticipant> tempArr = simulation.getSimulationParticipants();

            horseIDs[i] = tempArr.get(i).getHorseId();
            jockeyIDs[i] = tempArr.get(i).getJockeyId();

        }
        horseByIDs = ((HorseDao) horseDao).getCorrectHorsesForSimulation(simulation, horseIDs);
        jockeyByIDs = ((JockeyDao) jockeyDao).getCorrectJockeysForSimulation(simulation, jockeyIDs);
        return new DataHolder(horseByIDs, jockeyByIDs);

    }


    /*
        gets list of participants with NULL field for ranks
        orders them by avgSpeed descending and gives them a rank
     */
    private void sortByRank(ArrayList<SimulationParticipantOutput> completeds) {
        Collections.sort(completeds, Comparator.comparingDouble(SimulationParticipantOutput::getAvgSpeed));
        Collections.reverse(completeds);
        LOGGER.info("Simulation data calculated and sorted by highest average Speed");
        LOGGER.info("Set rank of participants");
        int tempRank = 1;
        completeds.get(0).setRank(tempRank);
        for (int i = 1; i < completeds.size(); i++) {
            if (completeds.get(i).getAvgSpeed() < completeds.get(i - 1).getAvgSpeed()) {
                tempRank++;
            }
            completeds.get(i).setRank(tempRank);
        }
    }


    /*
        Calculates data by taking the horsejockey combinations of IDs retrieved by simulationservice.getSimulationById
        and uses them as key for hashmap of horses and jockeys to calculate other values using the correct
        combination of horses and jockeys
     */
    private ArrayList<SimulationParticipantOutput> calculateData
        (HashMap<Integer, Horse> horses, HashMap<Integer, Jockey> jockeys, Simulation simulation) {
        Double p_min, p_max, k, p, k2, d;
        Float g;
        ArrayList<SimulationParticipantOutput> completeds = new ArrayList<>();

        //according to oracle javadoc to prevent comma: use nf and change locale where dot is used as decimal point
        //and cast to decimalformat
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
        DecimalFormat df = (DecimalFormat) nf;
        df.applyPattern("#.####");
        df.setRoundingMode(RoundingMode.HALF_UP);
        String horseName = null, jockeyName = null;

        for (int i = 0; i < simulation.getSimulationParticipants().size(); i++) {
            SimulationParticipant simpart = simulation.getSimulationParticipants().get(i);
            p_min = horses.get(simpart.getHorseId()).getMinSpeed();
            p_max = horses.get(simpart.getHorseId()).getMaxSpeed();
            k = jockeys.get(simpart.getJockeyId()).getSkill();
            g = simpart.getLuckFactor();
            p = (g - 0.95) * ((p_max - p_min) / (1.05 - 0.95)) + p_min;
            k2 = 1 + (0.15 * 1 / Math.PI * Math.atan((1 / 5f) * k));
            p = Double.valueOf(df.format(p));
            k2 = Double.valueOf(df.format(k2));
            d = k2 * p * g;
            d = Double.valueOf(df.format(d));
            jockeyName = jockeys.get(simpart.getJockeyId()).getName();
            horseName = horses.get(simpart.getHorseId()).getName();
            completeds.add(new SimulationParticipantOutput(simpart.getId(), null, horseName, jockeyName, d, p, k2, g));
        }

        return completeds;

    }

    //below method validates input data and only send it to persistence layer if input is valid
    private void validateData(Simulation simulation) throws InvalidDataException, OutofRangeException {
        float luckRangeMax = 1.05f;
        float luckRangeMin = 0.95f;

        if (simulation.getSimulationParticipants() == null) {
            LOGGER.error("Participants missing.");
            throw new InvalidDataException("A simulation needs participants. Participants missing!");
        }
        if (simulation.getName().isBlank()) {
            LOGGER.error("Name not set.");
            throw new InvalidDataException("Name must be set!");
        }

        for (int i = 0; i < simulation.getSimulationParticipants().size(); i++) {
            SimulationParticipant simPart = simulation.getSimulationParticipants().get(i);
            if (simPart.getHorseId() == null) {
                LOGGER.error("Horse id of participant missing.");
                throw new InvalidDataException("Horse id of participant missing!");
            }
            if (simPart.getJockeyId() == null) {
                LOGGER.error("Jockey id of participant missing.");
                throw new InvalidDataException("Jockey id of participant missing!");
            }
            if (simPart.getLuckFactor() == null) {
                LOGGER.error("Luck factor of participant missing.");
                throw new InvalidDataException("Luck factor of participant missing!");
            }

            if (simPart.getLuckFactor() > luckRangeMax || simPart.getLuckFactor() < luckRangeMin) {
                LOGGER.error("Luck factor of participant out of range.");
                throw new OutofRangeException("Luck factor of participant is out of range!");
            }

            for (int j = 0; j < simulation.getSimulationParticipants().size(); j++) {
                SimulationParticipant simPart2 = simulation.getSimulationParticipants().get(j);
                if (simPart.getJockeyId().equals(simPart2.getJockeyId()) && i != j) {
                    LOGGER.error("Duplicate jockey found.");
                    throw new InvalidDataException("Jockey with id " + simPart.getJockeyId() + " can only take part once in this race!");
                }
                if (simPart.getHorseId().equals(simPart2.getHorseId()) && i != j) {
                    LOGGER.error("Duplicate horse found.");
                    throw new InvalidDataException("Horse with id " + simPart.getHorseId() + " can only take part once in this race!");
                }
            }

        }
    }

    /**
     * Inner class: Holds HashMap of jockeys and horses by ID as key for retrieving the correct horse/jockey combination
     */
    class DataHolder{
        private HashMap<Integer, Jockey> jockeysByID;
        private HashMap<Integer, Horse> horsesByID;
        public DataHolder(HashMap<Integer, Horse> horsesByID, HashMap<Integer, Jockey> jockeysByID){
            this.horsesByID=horsesByID;
            this.jockeysByID=jockeysByID;

        }

        public HashMap<Integer, Horse> getHorsesByID() {
            return horsesByID;
        }

        public void setHorsesByID(HashMap<Integer, Horse> horsesByID) {
            this.horsesByID = horsesByID;
        }

        public HashMap<Integer, Jockey> getJockeysByID() {
            return jockeysByID;
        }

        public void setJockeysByID(HashMap<Integer, Jockey> jockeysByID) {
            this.jockeysByID = jockeysByID;
        }
    }


}