package at.ac.tuwien.sepm.assignment.individual.persistence.impl;

import at.ac.tuwien.sepm.assignment.individual.entity.*;
import at.ac.tuwien.sepm.assignment.individual.persistence.ISimulationDao;
import at.ac.tuwien.sepm.assignment.individual.persistence.exceptions.PersistenceException;
import at.ac.tuwien.sepm.assignment.individual.persistence.util.DBConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;


@Repository
public class SimulationDao implements ISimulationDao {


    private static final Logger LOGGER = LoggerFactory.getLogger(JockeyDao.class);
    private final DBConnectionManager dbConnectionManager;

    @Autowired
    public SimulationDao(DBConnectionManager dbConnectionManager) {

        this.dbConnectionManager = dbConnectionManager;

    }

    @Override
    public Integer insertOne(Simulation simulation) throws PersistenceException {
        LOGGER.info("Insert Simulation");
        Connection con=dbConnectionManager.getConnection();
        String sql = "INSERT INTO simulation(name,created) VALUES (?,?);";
        String sql2="INSERT INTO hj_combination(luckfactor, horseid, jockeyid, simulationid) VALUES (?,?,?,?)";


        try {

            Timestamp tmstmp = Timestamp.valueOf(LocalDateTime.now());
            PreparedStatement statement = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, simulation.getName());
            statement.setTimestamp(2, tmstmp);
            statement.executeUpdate();
            ResultSet rs = statement.getGeneratedKeys();
            int key = 1;
            if (rs != null && rs.next()) {
                key = rs.getInt(1);
            }

            statement.clearParameters();
            statement=con.prepareStatement(sql2);

            for (int i = 0; i < simulation.getSimulationParticipants().size(); i++) {
                setValues(statement, key, simulation.getSimulationParticipants().get(i));
            }
            statement.executeUpdate();

            return key;
        } catch (SQLException e) {
            LOGGER.error("Problem while adding jockey to database", e);
            throw new PersistenceException("Could not add jockey to database", e);
        }
    }

    private void setValues( PreparedStatement statement, Integer key, SimulationParticipant participant) throws SQLException {

        statement.setFloat(1, participant.getLuckFactor());
        statement.setInt(2, participant.getHorseId());
        statement.setInt(3, participant.getJockeyId());
        statement.setDouble(4,key);
    }


    private static Simulation dbResultToSimulation(ResultSet result) throws SQLException {
        return new Simulation(
            result.getInt("id"),
            result.getString("name"),
            result.getTimestamp("created").toLocalDateTime());
    }


}
