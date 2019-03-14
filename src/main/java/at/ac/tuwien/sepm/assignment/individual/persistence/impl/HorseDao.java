package at.ac.tuwien.sepm.assignment.individual.persistence.impl;

import at.ac.tuwien.sepm.assignment.individual.entity.Horse;
import at.ac.tuwien.sepm.assignment.individual.exceptions.NotFoundException;
import at.ac.tuwien.sepm.assignment.individual.persistence.IHorseDao;
import at.ac.tuwien.sepm.assignment.individual.persistence.exceptions.PersistenceException;
import at.ac.tuwien.sepm.assignment.individual.persistence.util.DBConnectionManager;
import at.ac.tuwien.sepm.assignment.individual.service.exceptions.InvalidDataException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;


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
            statement.close();
        } catch (SQLException e) {
            LOGGER.error("Problem while executing SQL SELECT statement for reading horse with id " + id, e);
            throw new PersistenceException("Error while accessing database", e);
        }


        if (horse != null) {
            return horse;
        } else {
            LOGGER.error("Could not find horse with id "+id);
            throw new NotFoundException("Could not find horse with id " + id);
        }

    }

    @Override
    public Horse insertOne(Horse horse) throws PersistenceException {
        LOGGER.info("Insert horse");
        String sql = "INSERT INTO HORSE(name,breed,min_Speed, max_Speed, created, updated) VALUES (?,?,?,?,?,?);";

        try {

            Timestamp tmstmp = Timestamp.valueOf(LocalDateTime.now());
            PreparedStatement statement = dbConnectionManager.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, horse.getName());
            statement.setString(2, horse.getBreed());
            statement.setDouble(3, horse.getMinSpeed());
            statement.setDouble(4, horse.getMaxSpeed());
            statement.setTimestamp(5, tmstmp);
            statement.setTimestamp(6, tmstmp);
            statement.executeUpdate();
            ResultSet rs = statement.getGeneratedKeys();

            int key = 1;
            if (rs != null && rs.next()) {
                key = rs.getInt(1);
            }
            horse.setCreated(tmstmp.toLocalDateTime());
            horse.setUpdated(tmstmp.toLocalDateTime());
            horse.setId(key);
            statement.close();

            return horse;
        } catch (SQLException e) {
            LOGGER.error("Problem while adding horse to database", e);
            throw new PersistenceException("Could not add horse to database", e);
        }

    }

    @Override
    public Horse updateOneById(Integer id, Horse horse) throws PersistenceException, NotFoundException, InvalidDataException {
        Horse dbHorse = null;
        LOGGER.info("Update horse with id " + id);
        String sql = "UPDATE horse SET name = ?, breed = ?, min_Speed=?, max_Speed=?, updated=? WHERE id=?";
        try {

            Connection con = dbConnectionManager.getConnection();
            PreparedStatement statement = con.prepareStatement(sql);
            statement.clearParameters();

            //throws NotFoundException
            dbHorse = findOneById(id);

            if(horse.getMinSpeed()!=null){
                if(dbHorse.getMaxSpeed()<horse.getMinSpeed()){
                    throw new InvalidDataException("Input min speed is larger than max speed present in database!");
                }
            }
            if(horse.getMaxSpeed()!=null){
                if(dbHorse.getMinSpeed()>horse.getMaxSpeed()){
                    throw new InvalidDataException("Input max speed is smaller than min speed present in database!");
                }
            }

            statement = con.prepareStatement(sql);
            Timestamp tmstmp = Timestamp.valueOf(LocalDateTime.now());
            statement.setString(1, horse.getName()==null?dbHorse.getName():horse.getName());
            statement.setString(2, horse.getBreed()==null?dbHorse.getBreed():horse.getBreed());
            statement.setDouble(3, horse.getMinSpeed()==null?dbHorse.getMinSpeed():horse.getMinSpeed());
            statement.setDouble(4, horse.getMaxSpeed()==null?dbHorse.getMaxSpeed():horse.getMaxSpeed());
            statement.setTimestamp(5, tmstmp);
            statement.setInt(6, id);
            statement.executeUpdate();
            statement.clearParameters();

            //throws NotFoundException
            dbHorse = findOneById(id);
            statement.close();

            return dbHorse;
        } catch (
            SQLException e) {
            LOGGER.error("Could not update horse with " + id, e);
            throw new PersistenceException("Could not update horse with id " + id, e);
        }

    }


    @Override
    public void deleteOneById(Integer id) throws PersistenceException, NotFoundException{
        LOGGER.info("Delete horse with id "+id);

        String sql="DELETE FROM horse WHERE id=?";
        try{
            PreparedStatement statement=dbConnectionManager.getConnection().prepareStatement(sql);
            statement.setInt(1,id);
            int checkZero=statement.executeUpdate();

            statement.close();
            if(checkZero>0){
                return;
            } else{
                LOGGER.error("Could not find horse with id "+id);
                throw new NotFoundException("Could not find horse with id "+id);
            }


        } catch (SQLException e) {
            LOGGER.error("Could not delete horse with id "+id,e);
            throw new PersistenceException("Could not delete horse with id "+id,e);
        }

    }

    @Override
    public ArrayList<Horse> getAllOrFiltered(Horse horse) throws PersistenceException, NotFoundException {
        LOGGER.info("Get horse/s with optional parameters: "+horse.printOptionals());
        ArrayList<Horse> filteredList= new ArrayList<>();
        String sql="SELECT * FROM horse WHERE name LIKE ? AND COALESCE(horse.breed,'') LIKE ? AND min_speed>=? AND max_speed<=?";
        try{
            PreparedStatement statement=dbConnectionManager.getConnection().prepareStatement(sql);
            if(horse.getName()==null){
                statement.setString(1, "%");
            } else statement.setString(1, "%"+horse.getName()+"%");
            if(horse.getBreed()==null){
                statement.setString(2, "%");
            } else statement.setString(2, "%"+horse.getBreed()+"%");
            if(horse.getMinSpeed()==null){
                statement.setDouble(3, 40.0);
            } else statement.setDouble(3, horse.getMinSpeed());
            if(horse.getMaxSpeed()==null){
                statement.setDouble(4, 60.0);
            } else statement.setDouble(4, horse.getMaxSpeed());

            ResultSet rs=statement.executeQuery();

            while(rs.next()){
                filteredList.add(dbResultToHorseDto(rs));
            }

            statement.close();
        } catch (SQLException e) {
            LOGGER.error("Problem while executing SQL SELECT statement");
            throw new PersistenceException("Error while accessing database");
        }

        if(filteredList.isEmpty()){
            LOGGER.error("Could not find horse/s with following optional parameters: "+horse.printOptionals());
            throw new NotFoundException("Could not find horses with optional parameters: "+horse.printOptionals());
        } else return filteredList;

    }



}
