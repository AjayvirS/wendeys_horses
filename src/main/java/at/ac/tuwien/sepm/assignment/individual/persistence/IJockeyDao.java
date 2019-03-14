package at.ac.tuwien.sepm.assignment.individual.persistence;

import at.ac.tuwien.sepm.assignment.individual.entity.Horse;
import at.ac.tuwien.sepm.assignment.individual.entity.Jockey;
import at.ac.tuwien.sepm.assignment.individual.exceptions.NotFoundException;
import at.ac.tuwien.sepm.assignment.individual.persistence.exceptions.PersistenceException;
import at.ac.tuwien.sepm.assignment.individual.service.exceptions.InvalidDataException;

import java.util.ArrayList;

public interface IJockeyDao {

    Jockey insertOne(Jockey jockey) throws PersistenceException;

    Jockey updateOneById(Integer id, Jockey jockey) throws PersistenceException, NotFoundException, InvalidDataException;

    Jockey findOneById(Integer id) throws PersistenceException, NotFoundException;

    void deleteOneById(Integer id) throws PersistenceException, NotFoundException;

    ArrayList<Jockey> getAllOrFiltered(Jockey jockey) throws PersistenceException, NotFoundException;
}
