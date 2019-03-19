package at.ac.tuwien.sepm.assignment.individual.service;

import at.ac.tuwien.sepm.assignment.individual.entity.Jockey;
import at.ac.tuwien.sepm.assignment.individual.exceptions.NotFoundException;
import at.ac.tuwien.sepm.assignment.individual.service.exceptions.InvalidDataException;
import at.ac.tuwien.sepm.assignment.individual.service.exceptions.OutofRangeException;
import at.ac.tuwien.sepm.assignment.individual.service.exceptions.ServiceException;

import java.util.ArrayList;


public interface IJockeyService {


    /**
     * @param jockey to insert into database
     * @return jockey on successful insertion
     * @throws ServiceException  will be thrown if something goes wrong during data processing.
     * @throws InvalidDataException will be thrown if invalid data such as unexpected null fields are supplied.
     */
    Jockey insertOne(Jockey jockey) throws ServiceException, OutofRangeException, InvalidDataException;



    /**
     * @param id of the jockey to be updated.
     * @param jockey to update given values of jockey, may contain null fields.
     * @return jockey on successful insertion else below exception thrown.
     * @throws ServiceException  will be thrown if something goes wrong during data processing.
     * @throws InvalidDataException will be thrown if invalid data such as unexpected null fields are supplied.
     * @throws NotFoundException will be thrown if the jockey could not be found in the system.
     */
    Jockey updateOneById(Integer id, Jockey jockey) throws ServiceException, InvalidDataException, NotFoundException;


    /**
     * @param id of the jockey to be deleted.
     * @throws ServiceException  will be thrown if something goes wrong during data processing.
     * @throws NotFoundException will be thrown if the jockey could not be found in the system.
     */
    void deleteOneById(Integer id) throws ServiceException, NotFoundException;


    /**
     * @param jockey to filter records by name and/or skill[OPTIONAL]
     * @return A list of filtered records of jockey as ArrayList.
     */
    ArrayList<Jockey> getAllOrFiltered(Jockey jockey) throws ServiceException;

    /**
     * @param id of the jockey to find.
     * @return the jockey with the specified id.
     * @throws ServiceException  will be thrown if something goes wrong during data processing.
     * @throws NotFoundException will be thrown if the jockey could not be found in the system.
     */
    Jockey findOneById(Integer id) throws ServiceException, NotFoundException;
}