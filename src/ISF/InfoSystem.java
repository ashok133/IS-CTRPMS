package ISF;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

//import static ISF.InfoSystemController.encrypt;

public class InfoSystem extends Model {

    String e_id, alive_status, grade, course_name, fname, lname;
    int section_id, course_id;

    int count_check_counter = 0;
    int no_divs, no_rows;


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
    public InfoSystem(String theEId) {
        Connection db = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            // Step 1: Get a database connection
            db = DbConnection.getConnection();

            // Step 2: Create the statement object
            String sql = "SELECT s.fname, s.lname, s.e_id, sec.section_id, c.course_name, e.alive_status, e.grade\n" +
                    "FROM Student s, Enrollment e, Section sec, Course c\n" +
                    "WHERE s.e_id = e.e_id \n" +
                    "\tAND e.section_id = sec.section_id \n" ;
                    //"\ts.e_id = ?"; // Always a good idea to name parameters, instead of using SELECT *
            ps = db.prepareStatement(sql);

            // Step 3: Enter parameters (if any)
            ps.setString(1, theEId); // Set the first parameter (1) to the value of theId

            // Step 4: Execute query
            rs = ps.executeQuery();

            // Step 5: Process results (for a select)
            while (rs.next()) {
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

            throw new IllegalArgumentException();

        } catch (SQLException sqe) {
            System.err.println("ERROR: SQL Exception.");
            System.err.println(sqe.getMessage());
        } finally {
            // Lastly, it's good practice to close all of your resources when finished.
            try {
                rs.close();
            } catch (Exception e) { /* ignored */ }
            try {
                ps.close();
            } catch (Exception e) { /* ignored */ }
            try {
                db.close();
            } catch (Exception e) { /* ignored */ }
        }
    }

    public void populateStudents(String fileName, int cohort) throws SQLException {
        Connection db = null;
        PreparedStatement ps = null;
        Statement stmt = null;
        ResultSet rs = null;


        String lname = "";
        String fname = "";
        String email = "";
        String e_id = "";
        List<String> listOfStudents = null;

        FileReaderApp reader = new FileReaderApp();
        listOfStudents = reader.readContents(fileName);
        System.out.println("Reached populateStudents method...");

        int subKey = 1;

        for (String theLine : listOfStudents) {
            String[] arrOfStr = theLine.split(",", 7);
            lname = arrOfStr[0];
            fname = arrOfStr[1];
            email = arrOfStr[2];
            e_id = arrOfStr[3];

            db = DbConnection.getConnection();

            String sql = "INSERT INTO Student values (?,?,?,?,?)";

            ps = db.prepareStatement(sql);

            String duplicateCheckerSql = "SELECT TOP 1 e_id \n" +
                    "FROM Student WHERE e_id = '"+e_id+"'";

            stmt = db.createStatement();

            rs = stmt.executeQuery(duplicateCheckerSql);

            int dupeFlag = 0;

            while(rs.next()) {
                System.out.println("FOUND A DUPLICATE e_id. Replacing it with a new one.");
                dupeFlag = 1;
                subKey++;
            }

            ps.setString(1, lname);
            ps.setString(2, fname);
            ps.setString(3, email);

            if (dupeFlag == 1)
                ps.setString(4,"M0-9999990"+Integer.toString(subKey));
            else
                ps.setString(4, e_id);

            ps.setInt(5,cohort);

            ps.executeUpdate();

            System.out.println("Successfully inserted record for: " + lname + " " + fname + "(" + e_id + ")");

            try {
                ps.close();
            } catch (Exception e) { // ignored
            }
            try {
                db.close();
            } catch (Exception e) { // ignored
            }
        }



//
//        Connection db = null;
//        PreparedStatement ps = null;
//
//
//        String lname = "";
//        String fname = "";
//        String email = "";
//        String e_id = "";
//        List<String> listOfStudents = null;
//
//        FileReaderApp reader = new FileReaderApp();
//        listOfStudents = reader.readContents(fileName);
//        System.out.println("Reached populateStudents method...");
//
//        for (String theLine : listOfStudents) {
//            String[] arrOfStr = theLine.split(",", 7);
//            lname = arrOfStr[0].replaceAll("^\"|\"$", "");
//            fname = arrOfStr[1].replaceAll("^\"|\"$", "");
//            email = arrOfStr[2].replaceAll("^\"|\"$", "");
//            e_id = arrOfStr[3].replaceAll("^\"|\"$", "");
//
//
//
//            String sql = "INSERT INTO Student values (?,?,?,?,?)";
//
//            db = DbConnection.getConnection();
//
//            ps = db.prepareStatement(sql);
//
//            ps.setString(1, lname);
//            ps.setString(2, fname);
//            ps.setString(3, email);
//            ps.setString(4, e_id);
//            if (fileName.equals("students1.csv")) {
//                ps.setInt(5,1);
//            }
//            else {
//                ps.setInt(5,2);
//            }
//
//            ps.executeUpdate();
//
//            System.out.println("Successfully inserted record for: " + lname + " " + fname + "(" + e_id + ")");
//
//
//            try {
//                ps.close();
//            } catch (Exception e) { // ignored
//            }
//            try {
//                db.close();
//            } catch (Exception e) { // ignored
//            }
//        }




    }

    public void populateCourses(String fileName) throws SQLException {
        Connection db2 = null;
        PreparedStatement ps2 = null;
        Scanner sc = new Scanner(System.in);

        String cname = "";

        List<String> listOfCourses = null;

        FileReaderApp reader = new FileReaderApp();
        listOfCourses = reader.readContents(fileName);

        System.out.println("Enter cohort number for this list:");
        int cohort_num = sc.nextInt();

        for (String theLine : listOfCourses) {

            cname = theLine;

            long c_id = Model.getNextPrimaryKey("Course");

            db2 = DbConnection.getConnection();

            String sql = "INSERT INTO Course values (?,?,?)";

            ps2 = db2.prepareStatement(sql);

            ps2.setLong(1, c_id);
            ps2.setString(2, cname);
            ps2.setInt(3, cohort_num);

            ps2.executeUpdate();

            System.out.println("Successfully inserted record for: " + c_id + ": " + cname);


            try {
                ps2.close();
            } catch (Exception e) { // ignored
            }
            try {
                db2.close();
            } catch (Exception e) { // ignored
            }
        }
    }

    // recursively divide the total number of students and return the number of sections
    public int countCheck(int no_students, int limit) {
        System.out.println("No studs in func(): "+no_students);
        if (no_students > limit) {
            countCheck((int) Math.ceil(no_students / 2),limit);
            count_check_counter++;
        } else {
//            System.out.println("No sections: "+ (int) Math.pow(2, count_check_counter));
            return (int) Math.pow(2, count_check_counter);
        }
        return (int) Math.pow(2, count_check_counter);
    }

    public void populateSections() throws SQLException {
        // initialise the connection and sql query objects
        Connection db3 = null;
        Connection db4 = null;
        Statement stmt = null;
        PreparedStatement ps = null;
        ResultSet rs1 = null;
        ResultSet rs2 = null;

        // initialise an array list to store our course_ids
        ArrayList<Integer> course_ids = new ArrayList<>();

        // write query string for extracting course_ids and get a connection instance with our server
        String sql1 = "SELECT course_id from Course";
        db3 = DbConnection.getConnection();

        // prepare the capsule for our sql query
        stmt = db3.createStatement();

        // pack the query and send it to sql server for executing it
        rs1 = stmt.executeQuery(sql1);

        // unpack the result set, extract course IDs and push them into - course_ids ArrayList
        while (rs1.next()) {
            course_ids.add(rs1.getInt("course_id"));
        }

        //for each course id in course_ids ArrayList, print it
        for (Integer is_object : course_ids) {
            System.out.println(is_object.toString());
//            System.out.println(""+is_object);
        }

        // get the number of students
        no_rows = (int) Model.getNextPrimaryKey("Student") - 1;

        System.out.println("ROWS:" + no_rows);

        // update
        System.out.println("Enter the maximum number of students in a section:");
        Scanner sc = new Scanner(System.in);
        int limit = sc.nextInt();

        // get the number of divisions in the section table
        no_divs = countCheck(no_rows, limit);

        System.out.println("SECTIONS:" + no_divs);
        db4 = DbConnection.getConnection();


        // fill in the Section table with section_ids(1,2,3,4) and for each section_id, put the course_ids (1,2,3,4,5,6,7)
        for (int i=1; i<=no_divs;i++ ) {
            for (int j=1; j<=course_ids.size(); j++) {
                String sql2 = "INSERT INTO Section values (?,?)";
                ps = db4.prepareStatement(sql2);
                ps.setInt(1, i);
                ps.setInt(2, j);
                ps.executeUpdate();
            }
        }

        try {
            rs1.close();
        } catch (Exception e) { // ignored
        }
        try {
            rs2.close();
        } catch (Exception e) { // ignored
        }
        try {
            stmt.close();
        } catch (Exception e) { // ignored
        }
        try {
            ps.close();
        } catch (Exception e) { // ignored
        }
        try {
            db3.close();
        } catch (Exception e) { // ignored
        }
        try {
            db4.close();
        } catch (Exception e) { // ignored
        }
    }

    public void fillEnrollment() {
        Connection db  = null;
        Statement stmt = null;
        ResultSet rs   = null;
        PreparedStatement ps = null;
        ArrayList<String> id_list = new ArrayList<>();
//        ArrayList<InfoSystem> stud_ids = new ArrayList<>();

        try {
            // Step 1: Get a database connection
            db = DbConnection.getConnection();

            // Step 2: Create the statement object
            String sql = "SELECT e_id FROM Student";

            stmt = db.createStatement();

            // Step 3: Enter parameters (if any)
            // ... no parameters this time

            // Step 4: Execute query
            rs = stmt.executeQuery(sql);

            // Step 5: Process results (for a select)
            while(rs.next())  {
                id_list.add(rs.getString("e_id"));
            }

            no_rows = id_list.size();
            System.out.println("NUMBER OF STUDENTS:"+no_rows);
//            no_rows = (int) Model.getNextPrimaryKey("Student") - 1;

            System.out.println("NEW NUMBER OF ROWS:" + no_rows);

            System.out.println("Enter the maximum number of students in a section:");
            Scanner sc = new Scanner(System.in);
            int limit = sc.nextInt();
            no_divs = countCheck(no_rows, limit);

            int id_row_counter = 0;

            for (String s: id_list) {
              System.out.println("!!!!!!!" + s);
              System.out.println(""+no_divs+":"+no_rows);
            }

            for (int i=1; i<=no_divs; i++) {
                System.out.println("nosec:"+i);
                for (int j=1;j<=Math.ceil(no_rows/no_divs); j++) {
                    System.out.println("norow:"+j);

                    db = DbConnection.getConnection();
                    String sql2 = "INSERT INTO Enrollment(e_id, section_id, course_id, alive_status, grade) values (?,?,?,?,?)";

                    ps = db.prepareStatement(sql2);

                    System.out.println("ID ROW COUNTER:"+id_row_counter);
                    System.out.println(id_list.get(id_row_counter));

                    ps.setString(1, id_list.get(id_row_counter));
                    ps.setInt(2, i);
                    ps.setInt(3,1);
                    ps.setString(4, "Y");
                    ps.setString(5, "A");
                    ps.executeUpdate();

                    if (id_row_counter < no_rows)
                        id_row_counter++ ;
                    else
                        break;
                }
            }

//            for (int i=1; i<=no_divs; i++) {
//                System.out.println("nosec:"+i);
//                for (int j=0;j<Math.ceil(no_rows/no_divs); j++) {
//                    System.out.println("norow:"+j);
//
//                    db = DbConnection.getConnection();
//                    String sql2 = "INSERT INTO Enrollment(e_id, section_id, course_id, alive_status, grade) values (?,?,?,?,?)";
//
//                    ps = db.prepareStatement(sql2);
//
//                    System.out.println("ID ROW COUNTER:"+id_row_counter);
//                    System.out.println(id_list.get(id_row_counter));
//
//                    ps.setString(1, id_list.get(id_row_counter));
//                    ps.setInt(2, i);
//                    ps.setInt(3,1);
//                    ps.setString(4, "Y");
//                    ps.setString(5, "A");
//                    ps.executeUpdate();
//
//                    if (id_row_counter < no_rows-1)
//                        id_row_counter++ ;
//                    else
//                        break;
//                }
//            }

        } catch (SQLException sqe){
            System.err.println("ERROR: SQL Exception.");
            System.err.println(sqe.getMessage());
        } finally {
            // Lastly, it's good practice to close all of your resources when finished.
            try { ps.close(); } catch (Exception e) { /* ignored */ }
            try { rs.close(); } catch (Exception e) { /* ignored */ }
            try { stmt.close(); } catch (Exception e) { /* ignored */ }
            try { db.close(); } catch (Exception e) { /* ignored */ }
        }

    }

    static public void flushData() {
        Connection db  = null;
        Statement stmt = null;
        ResultSet rs   = null;
        PreparedStatement ps = null;

        try {
            // Step 1: Get a database connection
            db = DbConnection.getConnection();

            // Step 2: Create the statement object
            String sql = "DROP TABLE Enrollment\n" +
                    "DROP TABLE Section\n" +
                    "DROP TABLE Course\n" +
                    "DROP TABLE Student";

            ps = db.prepareStatement(sql);

            // Step 3: Enter parameters (if any)
            // ... no parameters this time

            // Step 4: Execute query
            ps.executeUpdate();
            createTables();

            System.out.println("Done! Do you want to populate the tables again? \n\t(1) Y\n\t(2) N");
            Scanner sc = new Scanner(System.in);
            int response = sc.nextInt();
            if (response == 1) {
                System.out.println("Enter Data for?\n\t(1) Students\n\t(2) Courses");
                int filetype = sc.nextInt();
                if (filetype == 1) {
                    while(true) {
                        InfoSystem is2 = new InfoSystem();
                        Scanner sc3 = new Scanner(System.in);
                        System.out.println("Enter the file name for students: ");
                        String filename = sc3.next();
                        System.out.println("Enter the cohort number for these students: ");
                        int cohort = sc3.nextInt();
                        is2.populateStudents(filename,cohort);
                        System.out.println("Do we have more students? (Y/N)");
                        String moreStudentsResponse = sc.next();
                        if (moreStudentsResponse == "N")
                            break;
                    }
                }
                else {
                    while (true) {
                        InfoSystem is2 = new InfoSystem();
                        Scanner sc3 = new Scanner(System.in);
                        System.out.println("Enter the file name for courses: ");
                        String filename2 = sc3.next();
                        is2.populateCourses(filename2);
                        System.out.println("Do we have more courses? (Y/N)");
                        String moreCoursesResponse = sc.next();
                        if (moreCoursesResponse == "N")
                            break;
                    }
                }
            }
            else {
                System.out.println("You can return to the menu any time! ");
                return;
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

    }

    static public void createTables() {
        Connection db  = null;
        Statement stmt = null;
        ResultSet rs   = null;
        PreparedStatement ps = null;

        try {
            // Step 1: Get a database connection
            db = DbConnection.getConnection();

            /* Step 2: Create the statement object */
            String sql = "CREATE TABLE Student (\n" +
                    "lname VARCHAR(30) NOT NULL,\n" +
                    "fname VARCHAR(30) NOT NULL,\n" +
                    "email VARCHAR(40) NOT NULL,\n" +
                    "e_id VARCHAR(30) PRIMARY KEY NOT NULL,\n" +
                    "cohort int NOT NULL\n" +
                    ");\n" +
                    "CREATE TABLE Course (\n" +
                    "course_id int PRIMARY KEY,\n" +
                    "course_name VARCHAR(200),\n" +
                    "cohort_no int\n" +
                    ");"+
                    "\n" +
                    "CREATE TABLE Section (\n" +
                    "--section_table_id int IDENTITY(1,1) PRIMARY KEY,\n" +
                    "section_id int,\n" +
                    "course_id int,\n" +
                    "constraint sec_pk PRIMARY KEY (section_id,course_id),\n" +
                    "constraint sec_fk1 FOREIGN KEY (course_id) REFERENCES Course (course_id)\n" +
                    ");\n" +
                    "\n" +
                    "CREATE TABLE Enrollment (\n" +
                    "e_id VARCHAR(30),\n" +
                    "section_id int,\n" +
                    "course_id int,\n" +
                    "alive_status VARCHAR(2),\n" +
                    "grade VARCHAR(2), \n" +
                    "constraint enrollment_pk PRIMARY KEY (e_id, section_id),\n" +
                    "constraint enrollment_fk1 FOREIGN KEY (section_id, course_id) REFERENCES Section (section_id, course_id),\n" +
                    "constraint enrollment_fk2 FOREIGN KEY (e_id) REFERENCES Student (e_id)\n" +
                    ");";

            ps = db.prepareStatement(sql);

            // Step 3: Enter parameters (if any)
            // ... no parameters this time

            // Step 4: Execute query
            ps.executeUpdate();

            System.out.println("Successfully created all tables. Do you want to populate the tables? \n\t(1) Y\n\t(2) N");
            Scanner sc = new Scanner(System.in);
            int response = sc.nextInt();
            if (response == 1 ) {
                fileAdder:
                System.out.println("Enter Data for?\n\t1.Students\n\t2.Courses");
                int fileType = sc.nextInt();
                if (fileType == 1) {
                    while(true) {
                        InfoSystem is2 = new InfoSystem();
                        Scanner sc3 = new Scanner(System.in);
                        System.out.println("Enter the students file name: ");
                        String filename = sc3.next();
                        System.out.println("Enter the cohort number for these students: ");
                        int cohort = sc3.nextInt();

                        is2.populateStudents(filename,cohort);

                        System.out.println("Do we have more students? (Y/N)");
                        String moreStudentsResponse = sc.next();
                        if (moreStudentsResponse.equals("N"))
                            break;
                    }
                }
                else if (fileType == 2){
                    while (true) {
                        InfoSystem is2 = new InfoSystem();
                        Scanner sc3 = new Scanner(System.in);
                        System.out.println("Enter the course file name: ");
                        String filename2 = sc3.next();
                        is2.populateCourses(filename2);
                        System.out.println("Do we have more courses? (Y/N)");
                        String moreCoursesResponse = sc.next();
                        if (moreCoursesResponse.equals("N"))
                            break;
                    }
                }
            }
            else {
                System.out.println("You can return to the menu any time! ");
                return;
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
    }

    public static void getCoursesByCohort() {
        Connection db  = null;
        Statement stmt = null;
        ResultSet rs   = null;
        HashMap<Integer,String> courseList= new HashMap<>();
        HashMap<String,String[]> courseForStudents = new HashMap<>();

        try {

            // Step 1: Get a database connection
            db = DbConnection.getConnection();

            // Step 2: Create the statement object
            String sql = "SELECT cohort_no, course_name FROM Course"; // Always a good idea to name parameters, instead of using SELECT *

            stmt = db.createStatement();

            // Step 3: Enter parameters (if any)
            // ... no parameters this time

            // Step 4: Execute query
            rs = stmt.executeQuery(sql);

            // Step 5: Process results (for a select)
            while(rs.next())  {
                    int c_num = rs.getInt(1);
                    String course = rs.getString(2);
                    if (courseList.containsKey(c_num)) {
                        // map the courses to cohort
                        courseList.put(c_num,courseList.get(c_num)+course+"\n");
                    }
                    else {
                        courseList.put(c_num,course);
                    }
            }

//            System.out.println(courseList);

            String sql2 = "SELECT s.cohort, s.e_id, s.lname, s.fname\n" +
                    "FROM Student s\n";
            stmt = db.createStatement();
            rs = stmt.executeQuery(sql2);

            while(rs.next()) {
                int cohort_num = rs.getInt(1);
                String e_id = rs.getString(2);
                String lname = rs.getString(3);
                String fname = rs.getString(4);
                courseForStudents.put(e_id,new String[]{lname,fname,Integer.toString(cohort_num),courseList.get(cohort_num)});
            }
            for (Map.Entry<String, String[]> entry : courseForStudents.entrySet()) {
                System.out.println(entry.getKey()+" : "+entry.getValue());
            }

            generateHTMLForStudentsWithCourses(courseForStudents);

        } catch (SQLException sqe){
            System.err.println("ERROR: SQL Exception.");
            System.err.println(sqe.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // Lastly, it's good practice to close all of your resources when finished.
            try { rs.close(); } catch (Exception e) { /* ignored */ }
            try { stmt.close(); } catch (Exception e) { /* ignored */ }
            try { db.close(); } catch (Exception e) { /* ignored */ }
        }

    }

    public static void getStatusAndGrade(boolean withFeedback) {
        Connection db  = null;
        Statement stmt = null;
        ResultSet rs   = null;
        ArrayList<String[]> enrollmentData = new ArrayList<>();

        try {

            // Step 1: Get a database connection
            db = DbConnection.getConnection();

            // Step 2: Create the statement object
            String sql = "SELECT * FROM ENROLLMENT"; // Always a good idea to name parameters, instead of using SELECT *

            stmt = db.createStatement();

            // Step 3: Enter parameters (if any)
            // ... no parameters this time

            // Step 4: Execute query
            rs = stmt.executeQuery(sql);

            // Step 5: Process results (for a select)
            while(rs.next())  {
                String e_id = rs.getString(1);
                int section_id = rs.getInt(2);
                int course_id = rs.getInt(3);
                String alive_status = rs.getString(4);
                String grade = rs.getString(5);
                enrollmentData.add(new String[]{e_id,Integer.toString(section_id),Integer.toString(course_id),alive_status,grade});
            }

            generateHTMLForStatusAndGrade(enrollmentData, withFeedback);

        } catch (SQLException sqe){
            System.err.println("ERROR: SQL Exception.");
            System.err.println(sqe.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // Lastly, it's good practice to close all of your resources when finished.
            try { rs.close(); } catch (Exception e) { /* ignored */ }
            try { stmt.close(); } catch (Exception e) { /* ignored */ }
            try { db.close(); } catch (Exception e) { /* ignored */ }
        }



    }

    public static void getNewCourses() {
        Connection db  = null;
        Statement stmt = null;
        ResultSet rs   = null;
        ArrayList<String[]> courseData = new ArrayList<>();

        try {

            // Step 1: Get a database connection
            db = DbConnection.getConnection();

            // Step 2: Create the statement object
            String sql = "SELECT * FROM Course"; // Always a good idea to name parameters, instead of using SELECT *

            stmt = db.createStatement();

            // Step 3: Enter parameters (if any)
            // ... no parameters this time

            // Step 4: Execute query
            rs = stmt.executeQuery(sql);

            // Step 5: Process results (for a select)
            while(rs.next())  {
                int course_id = rs.getInt(1);
                String course_name = rs.getString(2);
                int cohort_num = rs.getInt(3);
                courseData.add(new String[]{Integer.toString(course_id),course_name,Integer.toString(cohort_num)});
            }

            generateHTMLForNewCourses(courseData);
//            generateHTMLForStatusAndGrade(enrollmentData, withFeedback);

        } catch (SQLException sqe){
            System.err.println("ERROR: SQL Exception.");
            System.err.println(sqe.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // Lastly, it's good practice to close all of your resources when finished.
            try { rs.close(); } catch (Exception e) { /* ignored */ }
            try { stmt.close(); } catch (Exception e) { /* ignored */ }
            try { db.close(); } catch (Exception e) { /* ignored */ }
        }

    }

    private static void generateHTMLForStudentsWithCourses(HashMap<String, String[]> courseForStudents) throws IOException {
        String e_id = "";
        String lname = "";
        String fname = "";
        int cohort_num = -1;
        String courseList = "";

        FileReaderApp fr = new FileReaderApp();

        List<String> lines = fr.readContents("template.html");

        String htmlString = String.join("", lines);

        String title = "ISF - Courses by Students";
        htmlString = htmlString.replace("$title",title);

        String header = "Courses for each Student";
        htmlString = htmlString.replace("$header",header);

        String tableHeaders = "<tr>\n" +
                "\t\t\t<th>EMPLOYEE ID</th>\n" +
                "\t\t\t<th>LAST NAME</th>\n" +
                "\t\t\t<th>FIRST NAME</th>\n" +
                "\t\t\t<th>COHORT NUMBER</th>\n" +
                "\t\t\t<th>COURSES</th>\n" +
                "\t\t</tr>";
        htmlString = htmlString.replace("$tableheader",tableHeaders);

        String tableRows = "";

        for (Map.Entry<String, String[]> entry : courseForStudents.entrySet()) {
            e_id = entry.getKey();
            lname = entry.getValue()[0];
            fname = entry.getValue()[1];
            cohort_num = Integer.parseInt(entry.getValue()[2]);
            courseList = entry.getValue()[3];

            System.out.println(e_id+lname+fname+cohort_num+courseList);

            String tempRow = "<tr>\n" +
                    "\t\t\t<td>"+e_id+"</td>\n" +
                    "\t\t\t<td>"+lname+"</td>\n" +
                    "\t\t\t<td>"+fname+"</td>\n" +
                    "\t\t\t<td>"+cohort_num+"</td>\n" +
                    "\t\t\t<td>"+courseList+"</td>\n" +
                    "\t\t</tr>";

            tableRows = tableRows+tempRow;
        }

        htmlString = htmlString.replace("$tablerows",tableRows);

        System.out.println("FILE SAVED AS: studentsByCourse.html");
        File newHtmlFile = new File("studentsByCourse.html");
        FileUtils.writeStringToFile(newHtmlFile, htmlString);
    }

    private static void generateHTMLForStatusAndGrade(List<String[]> enrollmentData, boolean withFeedback) throws IOException {
        String e_id = "";
        String section_id = "";
        String course_id = "";
        String alive_status = "";
        String grade = "";

        FileReaderApp fr = new FileReaderApp();

        List<String> lines = fr.readContents("template.html");

        String htmlString = String.join("", lines);

        String title = "ISF - Status and Grades";
        htmlString = htmlString.replace("$title",title);

        String header = "How are the students performing?";
        htmlString = htmlString.replace("$header",header);

        if (!withFeedback) {
            String tableHeaders = "<tr>\n" +
                    "\t\t\t<th>EMPLOYEE ID</th>\n" +
                    "\t\t\t<th>SECTION ID</th>\n" +
                    "\t\t\t<th>STATUS</th>\n" +
                    "\t\t\t<th>GRADE</th>\n" +
                    "\t\t</tr>";
            htmlString = htmlString.replace("$tableheader",tableHeaders);
        }
        else {
            String tableHeaders = "<tr>\n" +
                    "\t\t\t<th>EMPLOYEE ID</th>\n" +
                    "\t\t\t<th>SECTION ID</th>\n" +
                    "\t\t\t<th>STATUS</th>\n" +
                    "\t\t\t<th>GRADE</th>\n" +
                    "\t\t\t<th>PEER FEEDBACK</th>\n" +
                    "\t\t</tr>";
            htmlString = htmlString.replace("$tableheader",tableHeaders);
        }

        String tableRows = "";

        for (String[] row: enrollmentData) {
            e_id = row[0];
            section_id = row[1];
            course_id = row[2];
            alive_status = row[3];
            grade = row[4];

            String tempRow;

            String[] feedbackList = new String[]{"A great team player!","He's smart","It'd be great if hes' not late for meetings","He is nice and empathetic.","I think he should speak up and express more","I've not seen a person this focused before","She should put more time in research maybe for a project before arriving at conclusions","His code gives me jitters!"};
            String feedback = feedbackList[new Random().nextInt(feedbackList.length)];
            if (!withFeedback) {
                tempRow = "<tr>\n" +
                        "\t\t\t<td>"+e_id+"</td>\n" +
                        "\t\t\t<td>"+section_id+"</td>\n" +
                        "\t\t\t<td>"+alive_status+"</td>\n" +
                        "\t\t\t<td>"+grade+"</td>\n" +
                        "\t\t</tr>";
            }
            else {

                tempRow = "<tr>\n" +
                        "\t\t\t<td>"+e_id+"</td>\n" +
                        "\t\t\t<td>"+section_id+"</td>\n" +
                        "\t\t\t<td>"+alive_status+"</td>\n" +
                        "\t\t\t<td>"+grade+"</td>\n" +
                        "\t\t\t<td>"+feedback+"</td>\n" +
                        "\t\t</tr>";
            }

            tableRows = tableRows + tempRow;
        }


        htmlString = htmlString.replace("$tablerows",tableRows);

        File newHtmlFile;

        if(!withFeedback) {
            newHtmlFile = new File("status&grades.html");
            System.out.println("FILE SAVED AS: status&grades.html");
        }

        else {
            newHtmlFile = new File("status&gradesWithFeedback.html");
            System.out.println("FILE SAVED AS: status&gradesWithFeedback.html");
        }

//        File newHtmlFile = new File("status&grades.html");
        FileUtils.writeStringToFile(newHtmlFile, htmlString);
    }

    private static void generateHTMLForNewCourses(List<String[]> enrollmentData) throws IOException {
        String course_id = "";
        String course_name = "";
        String cohort_num = "";

        FileReaderApp fr = new FileReaderApp();

        List<String> lines = fr.readContents("template.html");

        String htmlString = String.join("", lines);

        String title = "ISF - New Courses";
        htmlString = htmlString.replace("$title",title);

        String header = "List of new courses";
        htmlString = htmlString.replace("$header",header);

        String tableHeaders = "<tr>\n" +
                    "\t\t\t<th>COURSE ID</th>\n" +
                    "\t\t\t<th>COURSE NAME</th>\n" +
                    "\t\t\t<th>COHORT NUMBER</th>\n" +
                    "\t\t</tr>";
        htmlString = htmlString.replace("$tableheader",tableHeaders);


        String tableRows = "";

        for (String[] row: enrollmentData) {
            course_id = row[0];
            course_name = row[1];
            cohort_num = row[2];

            String tempRow;


            tempRow = "<tr>\n" +
                        "\t\t\t<td>"+course_id+"</td>\n" +
                        "\t\t\t<td>"+course_name+"</td>\n" +
                        "\t\t\t<td>"+cohort_num+"</td>\n" +
                        "\t\t</tr>";


            tableRows = tableRows + tempRow;
        }


        htmlString = htmlString.replace("$tablerows",tableRows);

        File newHtmlFile;

        newHtmlFile = new File("newCourses.html");
        System.out.println("FILE SAVED AS: newCourses");

        FileUtils.writeStringToFile(newHtmlFile, htmlString);
    }

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

    public String getPass(String role) {
        Connection db   = null;
        Statement  stmt = null;
        ResultSet rs    = null;

        String[] arrPass = new String[2];
        try {
            // Step 1: Get a database connection
            db = DbConnection.getConnection();

//            System.out.println("ROLE!!!!!!"+role);
            // Step 2: Create the statement object

            String sql2 = "SELECT * FROM Login";



            String sql = "SELECT password FROM Login\n" +
                    "WHERE id = '" + role +"'"; // Be careful! potential SQL Injection vulnerability here. Not for use in production code.
//            System.out.println("SQL:"+sql);
            stmt = db.createStatement();

            // Step 3: Enter parameters (if any)
            // ... no parameters in this query

            // Step 4: Execute query
            rs = stmt.executeQuery(sql);

            while(rs.next())  {
                return rs.getString(1); // return the first column from the first row of the ResultSet
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
//        throw new IllegalArgumentException();
        return "awesome";
    }

    /** GETTERS & SETTERS
     *
     * Notice there is no setter for id. This is on purpose, as we want only
     * this Model class to be able to change it.
     *
     * Otherwise, we have getters/setters for each attribute.
     **/

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
