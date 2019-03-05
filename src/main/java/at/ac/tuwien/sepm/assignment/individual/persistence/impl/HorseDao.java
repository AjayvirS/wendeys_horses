package at.ac.tuwien.sepm.assignment.individual.persistence.impl;

import at.ac.tuwien.sepm.assignment.individual.entity.Horse;
import at.ac.tuwien.sepm.assignment.individual.exceptions.NotFoundException;
import at.ac.tuwien.sepm.assignment.individual.persistence.IHorseDao;
import at.ac.tuwien.sepm.assignment.individual.persistence.exceptions.PersistenceException;
import at.ac.tuwien.sepm.assignment.individual.persistence.util.DBConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.LocalTime;


@Repository
public class HorseDao implements IHorseDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(HorseDao.class);
    private final DBConnectionManager dbConnectionManager;

    @Autowired
    public HorseDao(DBConnectionManager dbConnectionManager) {
        this.dbConnectionManager = dbConnectionManager;
    }

    private static Horse dbResultToHorseDto(ResultSet result) throws SQLException {
        return new Horse(
            result.getInt("id"),
            result.getString("name"),
            result.getString("breed"),
            result.getDouble("min_speed"),
            result.getDouble("max_speed"),
            result.getTimestamp("created").toLocalDateTime(),
            result.getTimestamp("updated").toLocalDateTime());
    }

    @Override
    public Horse findOneById(Integer id) throws PersistenceException, NotFoundException {
        LOGGER.info("Get horse with id " + id);
        String sql = "SELECT * FROM Horse WHERE id=?";
        Horse horse = null;
        try {
            PreparedStatement statement = dbConnectionManager.getConnection().prepareStatement(sql);
            statement.setInt(1, id);
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                horse = dbResultToHorseDto(result);
            }
        } catch (SQLException e) {
            LOGGER.error("Problem while executing SQL select statement for reading horse with id " + id, e);
            throw new PersistenceException("Could not read horses with id " + id, e);
        }
        if (horse != null) {
            return horse;
        } else {
            throw new NotFoundException("Could not find horse with id " + id);
        }
    }

    @Override
    public Horse insertOne(Horse horse) throws PersistenceException {

        String sql="INSERT INTO HORSE(name,breed,min_Speed, max_Speed, created, updated) VALUES (?,?,?,?,?,?);";

        try{

            Timestamp tmstmp=Timestamp.valueOf(LocalDateTime.now());
            PreparedStatement statement=dbConnectionManager.getConnection().prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
            statement.setString(1,horse.getName());
            statement.setString(2,horse.getBreed());
            statement.setDouble(3,horse.getMinSpeed());
            statement.setDouble(4,horse.getMaxSpeed());
            statement.setTimestamp(5, tmstmp);
            statement.setTimestamp(6, tmstmp);
            statement.executeUpdate();
            ResultSet rs = statement.getGeneratedKeys();

            int key=1;
            if (rs != null && rs.next()) {
                key = rs.getInt(1);
            }
            horse.setCreated(tmstmp.toLocalDateTime());
            horse.setUpdated(tmstmp.toLocalDateTime());
            horse.setId(key);

            LOGGER.info("Save horse: "+ horse.toString());

            return horse;
        } catch (SQLException e){
            LOGGER.error("Problem while adding horse to database", e);
            throw new PersistenceException("Could not add horse to database", e);
        }

    }

}
