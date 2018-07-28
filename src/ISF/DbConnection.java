package ISF;

import java.sql.*;  

/**
 * For this class to work, you're going to need the Microsoft JDBC driver
 * added to your **project's** "Libraries" folder.
 * 
 * Fet it here: https://docs.microsoft.com/en-us/sql/connect/jdbc/microsoft-jdbc-driver-for-sql-server?view=sql-server-2017
 * 
 * In addition, you MUST update the username and password in the static 
 * constants of this class in order for this connection class work.
 * 
 * Other than updating your username and password, you should not need to edit this file.
 * 
 * @author tagregory
 */
public class DbConnection {
    
    static private Connection theConnection = null;
    
    // Update these, or it won't work.
    static private String DB_USER = "asmpatel_1763816";    // Your user name here
    static private String DB_PASSWORD = "9yIKj2m";           // Your password here
    static private String DB_SERVER = "db.webteach.iu.edu"; // Shouldn't need to change these
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
