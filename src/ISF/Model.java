/*
 * You should not need to edit this file, but do look through it to see how it works.
 */
package ISF;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author tomgreg
 */
public abstract class Model {
    
    /**
     * All subclasses should implement a default constructor that sets default 
     * values for attributes.
     * 
     * The developer would later need to call "Create" to commit the row 
     * to the database.
     */
    public Model() {}
    
    /**
     * When the constructor is passed a long, it performs a db search, and creates
     * an instance of the model object based on the db table. (i.e., SELECT)
     * 
     * Intended to be overwritten in sub class.
     * 
     * @param id Primary key of model's db table
     */
    public Model(long id) {}
    
    /**
     * When Implementing Model, sub classes are required to provide an 
     * update method, which executes an UPDATE statement in the database.
     */
//     abstract public void save();
     
     /**
     * When Implementing Model, sub classes are required to provide a 
     * create method, which executes a CREATE statement in the database,
     * then updates the id attribute of the instance appropriately.
     */
//     abstract public void create();
     
     /**
      * We could also do
      * 
      * abstract public void delete()
      * 
      * ... but I don't want to require all entities to implement a delete.
      */
     
     /**
      * This is a poor method. Don't ever do it this way in production.
      * Oracle's method of creating sequential primary keys is a bit messy for
      * beginners. Instead, we're going to count the number of rows in a table, 
      * and add one. This method is slow, not thread safe, and opens a potential
      * SQL injection security hole. Thus, don't ever use it in practice. It's
      * here only to make this project simpler.
      * 
      * @param table
      * @return long The next primary key (id) in the given table
      */
     static public long getNextPrimaryKey(String table) {
        Connection db   = null;
        Statement  stmt = null;
        ResultSet rs    = null;
        
        try {
            // Step 1: Get a database connection
            db = DbConnection.getConnection();

            // Step 2: Create the statement object
            String sql = "SELECT COUNT(1) FROM " + table; // Be careful! potential SQL Injection vulnerability here. Not for use in production code.
            stmt = db.createStatement();
  
            // Step 3: Enter parameters (if any)
            // ... no parameters in this query
            
            // Step 4: Execute query
            rs = stmt.executeQuery(sql);
        
            // Step 5: Process results (for a select) 
            while(rs.next())  {
                //Yes, this is in a while loop, but we really only want to do this once, so we'll return immediately
                return rs.getLong(1) + 1; // return the first column from the first row of the ResultSet
            }
            
            // If we make it to here, something terrible has happened.

        } catch (SQLException sqe){
            System.err.println("ERROR: SQL Exception.");
            System.err.println(sqe.getMessage());
        } finally {
            // Lastly, it's good practice to close all of your resources when finished.
            try { rs.close(); } catch (Exception e) { /* ignored */ }
            try { stmt.close(); } catch (Exception e) { /* ignored */ }
            try { db.close(); } catch (Exception e) { /* ignored */ }
        }
        throw new IllegalArgumentException();
     }
}
