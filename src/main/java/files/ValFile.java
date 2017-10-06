/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package files;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author mcaikovs
 */
public class ValFile extends MySourceFile {

    public ValFile(String filePath) {
        super(filePath);
    }

    public ValFile(Path filePath) {
        super(filePath);
    }

    void saveStackTrace(Exception ex) throws IOException {
        fu.saveStackTrace(filePath, ex);
    }
 
    @Override
    boolean isGeneticDataFile() {
        return true;
    }

}
