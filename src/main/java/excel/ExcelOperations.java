/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package excel;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

/**
 *
 * @author mcaikovs
 */
public class ExcelOperations {

    // String sourceFilePath;
    //  static String sourceFilePath = "C:\\Projects\\cBioPortal\\data sample\\H1975.hg19_coding01.TabTest.xlsx";
    static String canonicalMapFileName = "/isoform_overrides_uniprot.txt";
    static String AA_CHANGE_COLUMN = "AAChange.refGene";
    static String EXONIC_FUNC_REFGENE = "ExonicFunc.refGene";
    static String IGNORED_COLUMN = "#version 2.4";
    static String VALIDATION_COLUMN = "chuvValidation";
    static String SAMPLE_NAME_COLUMN = "chuvFileName";
    List<List<String>> doc;

//    public Path getOutputFilePath() {
//        return Paths.get(sourceFilePath + ".out");
//    }
    void saveDocument(Path path) throws IOException {
        List<String> content = new LinkedList<>();
        for (int r = 0; r < doc.get(0).size(); r++) {
            StringBuilder sb = new StringBuilder();
            for (int c = 0; c < doc.size(); c++) {
                sb.append(doc.get(c).get(r));
                if (c < doc.size() - 1) {
                    sb.append("\t");
                }
            }
            content.add(sb.toString());
        }
        Files.write(path, content);
    }

    public List<String> getRow(int index) {
        List<String> row = new LinkedList<>();

        for (int c = 0; c < doc.size(); c++) {
            row.add(doc.get(c).get(index));

        }
        return row;
    }

    public void removeRow(int index) {
        for (int c = 0; c < doc.size(); c++) {
            doc.get(c).remove(index);
        }
    }

    String getCellValue(Cell cell) {
        if (cell == null) {
            //  System.out.println("WARNING: cell is null");
            return "";
        }
        switch (cell.getCellTypeEnum()) {
            case STRING:
                return cell.getRichStringCellValue().getString();

            case NUMERIC:
                double d = cell.getNumericCellValue();
                if (Math.floor(d) == d) { // integer
                    return String.valueOf(new Double(cell.getNumericCellValue()).intValue());
                } else {
                    return String.valueOf(new Double(cell.getNumericCellValue()).floatValue());
                }

            case BLANK:
                return "";
            case BOOLEAN:
            case FORMULA:
            default:
                throw new RuntimeException("this should not happen: " + cell.getCellTypeEnum());
        }
    }

    public List<String> getColumn(String header) {
        for (List<String> col : doc) {
            if (col.get(0).equals(header)) {
                return col;
            }
        }
        return null;
    }

    void filterAAChangeColumn() {
        List<String> col = getColumn(AA_CHANGE_COLUMN);

        for (int c = 1; c < col.size(); c++) {
            String val = col.get(c);

            String[] variants = val.split(Pattern.quote(","));
            String aaChange = null;
            for (String variant : variants) {
                String[] vals = variant.split(Pattern.quote(":"));
                if (vals.length == 1) {
                    aaChange = "";
                    break;
                } else {
                    String transcript = vals[1];
                    if (!transcript.startsWith("NM")) {
                        throw new RuntimeException("Transcript name: " + transcript);
                    }

                    if (cannonicalTranscripts.contains(transcript)) {
                        aaChange = vals[4];
                        break;
                    }
                }
            }
            if (aaChange == null) {
                throw new RuntimeException("canonical transcript not found: ");
            }
            col.set(c, aaChange);
        }
    }

    Map<String, String> exonicFuncRefGene_VariantClassificationMap, sourceTargetHeaders;

    void replaceExonicFunction() {
        List<String> col = getColumn(EXONIC_FUNC_REFGENE);

        for (int c = 1; c < col.size(); c++) {

            String replacement = exonicFuncRefGene_VariantClassificationMap.get(col.get(c));
            if (replacement == null) {
                replacement = "";
            }
            col.set(c, replacement);
        }
    }

    public List<List<String>> loadDocument(String fileName) throws IOException, InvalidFormatException {
        doc = new LinkedList<>();
        Workbook wb = WorkbookFactory.create(new File(fileName));

        Sheet sheet = wb.getSheetAt(0);

        Row headerRow = sheet.getRow(0);

        for (Cell cell : headerRow) {
            doc.add(new LinkedList<>());
            List<String> col = doc.get(doc.size() - 1);
            col.add(cell.getStringCellValue());
        }

        for (int rowNum = 1; rowNum <= sheet.getLastRowNum(); rowNum++) {
            Row row = sheet.getRow(rowNum);
            if (row != null) {
                for (int colNum = 0; colNum < doc.size(); colNum++) {
                    Cell cell = row.getCell(colNum);
                    String cellVal = getCellValue(cell);
                    doc.get(colNum).add(cellVal);
                }
            } else {
                System.out.println("!!!!!!!!!!!!!!!!!!!!!!!! row " + rowNum + " is null, total rows=" + sheet.getLastRowNum() + "; file=" + fileName);
            }
        }
        wb.close();
        System.out.println("loaded rows: " + sheet.getLastRowNum());
        return doc;
    }

    public void printDocument(List<List<String>> document) {
        for (int row = 0; row < document.get(0).size(); row++) {
            System.out.print(row + "\t");
            for (int col = 0; col < document.size(); col++) {
                System.out.print(document.get(col).get(row).replace('\n', ' '));
                if (col < document.size() - 1) {
                    System.out.print("\t");
                }
            }
            System.out.println();
        }
    }

    void filterColumns() {

        for (int col = doc.size() - 1; col >= 0; col--) {
            if (!sourceTargetHeaders.keySet().contains(doc.get(col).get(0))) {
                doc.remove(col);
            }
        }
        System.out.println(sourceTargetHeaders.size() == doc.size());

    }

    void printDocument(String title) {
        System.out.println(">>> " + title);
        printDocument(doc);
        System.out.println("<<< " + title);
    }

    void printDocument() {
        printDocument(doc);
    }

    void addColumn(String header, String value) {
        doc.add(createFilledColumn(header, value));
    }

    void addColumn(List<String> l) {
        doc.add(l);
    }

    void insertColumn(String header, String value) {
        doc.add(0, createFilledColumn(header, value));
    }

    List<String> createFilledColumn(String header, String value) {
        int rowNum = doc.get(0).size();
        List<String> col = new LinkedList<>();
        col.add(header);
        while (col.size() < rowNum) {
            col.add(value);

        }
        return col;
    }

    List<String> loadList(String fileName) throws IOException {
        System.out.println(">ExcelAdaptor:loadList");
        List<String> list = new LinkedList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(fileName)))) {
            String l;
            while ((l = br.readLine()) != null) {
                list.add(l);
            }
        }
        System.out.println("<ExcelAdaptor:loadList: " + list.size());
        return list;
    }

    void loadCanonicalGeneMap(String fileName) throws IOException {
        List<String> l = loadList(fileName);
        for (int c = l.size() - 1; c >= 0; c--) {
            String row = l.get(c);
            String[] cols = row.split("\t", -1);
            String transcript = cols[2];
            if (transcript.isEmpty()) {
                l.remove(c);
            } else {
                l.set(c, transcript.split(Pattern.quote("."))[0]);
            }
        }

        cannonicalTranscripts = new HashSet<>(l);

    }

    Set<String> cannonicalTranscripts;

    String getSampleName(String filePath) {
        Path path = Paths.get(filePath);
        String fileName = path.getFileName().toString();
        return fileName.substring(0, fileName.indexOf("."));
    }

    void insertHeaders() {
        for (List<String> col : doc) {
            String header = sourceTargetHeaders.get(col.get(0));
            if (header == null) {
                throw new RuntimeException("unknown header: " + col.get(0));
            }
            col.add(1, header);
        }
    }

    public Path run() throws IOException, InvalidFormatException {
        //  loadDocument(sourceFilePath);
        // printDocument();
        filterColumns();

        filterAAChangeColumn();
        replaceExonicFunction();

        addColumn(VALIDATION_COLUMN, "Not validated");
        //   addColumn(SAMPLE_NAME_COLUMN, getSampleName(sourceFilePath));
        insertColumn(IGNORED_COLUMN, "");
        insertHeaders();
        //  printDocument();
        //   saveDocument(getOutputFilePath());
        //   return getOutputFilePath();
        return null;
    }

//    public static void main(String... args) throws IOException, InvalidFormatException {
//        new ExcelOperations("C:\\Projects\\cBioPortal\\data sample\\Mix_cell-line.hg19_coding01.Tab.xlsx").run();
//    }
}
