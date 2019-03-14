package at.ac.tuwien.sepm.assignment.individual.service;

import at.ac.tuwien.sepm.assignment.individual.entity.Horse;
import at.ac.tuwien.sepm.assignment.individual.entity.Jockey;
import at.ac.tuwien.sepm.assignment.individual.exceptions.NotFoundException;
import at.ac.tuwien.sepm.assignment.individual.service.exceptions.InvalidDataException;
import at.ac.tuwien.sepm.assignment.individual.service.exceptions.OutofRangeException;
import at.ac.tuwien.sepm.assignment.individual.service.exceptions.ServiceException;


public interface IJockeyService {

    Jockey insertOne(Jockey jockey) throws ServiceException, OutofRangeException, InvalidDataException;

    Jockey updateOneById(Integer id, Jockey jockey) throws ServiceException, OutofRangeException, InvalidDataException, NotFoundException;
}