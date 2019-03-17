package at.ac.tuwien.sepm.assignment.individual.persistence;

import at.ac.tuwien.sepm.assignment.individual.entity.Simulation;
import at.ac.tuwien.sepm.assignment.individual.entity.SimulationParticipantCompleted;
import at.ac.tuwien.sepm.assignment.individual.exceptions.NotFoundException;
import at.ac.tuwien.sepm.assignment.individual.persistence.exceptions.PersistenceException;

import java.util.ArrayList;

public interface ISimulationDao {


    /**
     * @param simulation to insert into database.
     * @param completeds list of participants, who completed simulation -> [id,rank,horseName,jockeyName,avgSpeed,horseSpeed,skill, luckFactor]
     *                   passed over to insert unique id for participants based on primary key of hj_combination table
     * @return simulation object containing id, name, created date
     * @throws PersistenceException will be thrown if something goes wrong during the database access.
     * @throws NotFoundException will be thrown if a participant (horse or jockey) is not found.
     */
    Simulation insertOne(Simulation simulation, ArrayList<SimulationParticipantCompleted> completeds) throws PersistenceException, NotFoundException;
}
