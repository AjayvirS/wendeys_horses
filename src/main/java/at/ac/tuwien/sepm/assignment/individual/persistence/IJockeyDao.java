package at.ac.tuwien.sepm.assignment.individual.persistence;

import at.ac.tuwien.sepm.assignment.individual.entity.Jockey;
import at.ac.tuwien.sepm.assignment.individual.exceptions.NotFoundException;
import at.ac.tuwien.sepm.assignment.individual.persistence.exceptions.PersistenceException;
import at.ac.tuwien.sepm.assignment.individual.service.exceptions.InvalidDataException;

import java.util.ArrayList;

public interface IJockeyDao {

    /**
     * @param jockey to insert into database.
     * @return jockey on successful insertion.
     * @throws PersistenceException will be thrown if something goes wrong during the database access.
     */
    Jockey insertOne(Jockey jockey) throws PersistenceException;


    /**
     * @param id of the jockey to be updated.
     * @param jockey to update given values of jockey.
     * @return jockey on successful update.
     * @throws PersistenceException will be thrown if something goes wrong during the database access.
     */
    Jockey updateOneById(Integer id, Jockey jockey) throws PersistenceException, NotFoundException, InvalidDataException;


    /**
     * @param id of the jockey to find.
     * @return the jockey with the specified id.
     * @throws PersistenceException will be thrown if something goes wrong during the database access.
     * @throws NotFoundException    will be thrown if the jockey could not be found in the database.
     */
    Jockey findOneById(Integer id) throws PersistenceException, NotFoundException;

    /**
     * @param id of the jockey to be deleted from the database.
     * @throws PersistenceException will be thrown if something goes wrong during the database access.
     * @throws NotFoundException if jockey with id does not exist
     */
    void deleteOneById(Integer id) throws PersistenceException, NotFoundException;

    /**
     *
     * @param jockey to filter records by name and/or skill [OPTIONAL]
     * @return A list of filtered records of jockeys as ArrayList
     * @throws PersistenceException will be thrown if something goes wrong during the database access
     * @throws NotFoundException will be thrown if list returned is empty
     */
    ArrayList<Jockey> getAllOrFiltered(Jockey jockey) throws PersistenceException, NotFoundException;
}
