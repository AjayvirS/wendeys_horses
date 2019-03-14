package at.ac.tuwien.sepm.assignment.individual.persistence.impl;

import at.ac.tuwien.sepm.assignment.individual.entity.Jockey;
import at.ac.tuwien.sepm.assignment.individual.exceptions.NotFoundException;
import at.ac.tuwien.sepm.assignment.individual.persistence.IJockeyDao;
import at.ac.tuwien.sepm.assignment.individual.persistence.exceptions.PersistenceException;
import at.ac.tuwien.sepm.assignment.individual.persistence.util.DBConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDateTime;


@Repository
public class JockeyDao implements IJockeyDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(JockeyDao.class);
    private final DBConnectionManager dbConnectionManager;

    @Autowired
    public JockeyDao(DBConnectionManager dbConnectionManager) {
        this.dbConnectionManager = dbConnectionManager;
    }


    @Override
    public Jockey insertOne(Jockey jockey) throws PersistenceException {
        LOGGER.info("Insert Jockey");
        String sql = "INSERT INTO jockey(name,skill,created, updated) VALUES (?,?,?,?);";

        try {

            Timestamp tmstmp = Timestamp.valueOf(LocalDateTime.now());
            PreparedStatement statement = dbConnectionManager.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, jockey.getName());
            statement.setDouble(2, jockey.getSkill());
            statement.setTimestamp(3, tmstmp);
            statement.setTimestamp(4, tmstmp);
            statement.executeUpdate();
            ResultSet rs = statement.getGeneratedKeys();
            int key = 1;
            if (rs != null && rs.next()) {
                key = rs.getInt(1);
            }
            jockey.setCreated(tmstmp.toLocalDateTime());
            jockey.setUpdated(tmstmp.toLocalDateTime());
            jockey.setId(key);
            statement.close();
            return jockey;

        } catch (SQLException e) {
            LOGGER.error("Problem while adding jockey to database", e);
            throw new PersistenceException("Could not add jockey to database", e);
        }
    }

    @Override
    public Jockey updateOneById(Integer id, Jockey jockey) throws PersistenceException, NotFoundException {
        Jockey dbJockey = null;
        LOGGER.info("Update jockey with id " + id);
        String sql = "UPDATE jockey SET name = ?, skill = ?, updated=? WHERE id=?";
        try {

            Connection con = dbConnectionManager.getConnection();
            PreparedStatement statement = con.prepareStatement(sql);
            statement.clearParameters();

            //throws NotFoundException
            dbJockey = findOneById(id);

            statement = con.prepareStatement(sql);
            Timestamp tmstmp = Timestamp.valueOf(LocalDateTime.now());
            statement.setString(1, jockey.getName() == null ? dbJockey.getName() : jockey.getName());
            statement.setDouble(2, jockey.getSkill() == null ? dbJockey.getSkill() : jockey.getSkill());
            statement.setTimestamp(3, tmstmp);
            statement.setInt(4, id);
            statement.executeUpdate();
            statement.clearParameters();

            //throws NotFoundException
            dbJockey = findOneById(id);
            statement.close();

            return dbJockey;
        } catch (
            SQLException e) {
            LOGGER.error("Could not update jockey with " + id, e);
            throw new PersistenceException("Could not update jockey with id " + id, e);
        }


    }

    @Override
    public Jockey findOneById(Integer id) throws PersistenceException, NotFoundException {
        LOGGER.info("Get jockey with id " + id);
        String sql = "SELECT * FROM jockey WHERE id=?";
        Jockey jockey = null;
        try {
            PreparedStatement statement = dbConnectionManager.getConnection().prepareStatement(sql);
            statement.setInt(1, id);
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                jockey = dbResultToJockey(result);
            }
            statement.close();
        } catch (SQLException e) {
            LOGGER.error("Problem while executing SQL SELECT statement for reading jockey with id " + id, e);
            throw new PersistenceException("Error while accessing database", e);
        }

        if (jockey != null) {
            return jockey;
        } else {
            LOGGER.error("Could not find jockey with id "+id);
            throw new NotFoundException("Could not find jockey with id " + id);
        }
    }

    private static Jockey dbResultToJockey(ResultSet result) throws SQLException {
        return new Jockey(
            result.getInt("id"),
            result.getString("name"),
            result.getDouble("skill"),
            result.getTimestamp("created").toLocalDateTime(),
            result.getTimestamp("updated").toLocalDateTime());
    }




}
