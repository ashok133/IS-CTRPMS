package ISF;

import java.util.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class InfoSystem extends Model {

    String e_id, alive_status, grade, course_name, fname, lname;
    int section_id, course_id;

    int count_check_counter = 0;


    public InfoSystem() {
        this.e_id = "";
        this.section_id = -1;
        this.alive_status = "";
        this.grade = "";
        this.fname = "";
        this.lname = "";
        this.course_name = "";
        this.course_id = -1;
    }

    /**
     * This constructor will be used when creating instances from database rows
     *
     * @param theEId
     * @param theSectionId
     * @param theAliveStatus
     * @param theGrade
     * @param theFName
     * @param theLName
     * @param theCourseName
     */
    public InfoSystem(String theFName, String theLName, String theEId, int theSectionId, String theCourseName, String theAliveStatus, String theGrade) {
        e_id = theEId;
        section_id = theSectionId;
        alive_status = theAliveStatus;
        grade = theGrade;
        fname = theFName;
        lname = theLName;
    }

    /**
     * With this constructor, given only an id/PK as a parameter,
     * make a database call to retrieve the row from the database.
     *
     * @param theEId, theSectionId
     */
    public InfoSystem(String theEId ) {
        Connection db        = null;
        PreparedStatement ps = null;
        ResultSet rs         = null;

        try {
            // Step 1: Get a database connection
            db = DbConnection.getConnection();

            // Step 2: Create the statement object
            String sql = "SELECT s.fname, s.lname, s.e_id, sec.section_id, c.course_name, e.alive_status, e.grade\n" +
                    "FROM Student s, Enrollment e, Section sec, Course c\n" +
                    "WHERE s.e_id = e.e_id \n" +
                    "\tAND e.section_id = sec.section_id \n"+
                    "\ts.e_id = ?"; // Always a good idea to name parameters, instead of using SELECT *
            ps = db.prepareStatement(sql);

            // Step 3: Enter parameters (if any)
            ps.setString(1, theEId); // Set the first parameter (1) to the value of theId

            // Step 4: Execute query
            rs = ps.executeQuery();

            // Step 5: Process results (for a select)
            while(rs.next())  {
                //Yes, this is in a while loop, but we really only want to do this once, so we'll return immediately
                this.fname = rs.getString("fname");
                this.lname = rs.getString("lname");
                this.e_id = rs.getString("e_id");
                this.section_id = rs.getInt("section_id");
                this.course_name = rs.getString("course_name");
                this.alive_status = rs.getString("alive_status");
                this.grade = rs.getString("grade");

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

    /*

    public void create() {
        Connection db        = null;
        PreparedStatement ps = null;
        ResultSet rs         = null;

        long tempId = Model.getNextPrimaryKey("Enrollment"); // Pass the table name

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
            try { rs.close(); } catch (Exception e) { // ignored
                 }
            try { ps.close(); } catch (Exception e) { // ignored
                 }
            try { db.close(); } catch (Exception e) { // ignored
                 }
        }
    }

*/

    public void populateStudents(String fileName) throws SQLException {
        Connection db        = null;
        PreparedStatement ps = null;



        String lname = "";
        String fname = "";
        String email = "";
        String e_id = "";
        List<String> listOfStudents = null;

        FileReaderApp reader = new FileReaderApp();
        listOfStudents = reader.readContents(fileName);
        System.out.println("Reached populateStudents method...");

        for(String theLine : listOfStudents) {
            String [] arrOfStr = theLine.split(",", 7);
            lname = arrOfStr[0];
            fname = arrOfStr[1];
            email = arrOfStr[2];
            e_id = arrOfStr[3];

            String sql = "INSERT INTO Student values (?,?,?,?)";

            db = DbConnection.getConnection();

            ps = db.prepareStatement(sql);

            ps.setString(1,lname);
            ps.setString(2,fname);
            ps.setString(3,email);
            ps.setString(4,e_id);

            ps.executeUpdate();

            System.out.println("Successfully inserted record for: " + lname + " " + fname + "(" + e_id + ")");


            try { ps.close(); } catch (Exception e) { // ignored
            }
            try { db.close(); } catch (Exception e) { // ignored
            }
        }
    }

    public void populateCourses(String fileName) throws SQLException {
        Connection db2        = null;
        PreparedStatement ps2 = null;

        String cname = "";

        List<String> listOfCourses = null;

        FileReaderApp reader = new FileReaderApp();
        listOfCourses = reader.readContents(fileName);
        System.out.println("Reached populateCourses method...");

        for(String theLine : listOfCourses) {

            cname = theLine;

            long c_id = Model.getNextPrimaryKey("Course");

            db2 = DbConnection.getConnection();

            String sql = "INSERT INTO Course values (?,?)";

            ps2 = db2.prepareStatement(sql);

            ps2.setLong(1,c_id);
            ps2.setString(2,cname);

            ps2.executeUpdate();

            System.out.println("Successfully inserted record for: " + c_id + ": " + cname );


            try { ps2.close(); } catch (Exception e) { // ignored
            }
            try { db2.close(); } catch (Exception e) { // ignored
            }
        }
    }

    public int countCheck (int no_students) {
        if (no_students > 35) {
            countCheck((int) Math.ceil(no_students / 2));
            count_check_counter ++;
        }
        else
            return (int) Math.pow(2,count_check_counter);
    }

    public void populateSections () throws SQLException {
        Connection db3 = null;
        Statement stmt = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        System.out.println("1");

        ArrayList<Integer> course_ids = new ArrayList<>() ;

        String sql1 = "SELECT course_id from Course";
        db3 = DbConnection.getConnection();
        System.out.println("2");

        stmt = db3.createStatement();

        rs = stmt.executeQuery(sql1);
        System.out.println("3");

        while(rs.next()) {
            course_ids.add(rs.getInt("course_id"));
        }
        System.out.println("4");
        for (Integer is_object: course_ids) {
            System.out.println(is_object.toString());
        }


//
//        // update
//        int no_divs = countCheck()

        for (int i=0; i<)

        try { rs.close(); } catch (Exception e) { // ignored
        }
        try { stmt.close(); } catch (Exception e) { // ignored
        }
        try { db3.close(); } catch (Exception e) { // ignored
        }
    }

    /*

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
            try { rs.close(); } catch (Exception e) { // ignored  }
            try { ps.close(); } catch (Exception e) { // ignored  }
            try { db.close(); } catch (Exception e) { // ignored  }
        }
    }

    */

    static public ArrayList<InfoSystem> all() {
        Connection db  = null;
        Statement stmt = null;
        ResultSet rs   = null;

        ArrayList<InfoSystem> enrollmentList = new ArrayList<InfoSystem>();

        try {
            // Step 1: Get a database connection
            db = DbConnection.getConnection();

            // Step 2: Create the statement object
            String sql = "SELECT s.fname, s.lname, s.e_id, sec.section_id, c.course_name, e.alive_status, e.grade\n" +
                    "FROM Student s, Enrollment e, Section sec, Course c\n" +
                    "WHERE s.e_id = e.e_id \n" +
                    "\tAND e.section_id = sec.section_id"; // Always a good idea to name parameters, instead of using SELECT *
            stmt = db.createStatement();

            // Step 3: Enter parameters (if any)
            // ... no parameters this time

            // Step 4: Execute query
            rs = stmt.executeQuery(sql);

            // Step 5: Process results (for a select)
            while(rs.next())  {

                InfoSystem s = new InfoSystem(
                        rs.getString("fname"),
                        rs.getString("lname"),
                        rs.getString("e_id"),
                        rs.getInt("section_id"),
                        rs.getString("course_name"),
                        rs.getString("alive_status"),
                        rs.getString("grade")
                );

                enrollmentList.add(s);
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
        return enrollmentList;
    }

    /** UTILITY METHODS
     * It's always useful to be able to print out our models.
     * Overriding toString() is an easy way to make that happen.
     */

    public String toString() {
        // '\t' is the <tab> character
        // In String.format, 3 character minimum, two after the decimal place
        // See docs for String.format here: http://docs.oracle.com/javase/8/docs/api/java/util/Formatter.html#syntax
        return "" + e_id + "\t" + lname + "\t" + fname + "\t" + section_id + "\t" +alive_status + "\t" + grade;
    }

    /** GETTERS & SETTERS
     *
     * Notice there is no setter for id. This is on purpose, as we want only
     * this Model class to be able to change it.
     *
     * Otherwise, we have getters/setters for each attribute.
     **/

//    public long getId() {
//        return id;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public void setName(String theName) {
//        name = theName;
//    }
//
//    public double getPrice() {
//        return price;
//    }
//
//    public void setPrice(double thePrice) {
//        price = thePrice;
//    }

    public void setEId(String newEId) {
        e_id = newEId;
    }

    public void setSecId(int newSecId) {
        section_id = newSecId;
    }

    public void setGrade(String newGrade) {
        grade = newGrade;
    }

    public void setStatus(String newStatus) {
        grade = newStatus;
    }

}
