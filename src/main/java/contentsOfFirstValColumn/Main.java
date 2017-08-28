package contentsOfFirstValColumn;

import excel.ExcelOperations;
import excel.LoadRROTable;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author mcaikovs
 */
public class Main {

    ExcelOperations excel = new ExcelOperations();

    void importEverything(Set<Path> mutationFilePaths) throws Exception {
        LoadVal lv = new LoadVal();
        Set<String> set = new HashSet<>();Set<String> aucuneSet = new HashSet<>();
        for (Path sourceFilePath : mutationFilePaths) {
         //   System.out.println(sourceFilePath);
            excel.loadDocument(sourceFilePath.toString());

            List<String> geneCol = excel.getColumn("Gène");
            List<String> hCol = excel.getColumn("H");

            for (int r = geneCol.size() - 1; r > 0; r--) {
                String hCell = hCol.get(r);
                if ("G".equals(hCell) || hCell.isEmpty()) { // some files are aberrant have rows with empty values in H col
                    // excel.removeRow(r);
                    continue;
                }

                String gene = geneCol.get(r);

                if (gene.isEmpty()) {
                    continue;
                }
                set.add(gene);
                if (gene.contains("Aucune")) {
                    aucuneSet.add(sourceFilePath.toString());
                }
            }

        }
        //  printSet(set);  
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        List<String> l = new ArrayList<>(set);
        Collections.sort(l);
        for (String s : l) {
            System.out.println(s);
        }
        printSet(aucuneSet);
    }

    void printSet(Set<String> set) {
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        for (String s : set) {
            System.out.println(s);
        }
        System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
    }
    LoadRROTable rro;

    public void runImport() throws Exception {

        ValFileFinder ff = new ValFileFinder();
        Set<Path> mutationFilePaths = ff.run(SOURCE_FILE_DIR);

        Set<String> samplesInFiles = ff.getSampleNames();
        System.out.println("Samples in files: " + samplesInFiles.size());

        importEverything(mutationFilePaths);
    }

    static String SOURCE_FILE_DIR = "C:\\Projects\\cBioPortal\\data sample\\SECOND SAMPLES\\";

    // static String SOURCE_FILE_DIR = "C:\\Projects\\cBioPortal\\data sample\\test\\";
    public static void main(String... args) throws Exception {

        Main i = new Main();
        i.runImport();
    }
}
