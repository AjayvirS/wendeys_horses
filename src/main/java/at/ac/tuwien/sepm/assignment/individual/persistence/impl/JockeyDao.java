package at.ac.tuwien.sepm.assignment.individual.persistence.impl;

import at.ac.tuwien.sepm.assignment.individual.entity.Jockey;
import at.ac.tuwien.sepm.assignment.individual.entity.Simulation;
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
import java.util.Arrays;
import java.util.HashMap;


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

            insertOneHistoryWhenUpdated(dbJockey);

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
            Jockey dbJockey=findOneById(id);
            insertOneHistoryWhenDeleted(dbJockey);

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
    public ArrayList<Jockey> getAllOrFiltered(Jockey jockey) throws PersistenceException {
        LOGGER.info("Get jockey/s with following optional parameters: "+jockey.printOptionals());
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
        }
        return filteredList;

    }

    /*
        whenever the deleteOneById method is called, this one is gets called as well
        to insert deleted jockey in a history table for simulation purposes
     */
    private void insertOneHistoryWhenDeleted(Jockey dbJockey) throws PersistenceException {
        LOGGER.info("Check if jockey with id " + dbJockey.getId() + " exists: insert into jockeyhistory if no, else do nothing");
        String sql = "SELECT * FROM jockeyhistory WHERE jockeyId=?";
        try {
            PreparedStatement ps = dbConnectionManager.getConnection().prepareStatement(sql);
            ps.setInt(1, dbJockey.getId());
            ResultSet rs = ps.executeQuery();

            if (!rs.next()) {

                PreparedStatement statement;
                LOGGER.info("Insert into jockeyHistory with "+dbJockey.getId());
                String sqlHistory="INSERT INTO jockeyhistory(jockeyid, name, skill, updated, deleted) VALUES (?,?,?,?,?)";
                try {

                    statement = dbConnectionManager.getConnection().prepareStatement(sqlHistory);
                    statement.setInt(1,dbJockey.getId());
                    statement.setString(2, dbJockey.getName());
                    statement.setDouble(3, dbJockey.getSkill());
                    statement.setTimestamp(4, Timestamp.valueOf(dbJockey.getUpdated()));
                    statement.setBoolean(5, true);
                    statement.executeUpdate();
                    statement.close();

                } catch (SQLException e) {
                    LOGGER.error("Could not insert into jockeyhistory table", e);
                    throw new PersistenceException("Could not add jockey to database", e);
                }


            } else {
                LOGGER.info("Latest updated jockey already exists in jockeyhistory, no need to re-insert!");
            }
        } catch (SQLException e) {
            LOGGER.error("Could not insert into jockeyhistory table");
            throw new PersistenceException("Could not add jockey to database", e);
        }

    }

    /*
       whenever the updateOneById method is called, this one is gets called as well
       to insert deleted jockey in a history table for simulation purposes
    */
    private void insertOneHistoryWhenUpdated(Jockey dbJockey) throws PersistenceException {
        PreparedStatement statement;
        LOGGER.info("Insert into jockeyHistory with "+dbJockey.getId());
        String sqlHistory="INSERT INTO jockeyhistory(jockeyid, name, skill, updated) VALUES (?,?,?,?)";
        try {

            statement = dbConnectionManager.getConnection().prepareStatement(sqlHistory);
            statement.setInt(1,dbJockey.getId());
            statement.setString(2, dbJockey.getName());
            statement.setDouble(3, dbJockey.getSkill());
            statement.setTimestamp(4, Timestamp.valueOf(dbJockey.getUpdated()));
            statement.executeUpdate();
            statement.close();

        } catch (SQLException e) {
            LOGGER.error("Could not insert into jockeyhistory table", e);
            throw new PersistenceException("Could not add jockey to database", e);
        }

    }

    /*
        is used by simulationservice to get the correct version of jockeys for the simulation
        uses date to get the closest jockeys to CREATED date of simulation
        uses jockeyIDs of jockeys who participated in the simulation
     */
    public HashMap<Integer, Jockey> getCorrectJockeysForSimulation(Simulation sim, Integer[]jockeyIDs) throws PersistenceException, NotFoundException {
        LOGGER.info("Get correct version of participant jockeys");
        //this makes a union of horses and horsehistory and partitions them by id of the horses
        //then it proceeds to get the most recently updated horse right before the created date
        HashMap<Integer, Jockey> jockeysByID= new HashMap<>();
        Connection con=dbConnectionManager.getConnection();

        String sql="SELECT *\n" +
            "FROM (\n" +
            "SELECT jockeyid, name, skill, updated, deleted FROM jockeyhistory\n" +
            "   UNION\n" +
            "   SELECT id, name, skill, updated, false as deleted  FROM jockey GROUP BY id\n" +
            "   ) AS t\n" +
            "WHERE updated = (\n" +
            "    SELECT MAX(updated)\n" +
            "    FROM (\n" +
            "SELECT jockeyid, name, skill,updated, deleted FROM jockeyhistory\n" +
            "   UNION\n" +
            "   SELECT id, name, skill, updated, false as deleted FROM jockey GROUP BY id\n" +
            "   ) \n" +
            "    WHERE jockeyid=t.jockeyid\n" +
            "        AND updated <= ? AND deleted=FALSE AND t.jockeyid IN (SELECT * from TABLE(x INT=?)))";

        try {
            Array jockeyIDIn = con.createArrayOf("BIGINT", jockeyIDs);
            PreparedStatement stmt=con.prepareStatement(sql);
            stmt.setTimestamp(1, Timestamp.valueOf(sim.getCreated()));
            stmt.setObject(2,jockeyIDIn);
            ResultSet rs=stmt.executeQuery();

            while(rs.next()){
                jockeysByID.put(rs.getInt(1), new Jockey(null,rs.getString(2),
                    rs.getDouble(3), null, null) );
            }
            ArrayList<Integer> missingJockeyIDs= new ArrayList<>(Arrays.asList(jockeyIDs));

            missingJockeyIDs.removeAll(jockeysByID.keySet());

            if(!missingJockeyIDs.isEmpty()){
                LOGGER.info("Could not find participants jockeys: "+missingJockeyIDs.toString());
                throw new NotFoundException("Following Jockeys were not found: "+missingJockeyIDs.toString());
            }


            return jockeysByID;

        } catch (SQLException e) {
            LOGGER.error("Problem while executing SQL SELECT statement", e);
            throw new PersistenceException("Error while accessing database", e);
        }
    }





}
