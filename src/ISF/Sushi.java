/*
 * This is a sample Model class. You should be able to copy and paste it,
 * simply changing the model's attributes, getters and setters, and tweaking the
 * create, save, and all methods (and the constructor) to have the correct SQL.
 * (Don't forget to tweak toString() too.)
 *
 * It may look a bit daunting, but it isn't. I promise.
 */
package ISF;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 *
 * @author tomgreg
 */


// rename this to SushiModel 

public class Sushi extends Model
{

    long   id;
    String name;
    double price;
    
    public Sushi() {
        id   = 0;
        name = "";
        price = 0.0;
    }
    
    /**
     * This constructor will be used when creating instances from database rows
     * 
     * @param theId
     * @param theName
     * @param thePrice 
     */
    public Sushi(long theId, String theName, double thePrice) {
        id = theId;
        name = theName;
        price = thePrice;
    }
    
    /**
     * With this constructor, given only an id/PK as a parameter, 
     * make a database call to retrieve the row from the database.
     * 
     * @param theId 
     */
    public Sushi(long theId) {
        Connection db        = null;
        PreparedStatement ps = null;
        ResultSet rs         = null;
        
        try {
            // Step 1: Get a database connection
            db = DbConnection.getConnection();

            // Step 2: Create the statement object
            String sql = "SELECT id, name, price FROM sushi WHERE id = ?"; // Always a good idea to name parameters, instead of using SELECT *
            ps = db.prepareStatement(sql);  
  
            // Step 3: Enter parameters (if any)
            ps.setLong(1, theId); // Set the first parameter (1) to the value of theId
            
            // Step 4: Execute query
            rs = ps.executeQuery();
        
            // Step 5: Process results (for a select) 
            while(rs.next())  {
                //Yes, this is in a while loop, but we really only want to do this once, so we'll return immediately
                this.id = rs.getLong("id");
                this.name = rs.getString("name");
                this.price = rs.getDouble("price");
                
                return;
            }
            
            // If we make it to here, a row with the given id was not found,
            // and we should throw some sort of error. Normally, returning null
            // is a good choice, but this is a constructor, so we can't.
            //
            // IllegalArguement isn't exactly the best choice of Exception, but
            // it will work for our purposes.
            throw new IllegalArgumentException();

        } catch (SQLException sqe){
            System.err.println("ERROR: SQL Exception.");
            System.err.println(sqe.getMessage());
        } finally {
            // Lastly, it's good practice to close all of your resources when finished.
            try { rs.close(); } catch (Exception e) { /* ignored */ }
            try { ps.close(); } catch (Exception e) { /* ignored */ }
            try { db.close(); } catch (Exception e) { /* ignored */ }
        }
    }
    
    public void create() {
        Connection db        = null;
        PreparedStatement ps = null;
        ResultSet rs         = null;
        
        long tempId = Model.getNextPrimaryKey("sushi"); // Pass the table name
        
        try {
            // Step 1: Get a database connection
            db = DbConnection.getConnection();

            // Step 2: Create the statement object
            String sql = "INSERT INTO sushi (id, name, price) VALUES (?,?,?)";
            ps = db.prepareStatement(sql);  

            // Step 3: Enter parameters (if any)
            ps.setLong(1, tempId);  // Set the first parameter to the value of theId
            ps.setString(2, name);  // Set the second parameter to the value of this.name
            ps.setDouble(3, price); // Set the third parameter to the value of this.price
            
            // Step 4: Execute query
            ps.executeUpdate();
        
            // Step 5: Process results (for a select) 
            // ... Not a SELECT, so nothing to do.
            
            this.id = tempId; // Update this instance to have the new PK we used

        } catch (SQLException sqe){
            System.err.println("ERROR: SQL Exception.");
            System.err.println(sqe.getMessage());
        } finally {
            // Lastly, it's good practice to close all of your resources when finished.
            try { rs.close(); } catch (Exception e) { /* ignored */ }
            try { ps.close(); } catch (Exception e) { /* ignored */ }
            try { db.close(); } catch (Exception e) { /* ignored */ }
        }
    }
    
    public void save() {
        Connection db        = null;
        PreparedStatement ps = null;
        ResultSet rs         = null;
                
        try {
            // Step 1: Get a database connection
            db = DbConnection.getConnection();

            // Step 2: Create the statement object
            String sql = "UPDATE sushi SET name = ?, price = ? WHERE id = ?";
            ps = db.prepareStatement(sql);  
  
            // Step 3: Enter parameters (if any)
            ps.setString(1, name);  // Set the first parameter to the value of this.name
            ps.setDouble(2, price); // Set the second parameter to the value of this.price
            ps.setLong(3, id);      // Set the third parameter to the value of this.id

            
            // Step 4: Execute query
            ps.executeUpdate();
        
            // Step 5: Process results (for a select) 
            // ... Not a SELECT, so nothing to do.
            
        } catch (SQLException sqe){
            System.err.println("ERROR: SQL Exception.");
            System.err.println(sqe.getMessage());
        } finally {
            // Lastly, it's good practice to close all of your resources when finished.
            try { rs.close(); } catch (Exception e) { /* ignored */ }
            try { ps.close(); } catch (Exception e) { /* ignored */ }
            try { db.close(); } catch (Exception e) { /* ignored */ }
        }
    }
    
    static public ArrayList<Sushi> all() {
        Connection db  = null;
        Statement stmt = null;
        ResultSet rs   = null;
        
        ArrayList<Sushi> sushiList = new ArrayList<Sushi>();
        
        try {
            // Step 1: Get a database connection
            db = DbConnection.getConnection();

            // Step 2: Create the statement object
            String sql = "SELECT id, name, price FROM sushi"; // Always a good idea to name parameters, instead of using SELECT *

            stmt = db.createStatement();
  
            // Step 3: Enter parameters (if any)
            // ... no parameters this time
            
            // Step 4: Execute query
            rs = stmt.executeQuery(sql);
        
            // Step 5: Process results (for a select) 
            while(rs.next())  {
                
                Sushi s = new Sushi(
                    rs.getLong("id"),
                    rs.getString("name"),
                    rs.getDouble("price")
                );
                
                sushiList.add(s);
            }

        } catch (SQLException sqe){
            System.err.println("ERROR: SQL Exception.");
            System.err.println(sqe.getMessage());
        } finally {
            // Lastly, it's good practice to close all of your resources when finished.
            try { rs.close(); } catch (Exception e) { /* ignored */ }
            try { stmt.close(); } catch (Exception e) { /* ignored */ }
            try { db.close(); } catch (Exception e) { /* ignored */ }
        }
        
        // We've added all of the rows to the list, so return it
        return sushiList;
    }
    
    /** UTILITY METHODS 
     * It's always useful to be able to print out our models. 
     * Overriding toString() is an easy way to make that happen.
     */
    
    public String toString() {
        // '\t' is the <tab> character
        // In String.format, 3 character minimum, two after the decimal place
        // See docs for String.format here: http://docs.oracle.com/javase/8/docs/api/java/util/Formatter.html#syntax
        return "" + id + "\t" + name + "\t" + String.format("%3.2f", price);
    }
    
    /** GETTERS & SETTERS 
     *
     * Notice there is no setter for id. This is on purpose, as we want only 
     * this Model class to be able to change it.
     * 
     * Otherwise, we have getters/setters for each attribute.
     **/
    
    public long getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String theName) {
        name = theName;
    }
    
    public double getPrice() {
        return price;
    }
    
    public void setPrice(double thePrice) {
        price = thePrice;
    }
}
