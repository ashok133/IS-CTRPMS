package ISF;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;


public class InfoSystemController {


    private static ArrayList<InfoSystem> menu;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        // TODO code application logic here

        menu = InfoSystem.all(); // Get all sushi from the database, as Sushi objects

            System.out.println("\n----------WELCOME TO Kelley CPMS-------------------\n");
            System.out.println("Choose your role:\n\t1. Mgmt/DC\n\t2. Client");


            Scanner sc0=  new Scanner(System.in);
            int roleChoice = sc0.nextInt();
            if(roleChoice == 1) {
                if (checkMgmtLogin()) {

                    while (true) {
                    System.out.println("(A) DB CREATION");
                        System.out.println("\t(1) Flush Data");
                        System.out.println("\t(2) Create Tables");
//                        System.out.println("---------------------------\n");

                    System.out.println("(B) DB MANAGEMENT");
                        System.out.println("\t(3) Populate Students"); // calls create item method of controller
                        System.out.println("\t(4) Populate Courses");
                        System.out.println("\t(5) Populate Sections");
                        System.out.println("\t(6) Fill Enrollment");
//                        System.out.println("---------------------------\n");

                    System.out.println("(C) REPORTS");
                        System.out.println("\t(7) New Courses");
                        System.out.println("\t(8) Status and grades");
                        System.out.println("\t(9) Students and courses by cohort number");

                    System.out.println("(0) QUIT\n");  // '\n' means New Line

                    System.out.println("Your choice? ");

                    Scanner sc = new Scanner(System.in); // Let's get input from the user

                    // initialise with -1 as that will never be one of the options
                    int i = -1;
                    try {
                        i = sc.nextInt(); // The input needs to be an integer
                    }  catch (Exception e) {} // What if someone doesn't type an int? Then i remains -1

                    switch (i) {
                        case 0:
                            // quit if 0
                            return;
                        case 7:
                            InfoSystem is9 = new InfoSystem();
                            is9.getNewCourses();
                            break;
                        case 3:
                            // we read students.csv files and populate the Student table
                            InfoSystem is = new InfoSystem();
                            Scanner sc2 = new Scanner(System.in);
                            System.out.println("Enter the file name for students: ");
                            String filename = sc2.next();
                            System.out.println("Enter the cohort number for these students: ");
                            int cohort = sc2.nextInt();
                            is.populateStudents(filename,cohort);

                            break;
                        case 4:
                            InfoSystem is2 = new InfoSystem();
                            Scanner sc3 = new Scanner(System.in);
                            System.out.println("Enter the file name for courses: ");
                            String filename2 = sc3.next();
                            is2.populateCourses(filename2);
                            break;
                        case 5:

                            InfoSystem is3 = new InfoSystem();
                            is3.populateSections();
                            break;
                        case 6:
                            InfoSystem is4 = new InfoSystem();
                            is4.fillEnrollment();
                            break;
                        case 8:
                            InfoSystem is8 = new InfoSystem();
                            is8.getStatusAndGrade(false);
                            break;
                        case 1:
                            // flush data i.e. drop all the tables
                            InfoSystem is5 = new InfoSystem();
                            is5.flushData();
                            break;
                        case 2:
                            // create all tables as per the schema
                            InfoSystem is6 = new InfoSystem();
                            is6.createTables();
                            break;
                        case 9:
                            InfoSystem is7 = new InfoSystem();
                            is7.getCoursesByCohort();
                            break;
                        default:
                            System.out.println("Sorry, we didn't catch that input. Make sure you enter a valid integer.");
                    }
                }
            }
            else {
                    System.out.println("Wrong Password, Try again");
                    return;
                }
            }


            else {
                if (checkClientLogin()) {
                    while (true) {

                        System.out.println("(1) Submit files");
                        System.out.println("(2) Check employee training status"); // calls create item method of controller

                        System.out.println("(0) QUIT\n");  // '\n' means New Line

                        System.out.println("Your choice? ");

                        Scanner sc1 = new Scanner(System.in); // Let's get input from the user

                        int i = -1;
                        try {
                            i = sc1.nextInt(); // The input needs to be an integer
                        }  catch (Exception e) {} // What if someone doesn't type an int? Then i remains -1

                        switch (i) {
                            case 0:
                                return;
                            case 1:
                                Scanner sc = new Scanner(System.in);
                                System.out.println("Enter the absolute path of source file:\n(psst.. it's /Users/ashok/Desktop/ISF-input/...)");
                                String source = sc.next();
                                System.out.println("Enter the destination file name:");
                                String dest = FileAdapter.getDefaultPath()+"/"+sc.next();
                                Path moveStatus = Files.move(Paths.get(source),Paths.get(dest));
                                System.out.println(moveStatus);

                                if(moveStatus != null)
                                {
                                    System.out.println("File loaded successfully");
                                }
                                else
                                {
                                    System.out.println("Failed to move the file");
                                }

                                break;
                            case 2:
                                InfoSystem is = new InfoSystem();
                                is.getStatusAndGrade(true);
                                break;
                            default:
                                System.out.println("Sorry, we didn't catch that input. Make sure you enter a valid integer.");
                        }
                    }
                }
        }
    }
    private static boolean checkMgmtLogin() {
        System.out.println("Enter the password for 'mgmt:'");
        Scanner sc = new Scanner(System.in);
        String pass = sc.next();
        InfoSystem is = new InfoSystem();

        StringBuffer encryptTemp = caesarEncrypt(pass,7);
//        System.out.println("ENCRYPTED TEXT:"+encryptTemp);

        String rot13encrypted = rot13(encryptTemp.toString());

        return rot13encrypted.equals(rot13(is.getPass("mgmt")));
    }

    private static boolean checkClientLogin(){
        System.out.println("Enter the password for 'client':");
        Scanner sc = new Scanner(System.in);
        String pass = sc.next();

        InfoSystem is = new InfoSystem();

        StringBuffer encryptTemp = caesarEncrypt(pass,7);
//        System.out.println("ENCRYPTED TEXT:"+encryptTemp);

        String rot13encrypted = rot13(encryptTemp.toString());

        return rot13encrypted.equals(rot13(is.getPass("client")));
    }

    public static void printEnrollments () {
        System.out.println();
        System.out.println("** MENU **");
        for (InfoSystem s : menu) {
            System.out.println(s);
        }
        System.out.println();
    }

    public static StringBuffer caesarEncrypt(String text, int s)
    {
        StringBuffer result= new StringBuffer();

        for (int i=0; i<text.length(); i++)
        {
            if (Character.isUpperCase(text.charAt(i)))
            {
                char ch = (char)(
                        ((int)text.charAt(i) + s - 65) % 26 + 65
                );
                result.append(ch);
            }
            else
            {
                char ch = (char)(((int)text.charAt(i) +
                        s - 97) % 26 + 97);
                result.append(ch);
            }
        }
        return result;
    }

    public static String rot13(String input) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if       (c >= 'a' && c <= 'm')
                c += 13;
            else if  (c >= 'A' && c <= 'M')
                c += 13;
            else if  (c >= 'n' && c <= 'z')
                c -= 13;
            else if  (c >= 'N' && c <= 'Z')
                c -= 13;
            sb.append(c);
        }
        return sb.toString();
    }
}
