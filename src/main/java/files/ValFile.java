/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package files;

import java.nio.file.Path;

/**
 *
 * @author mcaikovs
 */
public class ValFile extends MySourceFile{

    public ValFile(String filePath) {
        super(filePath);
    }

    public ValFile(Path filePath) {
        super(filePath);
    }

    

    @Override
    boolean isGeneticDataFile() {
        return true;
    }
    
}
