/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package files;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import main.MyImportValWithClinicalDataFromWatch;

/**
 *
 * @author mcaikovs
 */
public class MatchingGeneticClinicalDataFiles {

    Map<String, ValFile> sampleNameValFileMap = new HashMap<>();
    Map<String, ClinicalDataFile> sampleNameClinicalDataFileMap = new HashMap<>();

    void printRemainingFiles() {
        System.out.println(">printRemainingFiles: remains:");
        System.out.println("sampleNameValFileMap=" + sampleNameValFileMap);
        System.out.println("sampleNameClinicalDataFileMap=" + sampleNameClinicalDataFileMap);
    }

    public void addFile(Path filePath) {

        if (filePath.toString().endsWith("hg19_coding01.Val.xlsx")) {
            ValFile file = new ValFile(filePath);
            sampleNameValFileMap.put(file.getSampleName(), file);
        } else {
            ClinicalDataFile file = new ClinicalDataFile(filePath);
            sampleNameClinicalDataFileMap.put(file.getSampleName(), file);
        }

        findMatches();
        printRemainingFiles();
    }

    public void addFile(MySourceFile file) {
        if (file.isGeneticDataFile()) {
            sampleNameValFileMap.put(file.getSampleName(), (ValFile) file);
        } else {
            sampleNameClinicalDataFileMap.put(file.getSampleName(), (ClinicalDataFile) file);
        }

        findMatches();
        printRemainingFiles();
    }

    void findMatches() {
        Iterator<String> it = sampleNameValFileMap.keySet().iterator();
        while (it.hasNext()) {
            String s = it.next();
            if (sampleNameClinicalDataFileMap.containsKey(s)) {
                MatchedValClinicalDataFiles m = new MatchedValClinicalDataFiles(sampleNameClinicalDataFileMap.get(s), sampleNameValFileMap.get(s));
                it.remove();
                sampleNameClinicalDataFileMap.remove(s);
                processFiles(m);
            }
        }
    }

    void processFiles(MatchedValClinicalDataFiles m) {
        try {
            if (importFiles(m)) {
                m.moveToImported();
            } else {
                m.moveToFailed();
            }
        } catch (IOException ex) {
            System.out.println("EX: " + ex);
            ex.printStackTrace();
        }
    }

    boolean importFiles(MatchedValClinicalDataFiles m) {
        try {

            new MyImportValWithClinicalDataFromWatch().runImport(m.getClinicalDataFile(), m.getValFile());
            return true;
        } catch (Exception ex) {
            System.out.println("EX: " + ex);
            ex.printStackTrace();
            return false;
        }
    }
}
