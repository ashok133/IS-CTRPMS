package ISF;

import org.apache.commons.io.FileUtils;

import java.io.*;
import java.util.*;

public class ReportGen {

    public ArrayList<InfoSystem> data;

    public void writeReport() throws IOException {

        String e_id = "";
        String lname = "";
        String fname = "";
        String status = "";
        String grade = "";
        int section = -1;

        FileReaderApp fr = new FileReaderApp();

        List<String> lines = fr.readContents("template.html");

        String htmlString = String.join("", lines);

        InfoSystem is = new InfoSystem();

        data = InfoSystem.all();

        String title = "ISF - Enrollment";
        htmlString = htmlString.replace("$title",title);

        String header = "Enrollment Details";
        htmlString = htmlString.replace("$header",header);

        String tableHeaders = "<tr>\n" +
                "\t\t\t<th>EMPLOYEE ID</th>\n" +
                "\t\t\t<th>LAST NAME</th>\n" +
                "\t\t\t<th>FIRST NAME</th>\n" +
                "\t\t\t<th>SECTION</th>\n" +
                "\t\t\t<th>STATUS</th>\n" +
                "\t\t\t<th>GRADE</th>\n" +
                "\t\t</tr>";
        htmlString = htmlString.replace("$tableheader",tableHeaders);

        String tableRows = "";

        for (InfoSystem s : data) {
            e_id = s.e_id;
            lname = s.lname;
            fname = s.fname;
            status = s.alive_status;
            grade = s.grade;
//            section = s.section_id;

            String tempRow = "<tr>\n" +
                    "\t\t\t<td>"+e_id+"</td>\n" +
                    "\t\t\t<td>"+lname+"</td>\n" +
                    "\t\t\t<td>"+fname+"</td>\n" +
                    "\t\t\t<td>"+Integer.toString(section)+"</td>\n" +
                    "\t\t\t<td>"+status+"</td>\n" +
                    "\t\t\t<td>"+grade+"</td>\n" +
                    "\t\t</tr>";

            tableRows = tableRows+tempRow;
        }

        htmlString = htmlString.replace("$tablerows",tableRows);

        File newHtmlFile = new File("latestReports.html");
        FileUtils.writeStringToFile(newHtmlFile, htmlString);
    }

}
