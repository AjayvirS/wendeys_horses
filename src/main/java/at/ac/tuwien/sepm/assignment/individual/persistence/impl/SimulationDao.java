package at.ac.tuwien.sepm.assignment.individual.persistence.impl;

import at.ac.tuwien.sepm.assignment.individual.entity.*;
import at.ac.tuwien.sepm.assignment.individual.exceptions.NotFoundException;
import at.ac.tuwien.sepm.assignment.individual.persistence.ISimulationDao;
import at.ac.tuwien.sepm.assignment.individual.persistence.exceptions.PersistenceException;
import at.ac.tuwien.sepm.assignment.individual.persistence.util.DBConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;


@Repository
public class SimulationDao implements ISimulationDao {


    private static final Logger LOGGER = LoggerFactory.getLogger(Simulation.class);
    private final DBConnectionManager dbConnectionManager;

    @Autowired
    public SimulationDao(DBConnectionManager dbConnectionManager) {

        this.dbConnectionManager = dbConnectionManager;

    }

    @Override
    public Simulation insertOne(Simulation simulation, ArrayList<SimulationParticipantOutput> completeds) throws PersistenceException {
        LOGGER.info("Insert Simulation");
        Connection con = dbConnectionManager.getConnection();
        String sql = "INSERT INTO simulation(name,created) VALUES (?,?);";
        String sql2 = "INSERT INTO hj_combination(luckfactor, horseid, jockeyid, simulationid) VALUES (?,?,?,?)";
        String sql3 = "SELECT id from hj_combination WHERE simulationid=?";


        try {

            PreparedStatement statement = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, simulation.getName());
            statement.setTimestamp(2, Timestamp.valueOf(simulation.getCreated()));
            statement.executeUpdate();
            ResultSet rs = statement.getGeneratedKeys();
            int key = 1;
            if (rs != null && rs.next()) {
                key = rs.getInt(1);
            }

            statement.clearParameters();
            statement = con.prepareStatement(sql2);

            for (int i = 0; i < simulation.getSimulationParticipants().size(); i++) {
                setValues(statement, key, simulation.getSimulationParticipants().get(i));
                statement.executeUpdate();
                statement.clearParameters();
            }
            simulation.setId(key);
            statement.clearParameters();
            statement = con.prepareStatement(sql3);
            statement.setInt(1, simulation.getId());
            rs = statement.executeQuery();
            int i = 0;
            while (rs.next()) {
                completeds.get(i).setId(rs.getInt("id"));
                i++;
            }
            return simulation;

        } catch (SQLException e) {
            LOGGER.error("Problem while adding simulation to database", e);
            throw new PersistenceException("Could not add simulation to database", e);
        }
    }

    @Override
    public Simulation getOneById(Integer id) throws PersistenceException, NotFoundException {
        LOGGER.info("Get simulation with id " + id);
        Connection con = dbConnectionManager.getConnection();
        String sql = "SELECT hj_combination.id as uid, horseid, jockeyid, luckfactor, simulationid, name, created FROM simulation JOIN hj_combination ON simulation.id=simulationid WHERE simulation" +
            "id=?";
        Simulation simulation;


        ArrayList<SimulationParticipant> simparts = new ArrayList<>();
        try {
            PreparedStatement statement = con.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_READ_ONLY);
            statement.setInt(1, id);
            ResultSet result = statement.executeQuery();
            statement.clearParameters();

            if (result.next()) {
                simulation = dbResultToSimulation(result);
                result.beforeFirst();
            } else {
                LOGGER.error("Could not find simulation with id " + id);
                throw new NotFoundException("Could not find simulation with id " + id);
            }


            while (result.next()) {
                simparts.add(new SimulationParticipant(result.getInt("uid"), result.getInt("horseid"),
                    result.getInt("jockeyid"), result.getFloat("luckFactor")));
            }

            simulation.setSimulationParticipants(simparts);

            statement.close();
        } catch (SQLException e) {
            LOGGER.error("Problem while executing SQL SELECT statement for reading simulation with id " + id, e);
            throw new PersistenceException("Error while accessing database", e);
        }
        return simulation;
    }


    @Override
    public ArrayList<Simulation> getAllOrFiltered(Simulation simulation) throws PersistenceException {
        LOGGER.info("Get simulation/s with following optional parameter: " + (simulation.getName() == null ? "" : simulation.getName()));
        ArrayList<Simulation> filteredList = new ArrayList<>();
        String sql = "SELECT * FROM simulation WHERE name LIKE ?";

        try {
            PreparedStatement statement = dbConnectionManager.getConnection().prepareStatement(sql);
            if (simulation.getName() == null) {
                statement.setString(1, "%");
            } else statement.setString(1, "%" + simulation.getName() + "%");

            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                filteredList.add(dbResultToSimulation(rs));
            }
            statement.close();

        } catch (SQLException e) {
            LOGGER.error("Problem while executing SQL SELECT statement");
            throw new PersistenceException("Error while accessing database");
        }

        if (filteredList.isEmpty()) {
            LOGGER.error("Could not find simulation/s with following optional parameters: " + simulation.getName());
        }
        return filteredList;


    }

    private void setValues(PreparedStatement statement, Integer key, SimulationParticipant participant) throws
        SQLException {

        statement.setFloat(1, participant.getLuckFactor());
        statement.setInt(2, participant.getHorseId());
        statement.setInt(3, participant.getJockeyId());
        statement.setDouble(4, key);
    }


    private static Simulation dbResultToSimulation(ResultSet result) throws SQLException {
        return new Simulation(
            result.getInt("simulationid"),
            result.getString("name"),
            result.getTimestamp("created").toLocalDateTime());
    }


}
