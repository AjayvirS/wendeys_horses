package at.ac.tuwien.sepm.assignment.individual.persistence.impl;

import at.ac.tuwien.sepm.assignment.individual.entity.Horse;
import at.ac.tuwien.sepm.assignment.individual.entity.SimulationParticipantOutput;
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
import java.util.HashMap;


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

            int key=1;
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
        Connection con = dbConnectionManager.getConnection();
        LOGGER.info("Update horse with id " + id);
        String sql = "UPDATE horse SET name = ?, breed = ?, min_Speed=?, max_Speed=?, updated=? WHERE id=?";
        try {

            PreparedStatement statement;
            //throws NotFoundException
            dbHorse = findOneById(id);


            if(horse.getMinSpeed()!=null){
                if(dbHorse.getMaxSpeed()<horse.getMinSpeed()){
                    LOGGER.error("Maximum speed is smaller than minimum speed");
                    throw new InvalidDataException("Input min speed is larger than max speed present in database!");
                }
            }
            if(horse.getMaxSpeed()!=null){
                if(dbHorse.getMinSpeed()>horse.getMaxSpeed()){
                    throw new InvalidDataException("Input max speed is smaller than min speed present in database!");
                }
            }

            //add horse to history table before updating
            insertOneHistory(dbHorse, con);


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

    private void insertOneHistory(Horse dbHorse, Connection dbConnection) throws PersistenceException {
        PreparedStatement statement;
        LOGGER.info("Insert into horsehistory with "+dbHorse.getId());
        String sqlHistory="INSERT INTO horsehistory(horseId, name, breed, min_speed, max_speed, updated) VALUES (?,?,?,?,?,?)";
        try {

            statement = dbConnection.prepareStatement(sqlHistory);
            statement.setInt(1,dbHorse.getId());
            statement.setString(2, dbHorse.getName());
            statement.setString(3, dbHorse.getBreed());
            statement.setDouble(4, dbHorse.getMinSpeed());
            statement.setDouble(5, dbHorse.getMaxSpeed());
            statement.setTimestamp(6, Timestamp.valueOf(dbHorse.getUpdated()));
            statement.executeUpdate();
            statement.close();

        } catch (SQLException e) {
            LOGGER.error("Could not insert into horsehistory table", e);
            throw new PersistenceException("Could not add horse to database", e);
        }

    }


    @Override
    public void deleteOneById(Integer id) throws PersistenceException, NotFoundException{
        Connection dbConnection=dbConnectionManager.getConnection();
        LOGGER.info("Delete horse with id "+id);
        String sql="DELETE FROM horse WHERE id=?";
        try{

            Horse dbHorse=findOneById(id);
            insertOneHistoryWhenDeleted(dbHorse,dbConnection);

            PreparedStatement statement=dbConnection.prepareStatement(sql);
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

    private void insertOneHistoryWhenDeleted(Horse dbHorse, Connection dbConnection) throws PersistenceException {
        LOGGER.info("Check if horse with id "+dbHorse.getId()+ " exists: insert into horsehistory if no, else do nothing");
        String sql="SELECT * FROM horsehistory WHERE horseId=?";
        try {
            PreparedStatement ps = dbConnectionManager.getConnection().prepareStatement(sql);
            ps.setInt(1, dbHorse.getId());
            ResultSet rs=ps.executeQuery();

            if(!rs.next()){
                insertOneHistory(dbHorse, dbConnection);
            }
            else{
                LOGGER.info("Latest updated horse already exists in horsehistory, no need to re-insert!");
            }
        }catch(SQLException e){
            LOGGER.error("Could not insert into horsehistory table");
            throw new PersistenceException("Could not add horse to database", e);
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
            throw new PersistenceException("Error while accessing database", e);
        }

        if(filteredList.isEmpty()){
            LOGGER.error("Could not find horse/s with following optional parameters: "+horse.printOptionals());
            throw new NotFoundException("Could not find horses with optional parameters: "+horse.printOptionals());
        } else return filteredList;

    }


    public HashMap<Integer, Horse> getCorrectHorsesForSimulation(LocalDateTime created, Integer[]horseIDs) throws PersistenceException, NotFoundException {
        LOGGER.info("Get correct version of participant horses");
        //this makes a union of horses and horsehistory and partitions them by id of the horses
        //then it proceeds to get the most recently updated horse right before the created date
        HashMap<Integer, Horse> horsesById= new HashMap<>();
        Connection con=dbConnectionManager.getConnection();



        String sql="SELECT TOP ?  tt.*\n" +
            "FROM (\n" +
            "   SELECT horseid, name, breed, min_speed, max_speed, updated FROM horsehistory\n" +
            "   UNION\n" +
            "   SELECT id, name, breed, min_speed, max_speed, updated FROM horse\n" +
            "   ) as tt\n" +
            "INNER JOIN (SELECT horseid, MAX(updated) AS updated FROM \n" +
            "(\n" +
            "   SELECT horseid, name, breed, min_speed, max_speed, updated from horsehistory\n" +
            "   UNION\n" +
            "   SELECT id, name, breed, min_speed, max_speed, updated FROM horse\n" +
            ")\n" +
            "GROUP BY horseid) AS grouptt\n" +
            "ON tt.horseid=grouptt.horseid AND tt.updated<? AND tt.horseid IN (SELECT * from TABLE(x INT=?))\n" +
            "ORDER by tt.updated DESC";

        try {
            Array horseIDIn = con.createArrayOf("BIGINT", horseIDs);

            System.out.println(horseIDIn);

            PreparedStatement stmt=con.prepareStatement(sql);
            stmt.setInt(1, horseIDs.length);
            stmt.setTimestamp(2, Timestamp.valueOf(created));
            stmt.setArray(3,horseIDIn);
            ResultSet rs=stmt.executeQuery();

            while(rs.next()){
                horsesById.put(rs.getInt(1), new Horse(null,rs.getString(2),
                    rs.getString(3), rs.getDouble(4),rs.getDouble(5), null, null) );
            }

            if(horsesById.isEmpty()){
                LOGGER.info("Could not find participants horses");
                throw new NotFoundException("Could not find one or more participant horses");
            }

            return horsesById;

        } catch (SQLException e) {
            LOGGER.error("Problem while executing SQL SELECT statement", e);
            throw new PersistenceException("Error while accessing database", e);
        }
    }



}
