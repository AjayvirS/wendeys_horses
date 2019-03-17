package at.ac.tuwien.sepm.assignment.individual.service;

import at.ac.tuwien.sepm.assignment.individual.entity.Simulation;
import at.ac.tuwien.sepm.assignment.individual.exceptions.NotFoundException;
import at.ac.tuwien.sepm.assignment.individual.persistence.exceptions.PersistenceException;
import at.ac.tuwien.sepm.assignment.individual.service.exceptions.InvalidDataException;
import at.ac.tuwien.sepm.assignment.individual.service.exceptions.OutofRangeException;
import at.ac.tuwien.sepm.assignment.individual.service.exceptions.ServiceException;

public interface ISimulationService {

    /**
     * @param simulation to insert into database.
     * @return simulation object containing id, name, created date and participants arraylist with complete data
     * @throws NotFoundException will be thrown if a participant (horse or jockey) is not found.
     * @throws ServiceException will be thrown if something goes wrong during data processing
     * @throws OutofRangeException will be thrown if luck factor is out of range
     */
    Simulation insertOne(Simulation simulation) throws ServiceException, InvalidDataException, OutofRangeException, NotFoundException;

    Simulation getOneById(Integer id) throws NotFoundException, PersistenceException, ServiceException;
}
