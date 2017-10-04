/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package excel;

import java.io.IOException;
import java.nio.file.Paths;
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
import static utils.CollectionUtils.printListAsTabbedRow;
import static utils.CollectionUtils.printMap;

/**
 *
 * @author mcaikovs
 */
public class LoadRROTable {

    static int HEADERS_ARE_IN_ROW = 0;
    //  static String RRO_FILE_PATH = "C:\\Projects\\cBioPortal\\data sample\\SECOND SAMPLES\\20170725 RRO CBIO exportMCunmodified.xlsx";

    static String VALUE_SEPARATOR = "|", ESCAPED_VALUE_SEPARATOR = Pattern.quote(VALUE_SEPARATOR);

    List<String> mergeTwoRows(List<String> row1, List<String> row2) {

        List<String> row = new LinkedList<>();
        if (row1.size() != row2.size()) {
            throw new RuntimeException("rows have different size");
        }
        System.out.println(">mergeTwoRows:");
        System.out.println(">row1" + row1);
        System.out.println(">row2" + row2);
        for (int c = 0; c < row1.size(); c++) {

            String val1 = row1.get(c);
            String val2 = row2.get(c);

            if (val1 == null || val1.isEmpty()) {
                if (val2 != null) {
                    val1 = val2;
                }
            } else {
                if (val2 == null || val2.isEmpty()) {

                } else {

                    if (val1.equals(val2)) {

                    } else {
                        Set<String> set = new HashSet<>();
                        String[] vals1 = val1.split(ESCAPED_VALUE_SEPARATOR);
                        String[] vals2 = val2.split(ESCAPED_VALUE_SEPARATOR);
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
        //  System.out.println("<row3" + row);
        return row;
    }

    ExcelOperations excel = new ExcelOperations(HEADERS_ARE_IN_ROW);

    void ensureThatSeparatorsAreAbsent(List<String> row) {
        for (String s : row) {
            if (s != null && s.contains(VALUE_SEPARATOR)) {
                throw new RuntimeException("Separator " + VALUE_SEPARATOR + " is present in " + s);
            }
        }
    }

    Map<String, List<String>> loadRefextNipMap() throws IOException, InvalidFormatException {

        //   excel.printDocument();
        Map<String, List<String>> refextRowMap = new LinkedHashMap<>();
        List<String> refextCol = excel.getColumn(SAMPLE_NAME_HEADER_NAME);

        for (int c = HEADERS_ARE_IN_ROW + 1; c < refextCol.size(); c++) {
            String val = refextCol.get(c).trim();

            List<String> newRow = excel.getRow(c);
            ensureThatSeparatorsAreAbsent(newRow);
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
        return excel.getHeaders();
    }

    String getHeader(int index) {
        return excel.getHeader(index);
    }

    int getHeaderIndex(String header) {
        return excel.getHeaderIndex(header);
    }

    public int getNipHeaderIndex() {
        return getHeaderIndex(IPP_HEADER_NAME);
    }

    public int getRefextHeaderIndex() {
        return getHeaderIndex(SAMPLE_NAME_HEADER_NAME);
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

    Map<String, String> DUPLICATE_COLUMNS_TO_REMOVE_AND_RENAME = new HashMap<String, String>() {
        {
            put("NOMCODIF", "ORGANE");
            put("NOMCODIF_1", "LESION");
            put("NOMCODIF_2", "ORIGMETA");
        }
    };

    void removeUselessColumns() {
        for (String s : DUPLICATE_COLUMNS_TO_REMOVE_AND_RENAME.values()) {
            excel.removeColumn(getHeaderIndex(s));
        }
    }

    void renameColumns() {
        List<String> headers = getHeaders();
        for (String header : DUPLICATE_COLUMNS_TO_REMOVE_AND_RENAME.keySet()) {

            headers.set(getHeaderIndex(header), DUPLICATE_COLUMNS_TO_REMOVE_AND_RENAME.get(header));
        }
        excel.setHeaders(headers);
    }

    void removeUselessColumnsAndRenameDuplicateColumns() {
        removeUselessColumns();
        renameColumns();
    }

    static String ORIGINAL_REFERENCE_COLUMN = "TXTRC", ORIGINAL_BLOCK_NUMBER_COLUMN = "ORIGINAL_BLOCK", EXTERNAL_REFERENCE_COLUMN = "EXTERNAL_REF";

    void divideExternalReferenceColumnIntoTwoColumns() {
        List<String> originalRefCol = excel.getColumn(ORIGINAL_REFERENCE_COLUMN);
        List<String> refextCol = excel.getColumn(SAMPLE_NAME_HEADER_NAME);

        excel.addColumn(ORIGINAL_BLOCK_NUMBER_COLUMN);
        excel.addColumn(EXTERNAL_REFERENCE_COLUMN);
        List<String> originalBlockCol = excel.getColumn(ORIGINAL_BLOCK_NUMBER_COLUMN);
        List<String> externalRefCol = excel.getColumn(EXTERNAL_REFERENCE_COLUMN);
        for (int c = HEADERS_ARE_IN_ROW + 1; c < refextCol.size(); c++) {
            String refext = refextCol.get(c);
            String txtrc = originalRefCol.get(c);

            if (!refext.equals(txtrc)) {
                System.out.println(refext + "; " + txtrc);
                if (!txtrc.startsWith("pool des blocs")) {
                    if (!txtrc.startsWith(refext)) {
                        throw new RuntimeException("txtrc does not start with refex");
                    }
                    String ancientRef = extractAncientReference(txtrc);
                    if (ancientRef != null) {
                        originalBlockCol.set(c, ancientRef);
                    } else {
                        String externalRef = extractExternalReference(txtrc);
                        if (externalRef != null) {
                            externalRefCol.set(c, externalRef);
                        } else {
                            throw new RuntimeException("everything is null");
                        }
                    }
                } else {
                    System.out.println("pool");
                }
            }
        }
        excel.removeColumn(getHeaderIndex(ORIGINAL_REFERENCE_COLUMN));
    }

    Pattern ANCIENT_REFERENCE_PATTERN = Pattern.compile(".+\\(ancienne réf\\.(.+)\\)");

    String extractAncientReference(String str) {
        Matcher m = ANCIENT_REFERENCE_PATTERN.matcher(str);
        if (m.matches()) {
            return m.group(1).trim();
        }
        return null;
    }
    //H1707849-1A (réf. KIRCHNER Volker : AB 38402)

    Pattern EXTERNAL_REFERENCE_PATTERN = Pattern.compile(".+\\(réf\\.(.+)\\)");

    String extractExternalReference(String str) {
        Matcher m = EXTERNAL_REFERENCE_PATTERN.matcher(str);
        if (m.matches()) {
            return m.group(1).trim();
        }
        return null;
    }

    public Map<String, List<String>> init(String rroFilePath) throws IOException, InvalidFormatException {

        excel.loadDocument(rroFilePath);
        removeUselessColumnsAndRenameDuplicateColumns();
        printListAsTabbedRow(getHeaders());
        divideExternalReferenceColumnIntoTwoColumns();

        refextNipMap = loadRefextNipMap();
        nipSamplesMap = createNipSampleMap(refextNipMap.values());
        excel.printDocument("FINAL");
        excel.saveDocument(Paths.get(SOURCE_FILE + ".out"));
        return refextNipMap;
    }

    public Map<String, List<List<String>>> getNipSamplesMap() {
        return nipSamplesMap;
    }

    static String SOURCE_FILE = "C:\\Projects\\cBioPortal\\data sample\\NEW CLINICAL DATA\\20170824 Cbioportal export2.xlsx";

    public static void main(String... args) throws IOException, InvalidFormatException {
        new LoadRROTable().init(SOURCE_FILE);
    }
}
