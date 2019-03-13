package at.ac.tuwien.sepm.assignment.individual.persistence;

import at.ac.tuwien.sepm.assignment.individual.entity.Jockey;
import at.ac.tuwien.sepm.assignment.individual.persistence.exceptions.PersistenceException;

public interface IJockeyDao {

    Jockey insertOne(Jockey jockey) throws PersistenceException;
}
