package ISF;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

public class InfoSystemController {

    private static ArrayList<InfoSystem> menu;
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws SQLException {
        // TODO code application logic here

        menu = InfoSystem.all(); // Get all sushi from the database, as Sushi objects

        while (true) { // <-- Be careful to NOT infinite loop! See the code below
            System.out.println("*** WELCOME ***");
            System.out.println("Please choose from the following:");
            System.out.println("(1) Print Enrollments");
            System.out.println("(2) Populate Students"); // calls create item method of controller
            System.out.println("(3) Populate Courses");
            System.out.println("(4) Populate Sections");

            // include delete as well

            System.out.println("(0) QUIT\n");  // '\n' means New Line

            System.out.println("Your choice? ");

            Scanner sc = new Scanner(System.in); // Let's get input from the user

            int i = -1;
            try {
                i = sc.nextInt(); // The input needs to be an integer
            }  catch (Exception e) {} // What if someone doesn't type an int? Then i remains -1

            switch (i) {
                case 0:
                    return;
                case 1:
                    printEnrollments();
                    break;
                case 2:
                    InfoSystem is = new InfoSystem();
                    Scanner sc2 = new Scanner(System.in);
                    System.out.println("Enter the file name: ");
                    String filename = sc2.next();
                    is.populateStudents(filename);
//                    createItem();
                    break;
                case 3:
                    InfoSystem is2 = new InfoSystem();
                    Scanner sc3 = new Scanner(System.in);
                    System.out.println("Enter the file name: ");
                    String filename2 = sc3.next();
                    is2.populateCourses(filename2);
//                    printEnrollments();
//                    editItem();
                    break;
                case 4:
                    InfoSystem is3 = new InfoSystem();
                    is3.populateSections();
//                    printEnrollments();
//                    editItem();
                    break;
                default:
                    System.out.println("** ERROR: Unknown entry. Please try again.");
            }
        }
    }

    public static void printEnrollments () {
        System.out.println();
        System.out.println("** MENU **");
        for (InfoSystem s : menu) {
            System.out.println(s);  // This works, because we created a toString method in Sushi
        }
        System.out.println();
    }


    /*

    public static void createItem() {
        Scanner scanner = new Scanner(System.in);
        InfoSystem theNewOne = new InfoSystem();

        System.out.println("** CREATE NEW ENROLLMENT **");
        System.out.print(" Employee ID: ");
        String newEId = scanner.nextLine();

        theNewOne.setEId(newEId);

        System.out.print("  Section ID: ");
        int newSecId = scanner.nextInt();
        theNewOne.setSecId(newSecId);

        // grade and status will be null and Y by default

        theNewOne.create();
        System.out.println(".. CREATED ..");

        // Need to recreate menu after adding something!
        menu = InfoSystem.all();
    }

*/

    public static void editItem() {
        System.out.println("** EDIT SUSHI **");
        System.out.print("  Enter id of sushi to edit: ");

        Scanner scanner = new Scanner(System.in);
        long id = scanner.nextLong();

        Sushi whichSushi;

        try {
            whichSushi = new Sushi(id); // This should get it from the database. See the model code!

            System.out.println(whichSushi);
        } catch (Exception e) {
            System.out.println (".. ERROR: Id not found ..");
            return;
        }

        System.out.print("  New Price: ");
        double myNewPrice = scanner.nextDouble();
        whichSushi.setPrice(myNewPrice);

        whichSushi.save();

        System.out.println(".. UPDATED ..");

        // Need to recreate menu after editing something!
        menu = InfoSystem.all();
    }
}
