package at.ac.tuwien.sepm.assignment.individual.persistence;

import at.ac.tuwien.sepm.assignment.individual.entity.Simulation;
import at.ac.tuwien.sepm.assignment.individual.entity.SimulationParticipantOutput;
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
    Simulation insertOne(Simulation simulation, ArrayList<SimulationParticipantOutput> completeds) throws PersistenceException, NotFoundException;


    /**
     *
     * @param id of the simulation to get
     * @return simulation containing simulation data and horse/jockey id combinations
     * @throws PersistenceException if anything goes wrong during database access
     * @throws NotFoundException if simulation is not found
     */
    Simulation getOneById(Integer id) throws PersistenceException, NotFoundException;

    /**
     *
     * @param simulation containing optional parameters to search simulation by
     * @return list of simulation matching optional parameters
     * @throws PersistenceException if something goes wrong during database access
     */
    ArrayList<Simulation> getAllOrFiltered(Simulation simulation) throws PersistenceException;
}
