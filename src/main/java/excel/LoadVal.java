/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package excel;

import static excel.LoadRROTable.printMap;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

public class LoadVal {

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

    public void init(String filePath) throws IOException, InvalidFormatException {

        excel.loadDocument(filePath);

        geneMutationsMap = new HashMap<>();
        List<String> resultatCol = excel.getColumn("Résultat");
        List<String> geneCol = excel.getColumn("Gène");
        List<String> hCol = excel.getColumn("H");
        List<String> refSeq = excel.getColumn("Référence");
        for (int r = geneCol.size() - 1; r >= 0; r--) {
            if (!"G".equals(hCol.get(r))) { // some files are aberrant have rows with empty values in H col
                excel.removeRow(r);
                continue;
            }

            String gene = geneCol.get(r);

            if (gene.isEmpty()) {
                excel.removeRow(r);
            } else {
                String mutation = getMutationFromCell(resultatCol.get(r));
                gene = gene.split(" ")[0]; // for cases like CDKN2A (p14ARF)
                if (mutation == null) {
                    excel.removeRow(r);
                } else {
                    System.out.println(gene + "; " + mutation);
                    if (geneMutationsMap.get(gene) == null) {
                        geneMutationsMap.put(gene, new HashSet<>());
                    }
                    if (!geneMutationsMap.get(gene).add(mutation)) {
                        throw new RuntimeException("mutation " + mutation + " was already in set " + gene);
                    }
                }
            }
        }
        printMap(geneMutationsMap);
        //  excel.printDocument();
    }

    String getMutationFromCell(String s) {
        Matcher m = p.matcher(s);
        if (m.matches()) {
            return m.group(1);
        }
        return null;
    }
    Pattern p = Pattern.compile(".+\\((.+)\\)", Pattern.DOTALL);

    public static void main(String... args) throws IOException, InvalidFormatException {
        new LoadVal().init("C:\\Projects\\cBioPortal\\data sample\\SECOND SAMPLES\\C1607855-1SS.hg19_coding01.Val.xlsx");
    }
}
