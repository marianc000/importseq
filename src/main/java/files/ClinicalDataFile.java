/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package files;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author mcaikovs
 */
public class ClinicalDataFile extends MySourceFile {

    public ClinicalDataFile(String filePath) {
        super(filePath);
    }

    public ClinicalDataFile(Path filePath) {
        super(filePath);
    }

    public String getStudyName(String filePath) {

        return getStudyNameFromFileName(Paths.get(filePath).getFileName().toString());

    }

    public String getStudyName() {

        return getStudyNameFromFileName( getFilePath() .getFileName().toString());

    }
    public static Pattern p = Pattern.compile("[^.]+\\.([^.]+).+"); // c.3405-2A>T, c.396_398dup
//H1703061-1A.chuv_val2.xlsx

    String getStudyNameFromFileName(String fileName) {
        Matcher m = p.matcher(fileName);
        if (m.matches()) {
            return m.group(1);

        }
        return null;
    }
}
