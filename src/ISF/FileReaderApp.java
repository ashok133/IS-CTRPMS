/*
 * An example controller class, showing how to use the provided FileAdapter.
 * You should copy and paste this code as needed.
 */
package ISF;

import java.util.List;
import java.io.IOException;


/**
 *
 * @author tagregory
 */
public class FileReaderApp {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        System.out.println("This code looks here for files by default:");
        System.out.println(FileAdapter.getDefaultPath());
        System.out.println();
        
        // Simulating getting a file name from the user:
        // Feel free to require files be read in from a particular directory.
        String fileName = "students1-cohort1.csv";
        
        FileAdapter theFile = new FileAdapter(fileName);
        
        List<String> lines = null;
        try {
            lines = theFile.readFileLines();
        } catch (IOException ioe) {
            System.err.println("ERROR reading file");
            System.err.println(ioe.getMessage());
        }
        
        // Now print the file to demonstrate we have it
        System.out.println("FILE CONTENTS:");
        for(String theLine : lines) {
            System.out.println(theLine);
        }
    }

    public List<String> readContents(String file) {

        System.out.println("This code looks here for files by default:");
        System.out.println(FileAdapter.getDefaultPath());
        System.out.println();

        // Simulating getting a file name from the user:
        // Feel free to require files be read in from a particular directory.
        String fileName = file;

        FileAdapter theFile = new FileAdapter(fileName);

        List<String> lines = null;
        try {
            lines = theFile.readFileLines();
        } catch (IOException ioe) {
            System.err.println("ERROR reading file");
            System.err.println(ioe.getMessage());
        }

        // Now print the file to demonstrate we have it
        System.out.println("FILE CONTENTS:");
        for(String theLine : lines) {
            System.out.println(theLine);
            System.out.println(theLine.getClass().getName());
        }

        return lines;

    }
}
