/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package excel;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import soapmutalizer.ChromosomeCoordinates;

/**
 *
 * @author mcaikovs
 */
public class OutputMafRow {

    String chromosome;
    int startPosition;
    int endPosition;
    String refAllele;
    String tumorAllele;
    String geneName;
    int refCount;
    int altCount;
    String variantClassification;
    String aaMutation;
    String validated = "Validated";
    String fileName;
    MutationNames mutationNames;
    String sampleName;
    AlleleFrequency alleleFrequency;
    ChromosomeCoordinates chromosomeCoordinates;

    public OutputMafRow(String resultatInVal, String geneNameInVal, String sampleName, String alleleFrequency, String coverage, String refSeq) {
        mutationNames = new MutationNames(resultatInVal);
        this.geneName = geneNameInVal;
        this.sampleName = sampleName;
        this.alleleFrequency = new AlleleFrequency(alleleFrequency, coverage);
        chromosomeCoordinates = new ChromosomeCoordinates(refSeq, mutationNames.getNucleotideMutation());
    }

    public String getIgnoreMe() {
        return "";
    }

    public String getChromosome() {
        return chromosomeCoordinates.getChromosome();
    }

    public int getStartPosition() {
        return chromosomeCoordinates.getStartPostion();
    }

    public int getEndPosition() {
        return chromosomeCoordinates.getEndPosition();
    }

    public String getGeneName() {
        return geneName;
    }

    public String getRefAllele() {
        return mutationNames.getRefAllele(); // TODO: it is not set for mutations other than substitions, it could be recovered from ncbi
    }

    public String getTumorAllele() {
        return mutationNames.getAltAllele();
    }

    public String getRefCount() {
        return alleleFrequency.getRefCount();
    }

    public String getAltCount() {
        return alleleFrequency.getAltCount();
    }

    public String getVariantClassification() {
        return mutationNames.getVariantClassification().toString();
    }

    public String getAaMutation() { // HGVSp_Short
        return mutationNames.getProteinMutation();
    }

    public String getValidated() {
        return "Validated";
    }

    public String getFileName() {
        return fileName;
    }

    public String getSampleName() {
        return sampleName;
    }

    public String toMafRow() {
        return getIgnoreMe() + "\t" + getChromosome() + "\t" + getStartPosition() + "\t" + getEndPosition() + "\t" + getRefAllele() + "\t" + getTumorAllele()
                + "\t" + getGeneName() + "\t" + getRefCount() + "\t" + getAltCount() + "\t" + getVariantClassification() + "\t"
                + getAaMutation() + "\t" + getValidated() + "\t" + getSampleName();
    }

    static String[] headers = {"IgnoreMe", "Chromosome", "Start_Position", "End_Position", "Reference_Allele", "Tumor_Seq_Allele1", "Hugo_Symbol", "t_ref_count", "t_alt_count", "Variant_Classification", "HGVSp_Short", "Validation_Status", "Tumor_Sample_Barcode"};

    static String getHeaders() {
        return String.join("\t", headers);
    }

    public static List<String> convertToRowList(List<OutputMafRow> l) {
        List<String> rows = new LinkedList<>();
        String[] firstRow = new String[headers.length];
        Arrays.fill(firstRow, "");
        firstRow[0] = "#version 2.4";
        rows.add(String.join("\t", firstRow));
        rows.add(OutputMafRow.getHeaders());
        for (OutputMafRow o : l) {
            rows.add(o.toMafRow());
        }
        return rows;
    }
}
