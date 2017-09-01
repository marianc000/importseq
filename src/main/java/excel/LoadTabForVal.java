/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package excel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import static utils.FileUtils.saveDocument;

/**
 *
 * @author mcaikovs
 */
public class LoadTabForVal {

    public LoadTabForVal(String sourceFilePath) throws IOException {
        this();
        this.sourceFilePath = sourceFilePath;
    }

    String sourceFilePath;
    //  static String sourceFilePath = "C:\\Projects\\cBioPortal\\data sample\\H1975.hg19_coding01.TabTest.xlsx";
    static String canonicalMapFileName = "/isoform_overrides_uniprot.txt";
    static String AA_CHANGE_COLUMN = "AAChange.refGene";
    static String GENE_COLUMN = "Gene.refGene";
    static String CHROMOSOME_COLUMN = "Chr";
    static String START_POSITION_COLUMN = "Start";
    static String END_POSITION_COLUMN = "End";

    static String EXONIC_FUNC_REFGENE = "ExonicFunc.refGene";
    static String IGNORED_COLUMN = "#version 2.4";
    static String VALIDATION_COLUMN = "chuvValidation";
    static String SAMPLE_NAME_COLUMN = "chuvFileName";
    //  List<List<String>> doc;

    public Path getOutputFilePath() {
        return Paths.get(sourceFilePath + ".out");
    }

    String getCellValue(Cell cell) {
        if (cell == null) {
            // System.out.println("WARNING: cell is null");
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
                throw new RuntimeException("this should not happen: type: " + cell.getCellTypeEnum() + "; val: " + cell.getStringCellValue());
        }
    }

    void filterAAChangeColumn() {
        List<String> col = excel.getColumn(AA_CHANGE_COLUMN);

        for (int c = 1; c < col.size(); c++) {
            String val = col.get(c);

            col.set(c, selectCanonicalTranscript(val));
        }
    }

    String selectCanonicalTranscript(String val) {
        //  System.out.println(">selectCanonicalTranscript: val=" + val);
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
            aaChange = selectCanonicalTranscriptForGeneWithoutCanonicalTranscript(val);
        }
        if (aaChange == null) {
            throw new RuntimeException("canonical transcript not found: ");
        }
        return aaChange;
    }

    // only one mutation proposed, it is identical for all transcripts
    String selectCanonicalTranscriptForGeneWithoutCanonicalTranscript(String val) {
        //System.out.println(">selectCanonicalTranscriptForGeneWithoutCanonicalTranscript: val=" + val);
        String[] variants = val.split(Pattern.quote(","));

        Set<String> set = new HashSet<>();

        for (String variant : variants) {
            String[] vals = variant.split(Pattern.quote(":"));
            set.add(vals[4]);
        }

        if (set.size() == 1) {
            return set.iterator().next();
        }

        return null;

    }
    Map<String, String> exonicFuncRefGene_VariantClassificationMap, sourceTargetHeaders;

    private LoadTabForVal() throws IOException {
        loadCanonicalGeneMap(canonicalMapFileName);

        exonicFuncRefGene_VariantClassificationMap = new HashMap<>();
        exonicFuncRefGene_VariantClassificationMap.put("nonsynonymous SNV", "Missense_Mutation");
        exonicFuncRefGene_VariantClassificationMap.put("stopgain", "Nonsense_Mutation");
        exonicFuncRefGene_VariantClassificationMap.put(".", "Splice_Site");
        exonicFuncRefGene_VariantClassificationMap.put("frameshift deletion", "Frame_Shift_Del");
        exonicFuncRefGene_VariantClassificationMap.put("nonframeshift deletion", "In_Frame_Del");
        exonicFuncRefGene_VariantClassificationMap.put("frameshift insertion", "Frame_Shift_Ins");
        exonicFuncRefGene_VariantClassificationMap.put("nonframeshift insertion", "In_Frame_Ins");
        exonicFuncRefGene_VariantClassificationMap.put("nonsynonymous SNV", "Missense_Mutation");
        exonicFuncRefGene_VariantClassificationMap.put("stoploss", "Nonstop_Mutation");
        sourceTargetHeaders = new HashMap<String, String>() {
            {
                put(GENE_COLUMN, "Hugo_Symbol");
                put(CHROMOSOME_COLUMN, "Chromosome");
                put(START_POSITION_COLUMN, "Start_Position");
                put(END_POSITION_COLUMN, "End_Position");
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

//Silent
//Translation_Start_Site
//Nonstop_Mutation
//3'UTR
//3'Flank
//5'UTR
//5'Flank
//IGR1 
//Intron
//RNA
//Targeted_Region
//De_novo_Start_InFrame
//De_novo_Start_OutOfFrame.     
    void replaceExonicFunction() {
        List<String> col = excel.getColumn(EXONIC_FUNC_REFGENE);

        for (int c = 1; c < col.size(); c++) {

            String replacement = exonicFuncRefGene_VariantClassificationMap.get(col.get(c));
            if (replacement == null) {
                // replacement = "";
                throw new RuntimeException("variant classification not found for: " + col.get(c));
            }
            col.set(c, replacement);
        }
    }

    void filterColumns() {
        excel.filterColumns(sourceTargetHeaders.keySet());
    }

    List<String> loadList(String fileName) throws IOException {
        // System.out.println(">ExcelAdaptor:loadList");
        List<String> list = new LinkedList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(fileName)))) {
            String l;
            while ((l = br.readLine()) != null) {
                list.add(l);
            }
        }
        // System.out.println("<ExcelAdaptor:loadList: " + list.size());
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

//    void insertHeaders() {
//        for (List<String> col : doc) {
//            String header = sourceTargetHeaders.get(col.get(0));
//            if (header == null) {
//                throw new RuntimeException("unknown header: " + col.get(0));
//            }
//            col.add(1, header);
//        }
//    }
    ExcelOperations excel = new ExcelOperations();

    public Map<String, Map<String, List<String>>> makeMap() throws IOException, InvalidFormatException {
        Map<String, Map<String, List<String>>> map = new HashMap<>();
        for (int c = 1; c < excel.getColumn(GENE_COLUMN).size(); c++) {
            String gene = excel.getColumn(GENE_COLUMN).get(c);
            String mutation = excel.getColumn(AA_CHANGE_COLUMN).get(c);

            if (map.get(gene) == null) {
                map.put(gene, new HashMap<>());
            }
            Map<String, List<String>> mutationMap = map.get(gene);
            mutationMap.put(mutation, excel.getRow(c));
        }
        return map;
    }

    public Path run() throws IOException, InvalidFormatException {

        excel.loadDocument(sourceFilePath);

        List<OutputMafRow> mafRows = new LoadValForVal().run(sourceFilePath.replace("coding01.Tab", "coding01.Val"));

        // printDocument();
        filterColumns();

        filterAAChangeColumn();
        replaceExonicFunction();
        Map<String, Map<String, List<String>>> geneMutationRowInTabMap = makeMap();
        
        for (int c = 0; c < excel.getColumn(GENE_COLUMN).size(); c++) {
            String gene = excel.getColumn(GENE_COLUMN).get(c);
            String mutation = excel.getColumn(AA_CHANGE_COLUMN).get(c);
            String mutationType = excel.getColumn(EXONIC_FUNC_REFGENE).get(c);
            String chromosome = excel.getColumn(CHROMOSOME_COLUMN).get(c);
            Set<String> validMutations = geneValidMutationsMap.get(gene);
            System.out.println("gene: " + gene + "; mutation: " + mutation + "; validMutations: " + validMutations);
            if (validMutations != null) {

                boolean validated = validMutations.remove(mutation);

                if (!validated) {
                    // try to replace * by X
                    String mutation2 = transformStopMutationName(mutation);
                    validated = validMutations.remove(transformStopMutationName(mutation2));
                    System.out.println("mutation2: " + mutation2 + "; validMutations: " + validMutations);
                }
                if (!validated) { // splicing, no mutation in protein
                    if (mutation.isEmpty() && mutationType.equals("Splice_Site")) {
                        validated = validMutations.remove("Splicing");
                    }
                }
                if (!validated) {
                    for (String validMutation : validMutations) { //p.L252_I254del
                        String transformedValidMutation = transformDeletionMutationName(validMutation); //p.252_254del
                        if (mutation.equals(transformedValidMutation)) {
                            validated = validMutations.remove(validMutation);
                            break;
                        }
                    }
                }

                if (validated) {
                    validationCol.set(c, "Validated in Val file");
                    if (validMutations.isEmpty()) {
                        geneValidMutationsMap.remove(gene);
                    }
                }
            }
        }
        //   LoadRROTable.printMap(geneValidMutationsMap);
        if (!geneValidMutationsMap.isEmpty()) {
            // throw new RuntimeException("map is not empty");
            System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" + sourceFilePath);
            System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!map is not empty");
            LoadRROTable.printMap(geneValidMutationsMap);
            System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        }

        addColumn(SAMPLE_NAME_COLUMN, getSampleName(sourceFilePath));
        insertColumn(IGNORED_COLUMN, "");
        insertHeaders();
        //  printDocument();
        saveDocument(getOutputFilePath(), doc);
        return getOutputFilePath();
    }
    Pattern delMutationPattern = Pattern.compile("p\\.[A-Z]\\d+_[A-Z]\\d+del");

    String transformDeletionMutationName(String nameInVal) {
        Matcher m = delMutationPattern.matcher(nameInVal);
        if (m.matches()) {
            return nameInVal.replaceAll("[A-Z]", "");
        }
        return nameInVal;
    }
    Pattern stopMutationPattern = Pattern.compile("p\\.[A-Z]\\d+X");

    String transformStopMutationName(String nameInTab) {
        Matcher m = stopMutationPattern.matcher(nameInTab);
        if (m.matches()) {
            return nameInTab.replace("X", "*");
        }

        return nameInTab;
    }

    public static void main(String... args) throws IOException, InvalidFormatException {
        new LoadTabForVal("C:\\Projects\\cBioPortal\\data sample\\Mix_cell-line.hg19_coding01.Tab.xlsx").run();
    }
}
