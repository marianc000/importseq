/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package folder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
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
public class ExcelAdaptor {

    static String sourceFilePath = "C:\\Projects\\cBioPortal\\data sample\\Mix_cell-line.hg19_coding01.Tab.xlsx";
    //  static String sourceFilePath = "C:\\Projects\\cBioPortal\\data sample\\H1975.hg19_coding01.TabTest.xlsx";
    static String canonicalMapFileName = "C:\\Projects\\cBioPortal\\isoform_overrides_uniprot.txt";
    static String AA_CHANGE_COLUMN = "AAChange.refGene";
    static String EXONIC_FUNC_REFGENE = "ExonicFunc.refGene";
    static String IGNORED_COLUMN = "#version 2.4";
    static String VALIDATION_COLUMN = "chuvValidation";
    static String SAMPLE_NAME_COLUMN = "chuvFileName";
    List<List<String>> doc;

    Path getOutputFilePath(String sourcePath) {
        return Paths.get(sourcePath + ".out");
    }

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

    String getCellValue(Cell cell) {
        if (cell == null) {
            System.out.println("WARNING: cell is null");
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
                throw new RuntimeException("this should not happen");
        }
    }

    List<String> getColumn(String header) {
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

            String[] variants = col.get(c).split(Pattern.quote(","));
            for (String variant : variants) {
                String[] vals = variant.split(Pattern.quote(":"));
                if (vals.length == 1) {
                    col.set(c, "");
                } else {
                    String transcript = vals[1];
                    if (!transcript.startsWith("NM")) {
                        throw new RuntimeException("Transcript name: " + transcript);
                    }
                    col.set(c, vals[4]);
                    if (cannonicalTranscripts.contains(transcript)) {
                        break;
                    }
                }
            }
        }
    }

    Map<String, String> exonicFuncRefGene_VariantClassificationMap, sourceTargetHeaders;

    public ExcelAdaptor() throws IOException {
        loadCanonicalGeneMap(canonicalMapFileName);

        exonicFuncRefGene_VariantClassificationMap = new HashMap<>();
        exonicFuncRefGene_VariantClassificationMap.put("nonsynonymous SNV", "Missense_Mutation");
        exonicFuncRefGene_VariantClassificationMap.put("stopgain", "Nonsense_Mutation");
        exonicFuncRefGene_VariantClassificationMap.put(".", "Splice_Site");
        exonicFuncRefGene_VariantClassificationMap.put("frameshift deletion", "In_Frame_Del");
        exonicFuncRefGene_VariantClassificationMap.put("nonframeshift deletion", "Missense_Mutation");
        exonicFuncRefGene_VariantClassificationMap.put("frameshift insertion", "Frame_Shift_Ins");
        exonicFuncRefGene_VariantClassificationMap.put("nonframeshift insertion", "In_Frame_Ins");
        exonicFuncRefGene_VariantClassificationMap.put("nonsynonymous SNV", "Missense_Mutation");
        exonicFuncRefGene_VariantClassificationMap.put("nonsynonymous SNV", "Missense_Mutation");

        sourceTargetHeaders = new HashMap<String, String>() {
            {
                put("Gene.refGene", "Hugo_Symbol");
                put("Chr", "Chromosome");
                put("Start", "Start_Position");
                put("End", "End_Position");
                put(EXONIC_FUNC_REFGENE, "Variant_Classification");
                put("Ref", "Reference_Allele");
                put("Alt", "Tumor_Seq_Allele1");
                put("Read1", "t_ref_count");
                put("Read2", "t_alt_count");
                put(AA_CHANGE_COLUMN, "HGVSp_Short");
                put(VALIDATION_COLUMN, "Validation_Status");
                put(SAMPLE_NAME_COLUMN, "Tumor_Sample_Barcode");
                put(IGNORED_COLUMN, "IgnoreMe");
            }
        };

    }

// 
//"Silent"
// 
//"Translation_Start_Site"
//"Nonstop_Mutation"
//"3'UTR"
//"3'Flank"
//"5'UTR"
//"5'Flank"
//"IGR1"
//"Intron"
//"RNA"
//"Targeted_Region"
//"De_novo_Start_InFrame"
//"De_novo_Start_OutOfFrame" 
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

    public void loadDocument(String fileName) throws IOException, InvalidFormatException {
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
            for (int colNum = 0; colNum < doc.size(); colNum++) {
                doc.get(colNum).add(getCellValue(row.getCell(colNum)));
            }
        }
    }

    void printDocument(List<List<String>> document) {
        for (int row = 0; row < document.get(0).size(); row++) {
            for (int col = 0; col < document.size(); col++) {
                System.out.print(document.get(col).get(row));
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

    void printDocument() {
        printDocument(doc);
    }

    void addColumn(String header, String value) {
        doc.add(createFilledColumn(header, value));
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

    void loadCanonicalGeneMap(String fileName) throws IOException {
        List<String> l = Files.readAllLines(Paths.get(fileName));
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

    void run(String filePath) throws IOException, InvalidFormatException {
        loadDocument(filePath);
        filterColumns();

        filterAAChangeColumn();
        replaceExonicFunction();

        addColumn(VALIDATION_COLUMN, "Not validated");
        addColumn(SAMPLE_NAME_COLUMN, getSampleName(filePath));
        insertColumn(IGNORED_COLUMN, "");
        insertHeaders();
        printDocument();
        saveDocument(getOutputFilePath(filePath));
    }

    public static void main(String... args) throws IOException, InvalidFormatException {
        new ExcelAdaptor().run(sourceFilePath);

    }
}
