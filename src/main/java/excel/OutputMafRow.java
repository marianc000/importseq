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
import static soapmutalizer.ChromosomeCoordinates.combineRefSeqWithNucleotideMutation;

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
    String refSeqWithNucleotideMutation;

    public OutputMafRow(String resultatInVal, String geneNameInVal, String sampleName, String alleleFrequency, String coverage, String refSeq) {
        mutationNames = new MutationNames(resultatInVal);
        this.geneName = geneNameInVal;
        this.sampleName = sampleName;
        this.alleleFrequency = new AlleleFrequency(alleleFrequency, coverage);
        refSeqWithNucleotideMutation = combineRefSeqWithNucleotideMutation(refSeq, mutationNames.getNucleotideMutation()); // save it to maf to have original data
        chromosomeCoordinates = new ChromosomeCoordinates(refSeq, mutationNames.getNucleotideMutation());
    }

    public String getRefSeqWithNucleotideMutation() {
        return refSeqWithNucleotideMutation;
    }

    public String getIgnoreMe() {
        return "";
    }

    public String getChromosome() {
        return chromosomeCoordinates.getChromosome();
    }

    public String getStartPosition() {
        return String.valueOf(chromosomeCoordinates.getStartPostion());
    }

    public String getEndPosition() {
        return String.valueOf(chromosomeCoordinates.getEndPosition());
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
        if (mutationNames.getVariantClassification() != null) {
            return mutationNames.getVariantClassification().toString();
        } else {
            return "failed";
        }
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
                + getAaMutation() + "\t" + getValidated() + "\t" + getSampleName() + "\t" + getRefSeqWithNucleotideMutation();
    }

    static String[] headers = {"IgnoreMe", "Chromosome", "Start_Position", "End_Position", "Reference_Allele", "Tumor_Seq_Allele1", "Hugo_Symbol", "t_ref_count", "t_alt_count", "Variant_Classification", "HGVSp_Short", "Validation_Status", "Tumor_Sample_Barcode", "RefSeqNucleotideMutationInVal"};

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
