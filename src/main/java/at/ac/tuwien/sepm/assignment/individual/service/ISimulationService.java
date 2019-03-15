package at.ac.tuwien.sepm.assignment.individual.service;

import at.ac.tuwien.sepm.assignment.individual.entity.Simulation;
import at.ac.tuwien.sepm.assignment.individual.service.exceptions.InvalidDataException;
import at.ac.tuwien.sepm.assignment.individual.service.exceptions.ServiceException;

public interface ISimulationService {
    Simulation insertOne(Simulation dtoToEntity) throws ServiceException, InvalidDataException;
}
