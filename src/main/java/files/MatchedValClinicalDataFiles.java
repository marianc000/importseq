/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package files;

import java.io.IOException;

/**
 *
 * @author mcaikovs
 */
public class MatchedValClinicalDataFiles {

    ValFile valFile;
    ClinicalDataFile clinicalDataFile;

    public ValFile getValFile() {
        return valFile;
    }

    public ClinicalDataFile getClinicalDataFile() {
        return clinicalDataFile;
    }

    public MatchedValClinicalDataFiles(ClinicalDataFile clinicalDataFile, ValFile valFile) {
        this.valFile = valFile;
        this.clinicalDataFile = clinicalDataFile;
    }

    void moveToImported() throws IOException {
        valFile.moveToImported();
        clinicalDataFile.moveToImported();
    }

    void moveToFailed() throws IOException {
        valFile.moveToFailed();
        clinicalDataFile.moveToFailed();
    }
}
