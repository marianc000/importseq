/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package excel;

import static excel.LoadRROTable.printMap;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
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

    List<String> mergeTwoRows(List<String> row1, List<String> row2) {
        List<String> row = new LinkedList<>();
        if (row1.size() != row2.size()) {
            throw new RuntimeException("rows have different size");
        }
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>");
        System.out.println(">row1" + row1);
        System.out.println(">row2" + row2);
        for (int c = 0; c < row1.size(); c++) {

            String val1 = row1.get(c);
            String val2 = row2.get(c);

            if (val1 == null) {
                if (val2 != null) {
                    val1 = val2;
                }
            } else {
                if (val2 == null) {

                } else {

                    if (val1.equals(val2)) {

                    } else {
                        Set<String> set = new HashSet<>();
                        String[] vals1 = val1.split(VALUE_SEPARATOR);
                        String[] vals2 = val2.split(VALUE_SEPARATOR);
                        Collections.addAll(set, vals1);
                        Collections.addAll(set, vals2);
                        List<String> l = new LinkedList<>(set);
                        Collections.sort(l);
                        String val3 = String.join(VALUE_SEPARATOR, l);
                        System.out.println("val1=" + val1 + "; val2=" + val2 + "; val3=" + val3);
                        val1 = val3;
                    }
                }
            }
            row.add(val1);
        }
        System.out.println("<row3" + row);
        return row;
    }

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

        for (int r = geneCol.size() - 1; r >= 0; r--) {
            String gene = geneCol.get(r);

            if (gene.isEmpty()) {
                excel.removeRow(r);
            } else {
                String mutation = getMutationFromCell(resultatCol.get(r));
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
        //   printMap(geneMutationsMap);
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
