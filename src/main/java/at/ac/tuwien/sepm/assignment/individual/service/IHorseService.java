package at.ac.tuwien.sepm.assignment.individual.service;

import at.ac.tuwien.sepm.assignment.individual.entity.Horse;
import at.ac.tuwien.sepm.assignment.individual.exceptions.NotFoundException;
import at.ac.tuwien.sepm.assignment.individual.service.exceptions.InvalidDataException;
import at.ac.tuwien.sepm.assignment.individual.service.exceptions.OutofRangeException;
import at.ac.tuwien.sepm.assignment.individual.service.exceptions.ServiceException;

import java.util.ArrayList;

public interface IHorseService {

    /**
     * @param id of the horse to find.
     * @return the horse with the specified id.
     * @throws ServiceException  will be thrown if something goes wrong during data processing.
     * @throws NotFoundException will be thrown if the horse could not be found in the system.
     */
    Horse findOneById(Integer id) throws ServiceException, NotFoundException;

    /**
     *
     * @param horse to insert into database
     * @return horse on successful insertion
     * @throws ServiceException  will be thrown if something goes wrong during data processing.
     * @throws OutofRangeException will be thrown if values of speed are out of range.
     * @throws InvalidDataException will be thrown if invalid data such as unexpected null fields are supplied.
     */
    Horse insertOne(Horse horse) throws ServiceException, OutofRangeException, InvalidDataException;


    /**
     * @param id of the horse to update.
     * @param horse to update given values of horse.
     * @return horse on successful insertion.
     * @throws ServiceException  will be thrown if something goes wrong during data processing.
     * @throws OutofRangeException will be thrown if values of speed are out of range.
     * @throws InvalidDataException will be thrown if invalid data such as unexpected null fields are supplied.
     * @throws NotFoundException will be thrown if the horse could not be found in the system.
     */
    Horse updateOneById(Integer id, Horse horse) throws ServiceException, OutofRangeException, InvalidDataException, NotFoundException;


    /**
     * @param id of the horse to delete.
     * @throws ServiceException  will be thrown if something goes wrong during data processing.
     * @throws NotFoundException will be thrown if the horse could not be found in the system.
     */
    void deleteOneById(Integer id) throws ServiceException, NotFoundException;

    ArrayList getAllOrFiltered(String name, String breed, Double minSpeed, Double maxSpeed) throws ServiceException, NotFoundException;
}
