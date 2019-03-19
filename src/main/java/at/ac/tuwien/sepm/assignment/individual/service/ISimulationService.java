package at.ac.tuwien.sepm.assignment.individual.service;

import at.ac.tuwien.sepm.assignment.individual.entity.Simulation;
import at.ac.tuwien.sepm.assignment.individual.exceptions.NotFoundException;
import at.ac.tuwien.sepm.assignment.individual.persistence.exceptions.PersistenceException;
import at.ac.tuwien.sepm.assignment.individual.service.exceptions.InvalidDataException;
import at.ac.tuwien.sepm.assignment.individual.service.exceptions.OutofRangeException;
import at.ac.tuwien.sepm.assignment.individual.service.exceptions.ServiceException;

import java.util.ArrayList;

public interface ISimulationService {

    /**
     * @param simulation to insert into database.
     * @return simulation object containing id, name, created date and participants arraylist with complete data
     * @throws NotFoundException will be thrown if a participant (horse or jockey) is not found.
     * @throws ServiceException will be thrown if something goes wrong during data processing
     * @throws OutofRangeException will be thrown if luck factor is out of range
     */
    Simulation insertOne(Simulation simulation) throws ServiceException, InvalidDataException, OutofRangeException, NotFoundException;


    /**
     *
     * @param id of simulation to get
     * @return simulation containing simulation data AND calculated participants data
     * @throws ServiceException if something goes wrong during database access
     * @throws NotFoundException if simulation not found
     */
    Simulation getOneById(Integer id) throws NotFoundException, ServiceException;

    /**
     *
     * @param simulation containing optional parameters to search simulation by
     * @return list of simulation matching optional parameters
     * @throws ServiceException if something goes wrong during database access
     */
    ArrayList<Simulation> getAllOrFiltered(Simulation simulation) throws ServiceException;
}
