package at.ac.tuwien.sepm.assignment.individual.persistence;

import at.ac.tuwien.sepm.assignment.individual.entity.Simulation;
import at.ac.tuwien.sepm.assignment.individual.entity.SimulationParticipantCompleted;
import at.ac.tuwien.sepm.assignment.individual.exceptions.NotFoundException;
import at.ac.tuwien.sepm.assignment.individual.persistence.exceptions.PersistenceException;

import java.util.ArrayList;

public interface ISimulationDao {
    Simulation insertOne(Simulation simulation, ArrayList<SimulationParticipantCompleted> completeds) throws PersistenceException, NotFoundException;
}
