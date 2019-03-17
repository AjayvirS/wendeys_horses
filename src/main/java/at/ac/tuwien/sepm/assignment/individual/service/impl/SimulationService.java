package at.ac.tuwien.sepm.assignment.individual.service.impl;


import at.ac.tuwien.sepm.assignment.individual.entity.*;
import at.ac.tuwien.sepm.assignment.individual.exceptions.NotFoundException;
import at.ac.tuwien.sepm.assignment.individual.persistence.IHorseDao;
import at.ac.tuwien.sepm.assignment.individual.persistence.IJockeyDao;
import at.ac.tuwien.sepm.assignment.individual.persistence.ISimulationDao;
import at.ac.tuwien.sepm.assignment.individual.persistence.exceptions.PersistenceException;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

@Service
public class SimulationService implements ISimulationService {

    private static float luckRangeMax=1.05f;
    private static float luckRangeMin=0.95f;
    private static final Logger LOGGER = LoggerFactory.getLogger(SimulationService.class);
    private final ISimulationDao simulationDao;
    private  final IJockeyDao jockeyDao;
    private final IHorseDao horseDao;

    @Autowired
    public SimulationService(ISimulationDao simulationDao, IJockeyDao jockeyDao, IHorseDao horseDao){
        this.simulationDao=simulationDao;
        this.horseDao=horseDao;
        this.jockeyDao=jockeyDao;

    }

    @Override
    public Simulation insertOne(Simulation simulation) throws ServiceException, InvalidDataException, OutofRangeException, NotFoundException {

        try {
            LOGGER.info("Validate data of simulation to be inserted");
            validateData(simulation);


            LOGGER.info("Prepare Simulation and calculate values based on horse speed, luck and skill");
            ArrayList<Horse> horses=getParticipantHorses(simulation);
            ArrayList<Jockey> jockeys= getParticipantJockeys(simulation);
            ArrayList<Float>luckFactors= getLuckFactors(simulation);

            ArrayList<SimulationParticipantCompleted>completeds= getCalculatedSimulation(horses, jockeys, luckFactors, simulation);
            Collections.sort(completeds, Comparator.comparingDouble(SimulationParticipantCompleted::getAvgSpeed));
            Collections.reverse(completeds);

            LOGGER.info("Simulation data calculated and sorted by highest average Speed");
            LOGGER.info("Set rank of participants");
            int tempRank=1;
            completeds.get(0).setRank(tempRank);
            for (int i = 1; i < completeds.size(); i++) {
                if(completeds.get(i).getAvgSpeed()<completeds.get(i-1).getAvgSpeed()){
                    tempRank++;
                }
                completeds.get(i).setRank(tempRank);
            }
            LOGGER.info("Insert simulation with following data: " + "Name: "+simulation.getName()+", Participants: "+simulation.getSimulationParticipants());
            Simulation compSim=simulationDao.insertOne(simulation, completeds);
            compSim.setSimulationParticipantsCompleted(completeds);
            return compSim;
        } catch (PersistenceException e) {
            LOGGER.error("Problem while processing jockey");
            throw new ServiceException(e.getMessage(), e);
        }
    }



    private void validateData(Simulation simulation) throws InvalidDataException, OutofRangeException {
        if(simulation.getSimulationParticipants()==null){
            LOGGER.error("Participants missing.");
            throw new InvalidDataException("A simulation needs participants. Participants missing!");
        }
        if(simulation.getName().isBlank()){
            LOGGER.error("Name not set.");
            throw new InvalidDataException("Name must be set!");
        }

        for (int i = 0; i < simulation.getSimulationParticipants().size(); i++) {
            SimulationParticipant simPart=simulation.getSimulationParticipants().get(i);
            if(simPart.getHorseId()== null){
                LOGGER.error("Horse id of participant"+i+"missing.");
                throw new InvalidDataException("Horse id of participant"+i+" missing!");
            }
            if(simPart.getJockeyId()==null){
                LOGGER.error("Jockey id of participant"+i+"missing.");
                throw new InvalidDataException("Jockey id of participant"+i+" missing!");
            }
            if(simPart.getLuckFactor()==null){
                LOGGER.error("Luck factor of participant "+i+" missing.");
                throw new InvalidDataException("Luck factor of participant "+i+" missing!");
            }
            if(simPart.getLuckFactor()>luckRangeMax || simPart.getLuckFactor()<luckRangeMin){
                LOGGER.error("Luck factor of participant "+i+" out of range.");
                throw new OutofRangeException("Luck factor of participant "+i+" is out of range!");
            }

            for (int j = 0; j < simulation.getSimulationParticipants().size(); j++) {
                SimulationParticipant simPart2=simulation.getSimulationParticipants().get(j);
                if(simPart.getJockeyId().equals(simPart2.getJockeyId()) && i!=j){
                    LOGGER.error("Duplicate jockey found.");
                    throw new InvalidDataException("Jockey with id "+simPart.getJockeyId()+" can only take part once in this race!");
                }
                if(simPart.getHorseId().equals(simPart2.getHorseId()) && i!=j){
                    LOGGER.error("Duplicate horse found.");
                    throw new InvalidDataException("Horse with id "+simPart.getHorseId()+" can only take part once in this race!");
                }
            }

        }
    }


    private ArrayList<Float> getLuckFactors(Simulation simulation) {
        ArrayList<Float>luckFactors=new ArrayList<>();

        for (int i = 0; i < simulation.getSimulationParticipants().size(); i++) {
            luckFactors.add(simulation.getSimulationParticipants().get(i).getLuckFactor());
        }

        return luckFactors;
    }

    private ArrayList<SimulationParticipantCompleted> getCalculatedSimulation(ArrayList<Horse> horses, ArrayList<Jockey> jockeys, ArrayList<Float> luckFactors, Simulation simulation) {
        Double p_min, p_max, k, p, k2, d;
        Float g;
        ArrayList<SimulationParticipantCompleted> completeds=new ArrayList<>();

        //according to oracle javadoc to prevent comma: use nf and change locale where dot is used as decimal point
        //and cast to decimalformat
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
        DecimalFormat df = (DecimalFormat)nf;
        df.applyPattern("#.####");
        df.setRoundingMode(RoundingMode.UP);
        String horseName=null, jockeyName=null;

        for (int i = 0; i < simulation.getSimulationParticipants().size(); i++) {
            p_min=horses.get(i).getMinSpeed();
            p_max=horses.get(i).getMaxSpeed();
            k=jockeys.get(i).getSkill();
            g=luckFactors.get(i);
            p=(g-0.95)*((p_max-p_min)/(1.05-0.95))+p_min;
            k2=1+((0.15*1/Math.PI)*Math.atan((1/5f)*k));
            System.out.println(df.format(p));
            p=Double.valueOf(df.format(p));
            k2=Double.valueOf(df.format(k2));
            d=k2*p*g;
            d=Double.valueOf(df.format(d));
            jockeyName=jockeys.get(i).getName();
            horseName=horses.get(i).getName();
            completeds.add(new SimulationParticipantCompleted(null, null,horseName,jockeyName, d, p, k, g));
        }

        return completeds;

    }

    private ArrayList<Jockey> getParticipantJockeys(Simulation simulation) throws NotFoundException, PersistenceException {

        ArrayList<Jockey>jockeys=new ArrayList<>();

        for (int i = 0; i < simulation.getSimulationParticipants().size(); i++) {
            jockeys.add(jockeyDao.findOneById(simulation.getSimulationParticipants().get(i).getJockeyId()));
        }

        return jockeys;
    }

    private ArrayList<Horse> getParticipantHorses(Simulation simulation) throws NotFoundException, PersistenceException {
        ArrayList<Horse>horses=new ArrayList<>();

        for (int i = 0; i < simulation.getSimulationParticipants().size(); i++) {
            horses.add(horseDao.findOneById(simulation.getSimulationParticipants().get(i).getHorseId()));
        }

        return horses;
    }
}