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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import static utils.CollectionUtils.printCollection;
import utils.FileUtils;

public class LoadValForVal {

    static int HEADERS_ARE_IN_ROW = 0;
    //  static String RRO_FILE_PATH = "C:\\Projects\\cBioPortal\\data sample\\SECOND SAMPLES\\20170725 RRO CBIO exportMCunmodified.xlsx";

    static String VALUE_SEPARATOR = ",";

//   
    ExcelOperations excel = new ExcelOperations();

    public List<String> getHeaders() {
        return excel.getRow(HEADERS_ARE_IN_ROW);
    }

    String getHeader(int index) {
        return getHeaders().get(index);
    }

    int getHeaderIndex(String header) {
        return getHeaders().indexOf(header);
    }

    public int getNipHeaderIndex() {
        return getHeaders().indexOf(IPP_HEADER_NAME);
    }

    public int getRefextHeaderIndex() {
        return getHeaders().indexOf(SAMPLE_NAME_HEADER_NAME);
    }

    public static String SAMPLE_NAME_HEADER_NAME = "REFEXT";
    public static String IPP_HEADER_NAME = "NIP";

// TODO: it is not necessary to make such map, the values would suffice
    public Map<String, Set<String>> getGeneMutationsMap() throws IOException, InvalidFormatException {

        return geneMutationsMap;
    }
    Map<String, Set<String>> geneMutationsMap;

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

    public String NUCLEOTIDE_MUTATION_COLUMN = "nucleotideMutation", PROTEIN_MUTATION_COLUMN = "proteinMutation";

    private List<OutputMafRow> addMutationsColumns(ExcelOperations excel, String sampleName) {
        List<String> resultatCol = excel.getColumn("Résultat");
        List<String> geneCol = excel.getColumn("Gène");
        List<String> alleleFrequencyCol = excel.getColumn("Fr. allélique");
        List<String> coverageCol = excel.getColumn("Couvert.");
        List<String> refSeqCol = excel.getColumn("Référence");
        List<OutputMafRow> outCol = new ArrayList<>();

        for (int r = 1; r < resultatCol.size(); r++) {
            outCol.add(new OutputMafRow(resultatCol.get(r), geneCol.get(r), sampleName,alleleFrequencyCol.get(r),coverageCol.get(r), refSeqCol. get(r)  ));
        }

        printCollection(convertToRowList(outCol));
        System.out.println(OutputMafRow.getHeaders());

        return outCol;
        //  excel.printDocument("addMutationsColumns");
    }

    public void init(String filePath) throws IOException, InvalidFormatException {

        excel.loadDocument(filePath);
        selectGeneRows(excel);
        geneMutationsMap = new HashMap<>();
        String sampleName = FileUtils.convertFilePathToSampleName(filePath);
        Files.write(Paths.get(filePath + ".out2"), convertToRowList(addMutationsColumns(excel, sampleName)));
        List<String> resultatCol = excel.getColumn("Résultat");
        List<String> geneCol = excel.getColumn("Gène");

//        for (int r = resultatCol.size() - 1; r >= 0; r--) {
//
//            String gene = geneCol.get(r);
//
//            String mutation = getMutationFromCell(resultatCol.get(r));
//            gene = gene.split(" ")[0]; // for cases like CDKN2A (p14ARF)
//            if (mutation == null) {
//                excel.removeRow(r);
//            } else {
//                System.out.println(gene + "; " + mutation);
//                if (geneMutationsMap.get(gene) == null) {
//                    geneMutationsMap.put(gene, new HashSet<>());
//                }
//                if (!geneMutationsMap.get(gene).add(mutation)) {
//                    throw new RuntimeException("mutation " + mutation + " was already in set " + gene);
//                }
//            }
//        }
        //       printMap(geneMutationsMap);
        //  excel.printDocument();
    }

    public static void main(String... args) throws IOException, InvalidFormatException {
        new LoadValForVal().init("C:\\Projects\\cBioPortal\\data sample\\SECOND SAMPLES\\corrected\\H1702318-1A.hg19_coding01.Val.xlsx");
    }
}
