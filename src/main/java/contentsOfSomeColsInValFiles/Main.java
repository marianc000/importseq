package contentsOfSomeColsInValFiles;

import contentsOfFirstValColumn.*;
import excel.ExcelOperations;
import static excel.LoadValForVal.selectGeneRows;
import excel.MutationNames;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import static soapmutalizer.ChromosomeCoordinates.combineRefSeqWithNucleotideMutation;
import static utils.CollectionUtils.printCollection;

/**
 *
 * @author mcaikovs
 */
public class Main {

    ExcelOperations excel = new ExcelOperations();

 

    void extractValues(Set<Path> mutationFilePaths) throws Exception {

        Set<String> set = new HashSet<>();

        for (Path sourceFilePath : mutationFilePaths) {
            //   System.out.println(sourceFilePath);
            excel.loadDocument(sourceFilePath.toString());
            selectGeneRows(excel);
            excel.printDocument(sourceFilePath.toString());
            List<String> resultatCol = excel.getColumn("Résultat");

            List<String> refSeqCol = excel.getColumn("Référence");

            for (int r = 1; r < resultatCol.size() - 1; r++) { // first row is headers
                MutationNames mn = new MutationNames(resultatCol.get(r));
                String fused = combineRefSeqWithNucleotideMutation(refSeqCol.get(r), mn.getNucleotideMutation());
                set.add(fused);
            }
        }
        //  printSet(set);  

        printCollection(set);
    }
 

    public void runImport() throws Exception {

        ValFileFinder ff = new ValFileFinder();
        Set<Path> mutationFilePaths = ff.run(SOURCE_FILE_DIR);

        Set<String> samplesInFiles = ff.getSampleNames();
        System.out.println("Samples in files: " + samplesInFiles.size());

        extractValues(mutationFilePaths);
    }

    static String SOURCE_FILE_DIR = "C:\\Projects\\cBioPortal\\data sample\\SECOND SAMPLES\\original";

    // static String SOURCE_FILE_DIR = "C:\\Projects\\cBioPortal\\data sample\\test\\";
    public static void main(String... args) throws Exception {

        Main i = new Main();
        i.runImport();
    }
}
