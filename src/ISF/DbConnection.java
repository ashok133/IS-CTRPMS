package ISF;

import java.sql.*;  

/**
 * 
 * @author tagregory, ashok133, pallavi, connor
 *
 */
public class DbConnection {
    
    static private Connection theConnection = null;
    
    // Update these, or it won't work.
    static private String DB_USER = "asmpatel_1763816";
    static private String DB_PASSWORD = "9yIKj2m";
    static private String DB_SERVER = "db.webteach.iu.edu";
    /**
     * Private constructor, as we'll only use the static method
     */
    private DbConnection() {}
    
    static public Connection getConnection() {
        try {
            // Do we have a connection already? 
            if (theConnection != null) {
                if (!theConnection.isClosed()) {
                    return theConnection;
                }
            }
        
            // Step 1: Load the driver class
        
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");  
        } catch (ClassNotFoundException|SQLException e) {
            System.err.println("ERROR: Class not found.");
            System.err.println("Make sure you have the MSSQL jdbc driver as part of your project.");
            System.err.println(e.getMessage());
            System.exit(1); //End the application
        }
   
        // Step 2: Create  the connection object  
        try {
            
            // localhost
            theConnection = DriverManager.getConnection(
                "jdbc:sqlserver://" + DB_SERVER + ":1433"
                    + ";databaseName=" + DB_USER
                    + ";user=" + DB_USER
                    + ";password=" + DB_PASSWORD
            );
        } catch (Exception sqe) {
            System.err.println("ERROR: Exception connecting to database.");
            System.err.println("Something is wrong with your connection string. Maybe you aren't connected to the IU VPN, or maybe you have the wrong username or password.");
            System.err.println("For more information about the IU VPN, visit https://kb.iu.edu/d/ajrq");
            System.err.println(sqe.getMessage());
            System.exit(1); //End the application
        }
        
        return theConnection;
    }
}
