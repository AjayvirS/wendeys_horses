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
import java.util.ArrayList;


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

    @Override
    public void deleteOneById(Integer id) throws PersistenceException, NotFoundException{
        LOGGER.info("Delete jockey with id "+id);

        String sql="DELETE FROM jockey WHERE id=?";
        try{
            PreparedStatement statement=dbConnectionManager.getConnection().prepareStatement(sql);
            statement.setInt(1,id);
            int checkZero=statement.executeUpdate();

            statement.close();
            if(checkZero>0){
                return;
            } else{
                LOGGER.error("Could not find jockey with id "+id);
                throw new NotFoundException("Could not find jockey with id "+id);
            }


        } catch (SQLException e) {
            LOGGER.error("Could not delete jockey with id "+id,e);
            throw new PersistenceException("Could not delete jockey with id "+id,e);
        }

    }


    @Override
    public ArrayList<Jockey> getAllOrFiltered(Jockey jockey) throws PersistenceException, NotFoundException {
        LOGGER.info("Get jockey/s with optional parameters: "+jockey.printOptionals());
        ArrayList<Jockey> filteredList= new ArrayList<>();
        String sql="SELECT * FROM jockey WHERE name LIKE ? AND skill>=?";
        try{
            PreparedStatement statement=dbConnectionManager.getConnection().prepareStatement(sql);
            if(jockey.getName()==null){
                statement.setString(1, "%");
            } else statement.setString(1, "%"+jockey.getName()+"%");
            if(jockey.getSkill()==null){
                statement.setDouble(2, -Double.MAX_VALUE);
            } else statement.setDouble(2, jockey.getSkill());

            ResultSet rs=statement.executeQuery();

            while(rs.next()){
                filteredList.add(dbResultToJockey(rs));
            }

            statement.close();
        } catch (SQLException e) {
            LOGGER.error("Problem while executing SQL SELECT statement");
            throw new PersistenceException("Error while accessing database");
        }

        if(filteredList.isEmpty()){
            LOGGER.error("Could not find jockey/s with following optional parameters: "+jockey.printOptionals());
            throw new NotFoundException("Could not find jockeys with optional parameters: "+jockey.printOptionals());
        } else return filteredList;

    }




}
