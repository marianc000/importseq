/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package excel;

import static excel.OutputMafRow.convertToRowList;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
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
import utils.FileUtils;
import static utils.FileUtils.saveDocument;

/**
 *
 * @author mcaikovs
 */
public class LoadTabForVal {

    //  static String sourceFilePath = "C:\\Projects\\cBioPortal\\data sample\\H1975.hg19_coding01.TabTest.xlsx";
    static String canonicalMapFileName = "/isoform_overrides_uniprot.txt";
    static String AA_CHANGE_COLUMN = "AAChange.refGene";
    static String GENE_COLUMN = "Gene.refGene";
    static String CHROMOSOME_COLUMN = "Chr";
    static String START_POSITION_COLUMN = "Start";
    static String END_POSITION_COLUMN = "End";
    static String EXONIC_FUNC_REFGENE_COLUMN = "ExonicFunc.refGene";
    static String IGNORED_COLUMN = "#version 2.4";
    static String REF_ALLELE_COLUMN = "Ref";
    static String ALT_ALLELE_COLUMN = "Alt";
    static String REF_COUNT_COLUMN = "Read1";
    static String ALT_COUNT_COLUMN = "Read2";

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

    public LoadTabForVal() throws IOException {
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
                put(EXONIC_FUNC_REFGENE_COLUMN, "Variant_Classification");
                put(REF_ALLELE_COLUMN, "Reference_Allele");
                put(ALT_ALLELE_COLUMN, "Tumor_Seq_Allele1");
                put(REF_COUNT_COLUMN, "t_ref_count");
                put(ALT_COUNT_COLUMN, "t_alt_count");
                put(AA_CHANGE_COLUMN, "HGVSp_Short");
            }
        };

    }

    void replaceExonicFunction() {
        List<String> col = excel.getColumn(EXONIC_FUNC_REFGENE_COLUMN);

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

    final void loadCanonicalGeneMap(String fileName) throws IOException {
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

    public Map<String, Map<String, OutputMafRow>> makeMap() throws IOException, InvalidFormatException {
        Map<String, Map<String, OutputMafRow>> map = new HashMap<>();
        for (int c = 1; c < excel.getColumn(GENE_COLUMN).size(); c++) {
            String gene = excel.getColumn(GENE_COLUMN).get(c);
            String mutation = excel.getColumn(AA_CHANGE_COLUMN).get(c);
            String chromosome = excel.getColumn(CHROMOSOME_COLUMN).get(c);
            int startPosition = Integer.valueOf(excel.getColumn(START_POSITION_COLUMN).get(c));
            int endPosition = Integer.valueOf(excel.getColumn(END_POSITION_COLUMN).get(c));
            String refAllele = excel.getColumn(REF_ALLELE_COLUMN).get(c);
            String tumorAllele = excel.getColumn(ALT_ALLELE_COLUMN).get(c);
            int refCount = Integer.valueOf(excel.getColumn(REF_COUNT_COLUMN).get(c));
            int altCount = Integer.valueOf(excel.getColumn(ALT_COUNT_COLUMN).get(c));
            String variantClassification = excel.getColumn(EXONIC_FUNC_REFGENE_COLUMN).get(c);

            if (map.get(gene) == null) {
                map.put(gene, new HashMap<>());
            }
            Map<String, OutputMafRow> mutationMap = map.get(gene);

            //OutputMafRow(String chromosome, int startPosition, int endPosition, String refAllele, String tumorAllele, String geneName, int refCount, int altCount, String variantClassification, String aaMutation)
            OutputMafRow row = new OutputMafRow(chromosome, startPosition, endPosition, refAllele, tumorAllele, gene, refCount, altCount, variantClassification, mutation);
            mutationMap.put(mutation, row);
        }
        return map;
    }

    public List<OutputMafRow> convertMapToList(Map<String, Map<String, OutputMafRow>> map) {

        List<OutputMafRow> l = new LinkedList<>();
        for (Map<String, OutputMafRow> m : map.values()) {
            l.addAll(m.values());
        }

        return l;
    }

    public Map<String, Map<String, OutputMafRow>> run(String sourceFilePath) throws IOException, InvalidFormatException {

        excel.loadDocument(sourceFilePath);

        // printDocument();
        filterColumns();

        filterAAChangeColumn();
        adjustStopCodonsInAAChangeColumn();
        replaceExonicFunction();
        Map<String, Map<String, OutputMafRow>> geneMutationRowInTabMap = makeMap();

        List<OutputMafRow> l = convertMapToList(geneMutationRowInTabMap);
       
        if (l.size() + 1 != excel.getColumn(GENE_COLUMN).size()) { 
            System.out.println(l.size() + "; " + excel.getColumn(GENE_COLUMN).size());
            throw new RuntimeException("Aberrant row number");
        }
        Files.write(FileUtils.getOutputFilePath(sourceFilePath), convertToRowList(l));
        return geneMutationRowInTabMap;
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

    void adjustStopCodonsInAAChangeColumn() { // in val * in tab X
        List<String> col = excel.getColumn(AA_CHANGE_COLUMN);

        for (int c = 1; c < col.size(); c++) {
            String val = col.get(c);

            col.set(c, transformStopMutationName(val));
        }
    }

    public static void main(String... args) throws IOException, InvalidFormatException {
        new LoadTabForVal().run("C:\\Projects\\cBioPortal\\data sample\\SECOND SAMPLES\\corrected\\H1702318-1A.hg19_coding01.Tab.xlsx");
    }
}
