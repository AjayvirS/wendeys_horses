package at.ac.tuwien.sepm.assignment.individual.persistence.impl;

import at.ac.tuwien.sepm.assignment.individual.entity.Simulation;
import at.ac.tuwien.sepm.assignment.individual.persistence.ISimulationDao;
import at.ac.tuwien.sepm.assignment.individual.persistence.exceptions.PersistenceException;
import at.ac.tuwien.sepm.assignment.individual.persistence.util.DBConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDateTime;

@Repository
public class SimulationDao implements ISimulationDao {


    private static final Logger LOGGER = LoggerFactory.getLogger(JockeyDao.class);
    private final DBConnectionManager dbConnectionManager;

    @Autowired
    public SimulationDao(DBConnectionManager dbConnectionManager) {
        this.dbConnectionManager = dbConnectionManager;
    }

    @Override
    public Simulation insertOne(Simulation simulation) throws PersistenceException {
        LOGGER.info("Insert Simulation");
        String sql = "INSERT INTO simulation(name,created) VALUES (?,?);";

        try {

            Timestamp tmstmp = Timestamp.valueOf(LocalDateTime.now());
            PreparedStatement statement = dbConnectionManager.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, simulation.getName());
            statement.setTimestamp(2, tmstmp);
            statement.executeUpdate();
            ResultSet rs = statement.getGeneratedKeys();
            int key = 1;
            if (rs != null && rs.next()) {
                key = rs.getInt(1);
            }
            statement.close();
            return null;

        } catch (SQLException e) {
            LOGGER.error("Problem while adding jockey to database", e);
            throw new PersistenceException("Could not add jockey to database", e);
        }
    }
}
