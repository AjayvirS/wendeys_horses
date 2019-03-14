package at.ac.tuwien.sepm.assignment.individual.persistence.impl;

import at.ac.tuwien.sepm.assignment.individual.entity.Jockey;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(HorseDao.class);
    private final DBConnectionManager dbConnectionManager;

    @Autowired
    public JockeyDao(DBConnectionManager dbConnectionManager){
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


}
