/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package excel;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

/**
 *
 * @author mcaikovs
 */
public class LoadRROTable {

    static int HEADERS_ARE_IN_ROW = 0;
    //  static String RRO_FILE_PATH = "C:\\Projects\\cBioPortal\\data sample\\SECOND SAMPLES\\20170725 RRO CBIO exportMCunmodified.xlsx";

    static void printMap(Map map) {
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        for (Object k : map.keySet()) {
            System.out.println(k + "\t" + map.get(k));
        }
        System.out.println(map.size());
        System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
    }
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

    Map<String, List<String>> loadRefextNipMap() throws IOException, InvalidFormatException {

        //   excel.printDocument();
        Map<String, List<String>> refextRowMap = new LinkedHashMap<>();
        List<String> refextCol = excel.getColumn(SAMPLE_NAME_HEADER_NAME);

        for (int c = HEADERS_ARE_IN_ROW + 1; c < refextCol.size(); c++) {
            String val = refextCol.get(c).trim();
            List<String> newRow = excel.getRow(c);
            if (refextRowMap.containsKey(val)) {
                List<String> row1 = refextRowMap.get(val);
                refextRowMap.put(val, mergeTwoRows(row1, newRow));
            } else {
                refextRowMap.put(val, newRow);
            }
        }
        printMap(refextRowMap);
        return refextRowMap;
    }

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

    Map<String, List<List<String>>> createNipSampleMap(Collection< List<String>> rows) throws IOException, InvalidFormatException {

        Map<String, List<List<String>>> nipSamplesMap = new LinkedHashMap<>();
        int nipIndex = getNipHeaderIndex();
        for (List<String> row : rows) {
            String nip = row.get(nipIndex);

            if (!nipSamplesMap.containsKey(nip)) {
                nipSamplesMap.put(nip, new LinkedList<>());
            }

            List<List<String>> samples = nipSamplesMap.get(nip);
            samples.add(row);

        }
        printMap(nipSamplesMap);
        return nipSamplesMap;

    }
// TODO: it is not necessary to make such map, the values would suffice

    public Map<String, List<String>> getRefextNipMap() throws IOException, InvalidFormatException {

        return refextNipMap;
    }
    Map<String, List<String>> refextNipMap;
    Map<String, List<List<String>>> nipSamplesMap;

    public Map<String, List<String>> init(String rroFilePath) throws IOException, InvalidFormatException {
        excel.loadDocument(rroFilePath);
        refextNipMap = loadRefextNipMap();
        nipSamplesMap = createNipSampleMap(refextNipMap.values());
        return refextNipMap;
    }

    public static void main(String... args) throws IOException, InvalidFormatException {
        new LoadRROTable().init("C:\\Projects\\cBioPortal\\data sample\\SECOND SAMPLES\\20170725 RRO CBIO exportMCunmodified.xlsx");
    }
}
