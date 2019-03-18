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
import java.util.*;

@Service
public class SimulationService implements ISimulationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimulationService.class);
    private final ISimulationDao simulationDao;
    private final IJockeyDao jockeyDao;
    private final IHorseDao horseDao;

    @Autowired
    public SimulationService(ISimulationDao simulationDao, JockeyDao jockeyDao, HorseDao horseDao) {
        this.simulationDao = simulationDao;
        this.horseDao = horseDao;
        this.jockeyDao = jockeyDao;

    }

    @Override
    public Simulation insertOne(Simulation simulation) throws ServiceException, InvalidDataException, OutofRangeException, NotFoundException {
        LOGGER.info("Post Simulation with name: "+simulation.getName()+ " and participants: "+simulation.getSimulationParticipantInputs());


        try {
            LOGGER.info("Validate data of simulation to be inserted");
            validateData(simulation);
            LOGGER.info("Prepare Simulation and calculate values based on horse speed, luck and skill");
            HashMap<Integer,Horse>horses= new HashMap<>();
            HashMap<Integer,Jockey>jockeys=new HashMap<>();
            prepareIDs(simulation, horses, jockeys);
            ArrayList<SimulationParticipantOutput> completeds = getCalculatedSimulation(horses, jockeys, simulation);
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
            LOGGER.info("Insert simulation with following data: " + "Name: " + simulation.getName() + ", Participants: " + simulation.getSimulationParticipantInputs());
            Simulation compSim = simulationDao.insertOne(simulation, completeds);
            compSim.setSimulationParticipantsCompleted(completeds);
            return compSim;
        } catch (PersistenceException e) {
            LOGGER.error("Problem while processing simulation");
            throw new ServiceException(e.getMessage(), e);
        }
    }

    private void prepareIDs(Simulation simulation, HashMap<Integer,Horse>horseByIDs, HashMap<Integer,Jockey>jockeyByIDs) throws PersistenceException, NotFoundException {
        Integer[]horseIDs=new Integer[simulation.getSimulationParticipantInputs().size()];
        Integer[]jockeyIDs=new Integer[simulation.getSimulationParticipantInputs().size()];
        for (int i = 0; i < simulation.getSimulationParticipantInputs().size(); i++) {
            ArrayList<SimulationParticipantInput> tempArr=simulation.getSimulationParticipantInputs();

            horseIDs[i]=tempArr.get(i).getHorseId();
            jockeyIDs[i]=tempArr.get(i).getJockeyId();

        }
        horseByIDs=((HorseDao)horseDao).getCorrectHorsesForSimulation(simulation.getCreated(), horseIDs);
        jockeyByIDs=((JockeyDao)jockeyDao).getCorrectJockeysForSimulation(simulation.getCreated(), jockeyIDs);
    }

    @Override
    public Simulation getOneById(Integer id) throws NotFoundException, ServiceException {
        LOGGER.info("Get Simulation with id " + id);
        Simulation simulation;

        try {
            simulation = simulationDao.getOneById(id);
            HashMap<Integer,Horse>horses= new HashMap<>();
            HashMap<Integer,Jockey>jockeys=new HashMap<>();
            prepareIDs(simulation, horses, jockeys);
            ArrayList<SimulationParticipantOutput> completeds = getCalculatedSimulation(horses, jockeys, simulation);
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
            simulation.setSimulationParticipantsCompleted(completeds);
            return simulation;

        } catch(PersistenceException e){
            LOGGER.error("Problem while processing simulation");
            throw new ServiceException(e.getMessage(), e);
        }

    }


    private void validateData(Simulation simulation) throws InvalidDataException, OutofRangeException {
        if (simulation.getSimulationParticipantInputs() == null) {
            LOGGER.error("Participants missing.");
            throw new InvalidDataException("A simulation needs participants. Participants missing!");
        }
        if (simulation.getName().isBlank()) {
            LOGGER.error("Name not set.");
            throw new InvalidDataException("Name must be set!");
        }

        for (int i = 0; i < simulation.getSimulationParticipantInputs().size(); i++) {
            SimulationParticipantInput simPart = simulation.getSimulationParticipantInputs().get(i);
            if (simPart.getHorseId() == null) {
                LOGGER.error("Horse id of participant" + i + "missing.");
                throw new InvalidDataException("Horse id of participant" + i + " missing!");
            }
            if (simPart.getJockeyId() == null) {
                LOGGER.error("Jockey id of participant" + i + "missing.");
                throw new InvalidDataException("Jockey id of participant" + i + " missing!");
            }
            if (simPart.getLuckFactor() == null) {
                LOGGER.error("Luck factor of participant " + i + " missing.");
                throw new InvalidDataException("Luck factor of participant " + i + " missing!");
            }
            float luckRangeMax = 1.05f;
            float luckRangeMin = 0.95f;
            if (simPart.getLuckFactor() > luckRangeMax || simPart.getLuckFactor() < luckRangeMin) {
                LOGGER.error("Luck factor of participant " + i + " out of range.");
                throw new OutofRangeException("Luck factor of participant " + i + " is out of range!");
            }

            for (int j = 0; j < simulation.getSimulationParticipantInputs().size(); j++) {
                SimulationParticipantInput simPart2 = simulation.getSimulationParticipantInputs().get(j);
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


    private ArrayList<SimulationParticipantOutput> getCalculatedSimulation
        (HashMap<Integer,Horse> horses, HashMap<Integer,Jockey>jockeys, Simulation simulation) {
        Double p_min, p_max, k, p, k2, d;
        Float g;
        ArrayList<SimulationParticipantOutput> completeds = new ArrayList<>();

        //according to oracle javadoc to prevent comma: use nf and change locale where dot is used as decimal point
        //and cast to decimalformat
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
        DecimalFormat df = (DecimalFormat) nf;
        df.applyPattern("#.####");
        df.setRoundingMode(RoundingMode.UP);
        String horseName = null, jockeyName = null;

        for (int i = 0; i < simulation.getSimulationParticipantInputs().size(); i++) {
            SimulationParticipantInput simpart=simulation.getSimulationParticipantInputs().get(i);
            p_min=horses.get(simpart.getHorseId()).getMinSpeed();
            p_max=horses.get(simpart.getHorseId()).getMaxSpeed();
            k=jockeys.get(simpart.getJockeyId()).getSkill();
            g=simpart.getLuckFactor();
            p = (g - 0.95) * ((p_max - p_min) / (1.05 - 0.95)) + p_min;
            k2 = 1 + ((0.15 * 1 / Math.PI) * Math.atan((1 / 5f) * k));
            p = Double.valueOf(df.format(p));
            k2 = Double.valueOf(df.format(k2));
            d = k2 * p * g;
            d = Double.valueOf(df.format(d));
            jockeyName=jockeys.get(simpart.getJockeyId()).getName();
            horseName=horses.get(simpart.getHorseId()).getName();
            completeds.add(new SimulationParticipantOutput(null,null,horseName,jockeyName,d,p,k,g));
        }

        return completeds;

    }


}