/*
 * You shouldn't have to change this file.
 * If you don't know where to copy your files to, run the getDefaultPath()
 * method for a hint.
 */
package ISF;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.util.List;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 *
 * @author Tom Gregory
 */
public class FileAdapter {
    
    Path theFilePath = null;
    
    final static Charset ENCODING = StandardCharsets.UTF_8;
    
    public FileAdapter(String fileName) {
            theFilePath = Paths.get(fileName);
    }
    
    public List<String> readFileLines() throws IOException {
        // A great tutorial for reading files is at
        // http://www.javapractices.com/topic/TopicAction.do?Id=42
        // Another useful discussion for reading files may be found here:
        // http://stackoverflow.com/questions/4716503/reading-a-plain-text-file-in-java
    
        return Files.readAllLines(theFilePath, ENCODING);
    }
    
    public static String getDefaultPath() {
        return Paths.get("").toAbsolutePath().toString();
    }
}
