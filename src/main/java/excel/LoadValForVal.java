/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package excel;

import static excel.OutputMafRow.convertToRowList;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import static utils.CollectionUtils.printCollection;
import utils.FileUtils;

public class LoadValForVal {

    public static void selectGeneRows(ExcelOperations excel) {
        List<String> hCol = excel.getColumn("H");
        List<String> geneCol = excel.getColumn("Gène");
        List<String> resultatCol = excel.getColumn("Résultat");
        for (int r = hCol.size() - 1; r > 0; r--) {
            if (!"G".equals(hCol.get(r))) { // some files are aberrant have rows with empty values in H col
                excel.removeRow(r);
                continue;
            }

            if (geneCol.get(r).isEmpty()) {
                excel.removeRow(r);
                continue;
            }
            if (resultatCol.get(r).isEmpty()) { // Aucune mutation identifiée in Gene column, see H1703976-1C
                excel.removeRow(r);
                continue;
            }
            if ("wild type".equals(resultatCol.get(r))) { // some files are aberrant have rows with empty values in H col
                excel.removeRow(r);
            }
        }
        // excel.printDocument("selectGeneRows");
    }

    private List<OutputMafRow> convertToMafRowList(ExcelOperations excel, String sampleName) {
        List<String> resultatCol = excel.getColumn("Résultat");
        List<String> geneCol = excel.getColumn("Gène");

        List<String> alleleFrequencyCol = excel.getColumn("Fr. allélique");
        List<String> coverageCol = excel.getColumn("Couvert.");
        List<String> refSeqCol = excel.getColumn("Référence");
        List<OutputMafRow> outCol = new ArrayList<>();

        for (int r = 1; r < resultatCol.size(); r++) {
            outCol.add(new OutputMafRow(resultatCol.get(r), cleanGeneName(geneCol.get(r)), sampleName, alleleFrequencyCol.get(r), coverageCol.get(r), refSeqCol.get(r)));
        }

   //     printCollection(convertToRowList(outCol));
        return outCol;

    }

    String cleanGeneName(String geneNameInVal) {
        return geneNameInVal.split(" ")[0]; // for cases like CDKN2A (p14ARF)
    }

    public List<OutputMafRow> run(String filePath) throws IOException, InvalidFormatException {
        ExcelOperations excel = new ExcelOperations();
        excel.loadDocument(filePath);
        selectGeneRows(excel);

        String sampleName = FileUtils.convertFilePathToSampleName(filePath);
        List<OutputMafRow> rows = convertToMafRowList(excel, sampleName);
        Files.write(FileUtils.getOutputFilePath(filePath), convertToRowList(rows));
        return rows;
    }

    public static void main(String... args) throws IOException, InvalidFormatException {
        new LoadValForVal().run("C:\\Projects\\cBioPortal\\data sample\\SECOND SAMPLES\\corrected\\H1702318-1A.hg19_coding01.Val.xlsx");
    }
}
