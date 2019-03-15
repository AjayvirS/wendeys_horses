package at.ac.tuwien.sepm.assignment.individual.service.impl;


import at.ac.tuwien.sepm.assignment.individual.entity.Simulation;
import at.ac.tuwien.sepm.assignment.individual.entity.SimulationParticipant;
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

@Service
public class SimulationService implements ISimulationService {

    private static float luckRangeMax=1.05f;
    private static float luckRangeMin=0.95f;
    private static final Logger LOGGER = LoggerFactory.getLogger(SimulationService.class);
    private final ISimulationDao simulationDao;

    @Autowired
    public SimulationService(ISimulationDao simulationDao){
        this.simulationDao=simulationDao;

    }

    @Override
    public Simulation insertOne(Simulation simulation) throws ServiceException, InvalidDataException, OutofRangeException {

        try {
            LOGGER.info("Validate data of simulation to be inserted");
            validateData(simulation);
            LOGGER.info("Insert simulation with following data: " + "Name: "+simulation.getName()+", Participants: "+simulation.getSimulationParticipants());
            return simulationDao.insertOne(simulation);
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
                SimulationParticipant simPart2=simulation.getSimulationParticipants().get(i);
                if(simPart.getJockeyId().equals(simPart2.getJockeyId())){
                    LOGGER.error("Duplicate jockey found.");
                    throw new InvalidDataException("Jockey with id"+simPart.getJockeyId()+" can only take part once in this race!");
                }
                if(simPart.getHorseId().equals(simPart2.getHorseId())){
                    LOGGER.error("Duplicate horse found.");
                    throw new InvalidDataException("Horse with id"+simPart.getJockeyId()+" can only take part once in this race!");
                }
            }

        }
    }
}
