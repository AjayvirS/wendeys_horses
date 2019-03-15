package at.ac.tuwien.sepm.assignment.individual.service.impl;


import at.ac.tuwien.sepm.assignment.individual.entity.Simulation;
import at.ac.tuwien.sepm.assignment.individual.entity.SimulationParticipant;
import at.ac.tuwien.sepm.assignment.individual.persistence.ISimulationDao;
import at.ac.tuwien.sepm.assignment.individual.persistence.exceptions.PersistenceException;
import at.ac.tuwien.sepm.assignment.individual.service.ISimulationService;
import at.ac.tuwien.sepm.assignment.individual.service.exceptions.InvalidDataException;
import at.ac.tuwien.sepm.assignment.individual.service.exceptions.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SimulationService implements ISimulationService {


    private static final Logger LOGGER = LoggerFactory.getLogger(SimulationService.class);
    private final ISimulationDao simulationDao;

    @Autowired
    public SimulationService(ISimulationDao simulationDao){
        this.simulationDao=simulationDao;

    }

    @Override
    public Simulation insertOne(Simulation simulation) throws ServiceException, InvalidDataException {

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

    private void validateData(Simulation simulation) throws InvalidDataException {
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

        }
    }
}
