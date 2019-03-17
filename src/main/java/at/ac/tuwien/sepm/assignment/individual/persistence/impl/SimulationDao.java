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
    public Simulation insertOne(ArrayList<SimulationParticipantCompleted> simPartsComp, Simulation simulation) throws PersistenceException {
        LOGGER.info("Insert Simulation");
        String sql = "INSERT INTO simulation(name,created) VALUES (?,?);";
        String sql2="INSERT INTO hj_combination(rank, horse_name, jockey_name, avg_speed, horse_speed, jockey_skill, luckfactor, simulationid) " +
            "VALUES (?,?,?,?,?,?,?,?)";


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

            for (int i = 0; i < simPartsComp.size(); i++) {
                simPartsComp.get(i).setSimulationId(key);

            }
            //@TODO
            /*
                insert method has simulation participants completed;
                need to insert every participant into table hj_combination;
                reset this statement or use new statement to insert data into hj_combination
                simpartsComp.simId=key
             */
            statement.clearParameters();
            statement=dbConnectionManager.getConnection().prepareStatement(sql2);
            Integer tempRank=1;
            for (int i = 0; i < simPartsComp.size()-1; i++) {

                setValues(simPartsComp.get(i), statement, tempRank);


            }




            return null;
        } catch (SQLException e) {
            LOGGER.error("Problem while adding jockey to database", e);
            throw new PersistenceException("Could not add jockey to database", e);
        }
    }

    private void setValues(SimulationParticipantCompleted participant, PreparedStatement statement, Integer rank) throws SQLException {

        statement.setInt(1, rank);
        statement.setString(2, participant.getHorseName());
        statement.setString(3, participant.getJockeyName());
        statement.setDouble(4, participant.getAvgSpeed());
        statement.setDouble(5, participant.getHorseSpeed());
        statement.setDouble(6, participant.getJockeySkill());
        statement.setDouble(7, participant.getLuckFactor());



    }


    private static Simulation dbResultToSimulation(ResultSet result) throws SQLException {
        return new Simulation(
            result.getInt("id"),
            result.getString("name"),
            result.getTimestamp("created").toLocalDateTime());
    }


}
