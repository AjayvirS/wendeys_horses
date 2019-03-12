package at.ac.tuwien.sepm.assignment.individual.persistence;

import at.ac.tuwien.sepm.assignment.individual.entity.Horse;
import at.ac.tuwien.sepm.assignment.individual.exceptions.NotFoundException;
import at.ac.tuwien.sepm.assignment.individual.persistence.exceptions.PersistenceException;
import at.ac.tuwien.sepm.assignment.individual.service.exceptions.InvalidDataException;

import java.util.ArrayList;

public interface IHorseDao {

    /**
     * @param id of the horse to find.
     * @return the horse with the specified id.
     * @throws PersistenceException will be thrown if something goes wrong during the database access.
     * @throws NotFoundException    will be thrown if the horse could not be found in the database.
     */
    Horse findOneById(Integer id) throws PersistenceException, NotFoundException;


    /**
     * @param horse to insert into database.
     * @return horse on successful insertion.
     * @throws PersistenceException will be thrown if something goes wrong during the database access.
     */
    Horse insertOne(Horse horse) throws PersistenceException;


    /**
     * @param id of the horse to be updated.
     * @param horse to update given values of horse.
     * @return horse on successful update.
     * @throws PersistenceException will be thrown if something goes wrong during the database access.
     */
    Horse updateOneById(Integer id, Horse horse) throws PersistenceException, NotFoundException, InvalidDataException;


    /**
     * @param id of the horse to be deleted from the database.
     * @throws PersistenceException will be thrown if something goes wrong during the database access.
     * @throws NotFoundException if horse with id does not exist
     */
    void deleteOneById(Integer id) throws PersistenceException, NotFoundException;

    /**
     *
     * @param horse filters records by name, breed, minSpeed, maxSpeed [OPTIONAL]
     * @return A list of filtered records of horses as ArrayList
     * @throws PersistenceException will be thrown if something goes wrong during the database access
     * @throws NotFoundException will be thrown if list returned is empty
     */
    ArrayList getAllOrFiltered(Horse horse) throws PersistenceException, NotFoundException;
}
