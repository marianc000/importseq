/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package excel;

import static excel.MutationNames.MAX_VALIDATION_STATUS_COLUMN_LENGTH;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import static persistence.MyMafUtil.MY_TEST;
import soapmutalizer.ChromosomeCoordinates;
import static soapmutalizer.ChromosomeCoordinates.combineRefSeqWithNucleotideMutation;
import utils.StringUtils;

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
    String fileName, comment = "";

    String sampleName;

    String refSeqWithNucleotideMutation, refSeqWithOriginalNucleotideProteinMutation="";

    public OutputMafRow() { // for tests
    }

    public String getComment() {
        return comment.replace('é','e').replace('ê','e');
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public OutputMafRow(String resultatInVal, String geneNameInVal, String sampleName, String alleleFrequencyStr, String coverage, String refSeq, String comment) {
        MutationNames mutationNames = new MutationNames(resultatInVal);
        refSeq = cleanRefSeq(refSeq);
        this.sampleName = sampleName;
        AlleleFrequency alleleFrequency = new AlleleFrequency(alleleFrequencyStr, coverage);
        refSeqWithNucleotideMutation = combineRefSeqWithNucleotideMutation(refSeq, mutationNames.getNucleotideMutation()); // save it to maf to have original data
        refSeqWithOriginalNucleotideProteinMutation = combineRefSeqWithNucleotideMutation(refSeq, mutationNames.getProteinAndNucleotideMutation());
        ChromosomeCoordinates chromosomeCoordinates = new ChromosomeCoordinates(refSeq, mutationNames.getNucleotideMutation());
        setComment(comment);

        init(chromosomeCoordinates.getChromosome(), chromosomeCoordinates.getStartPostion(), chromosomeCoordinates.getEndPosition(),
                mutationNames.getRefAllele(), mutationNames.getAltAllele(), geneNameInVal, alleleFrequency.getRefCountAsInt(), alleleFrequency.getAltCountAsInt(),
                mutationNames.getVariantClassification().toString(), mutationNames.getProteinMutation());
        //  System.out.println(this.toString());
    }

    final String cleanRefSeq(String refSeq) {
        return refSeq.replaceAll("\\*$", "");
    }

    public OutputMafRow(String chromosome, int startPosition, int endPosition, String refAllele, String tumorAllele, String geneName, int refCount, int altCount, String variantClassification, String aaMutation) {
        init(chromosome, startPosition, endPosition, refAllele, tumorAllele, geneName, refCount, altCount, variantClassification, aaMutation);
    }

    private void init(String chromosome, int startPosition, int endPosition, String refAllele, String tumorAllele, String geneName, int refCount, int altCount, String variantClassification, String aaMutation) {
        this.chromosome = chromosome;
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.refAllele = refAllele;
        this.tumorAllele = tumorAllele;
        this.geneName = geneName;
        this.refCount = refCount;
        this.altCount = altCount;
        this.variantClassification = variantClassification;
        this.aaMutation = aaMutation;
    }

    public String getRefSeqWithNucleotideMutation() {
        return refSeqWithNucleotideMutation;
    }

    public String getChromosome() {
        return chromosome;
    }

    public String getStartPosition() {
        return String.valueOf(startPosition);
    }

    public String getEndPosition() {
        return String.valueOf(endPosition);
    }

    public String getGeneName() {
        return geneName;
    }

    public String getRefAllele() {
        return refAllele; // TODO: it is not set for mutations other than substitions, it could be recovered from ncbi
    }

    public String getTumorAllele() {
        return tumorAllele;
    }

    public int getRefCount() {
        return refCount;
    }

    public int getAltCount() {
        return altCount;
    }

    public String getVariantClassification() {
        return variantClassification;
    }

    public String getAaMutation() { // HGVSp_Short
        return aaMutation;
    }

    public String getValidated() {
        //  return "Validated";
        // return refSeqWithOriginalNucleotideProteinMutation;
      //  System.out.println(">" + comment + "; " + comment.length()+"; "+comment.getBytes().length+"; "+new String (comment.getBytes(StandardCharsets.US_ASCII)).length());
        String r = StringUtils.truncate(getComment().replace("'", "''"), MAX_VALIDATION_STATUS_COLUMN_LENGTH-1);
      //  System.out.println("<r" + comment + "; " + r.length());
        return r;
    }
//Polymorphismes connus au sein de la population g�n�rale
    public String getFileName() {
        return fileName;
    }

    public String getSampleName() {
        return sampleName;
    }

    public String getRefSeqWithOriginalNucleotideProteinMutation() {
        return StringUtils.truncate(refSeqWithOriginalNucleotideProteinMutation, MAX_VALIDATION_STATUS_COLUMN_LENGTH);
    }

    public String toMafRow() {
        return getChromosome() + "\t" + getStartPosition() + "\t" + getEndPosition() + "\t" + getRefAllele() + "\t" + getTumorAllele()
                + "\t" + getGeneName() + "\t" + getRefCount() + "\t" + getAltCount() + "\t" + getVariantClassification() + "\t"
                + getAaMutation() + "\t" + getValidated() + "\t" + getSampleName() + "\t" + getRefSeqWithNucleotideMutation() + "\t" + getRefSeqWithOriginalNucleotideProteinMutation();
    }

    @Override
    public String toString() {
        return "OutputMafRow{" + "chromosome=" + chromosome + ", geneName=" + geneName + ", aaMutation=" + aaMutation + ", validated=" + validated + ", comment=" + getComment() + ", sampleName=" + sampleName + ", refSeqWithOriginalNucleotideProteinMutation=" + getRefSeqWithOriginalNucleotideProteinMutation() + '}';
    }

    static String[] headers = {"Chromosome", "Start_Position", "End_Position", "Reference_Allele", "Tumor_Seq_Allele1", "Hugo_Symbol", "t_ref_count", "t_alt_count", "Variant_Classification", "HGVSp_Short", "Validation_Status", "Tumor_Sample_Barcode", "RefSeqNucleotideMutationInVal", MY_TEST};

    static String getHeaders() {
        return String.join("\t", headers);
    }

    public static List<String> convertToRowList(Collection<OutputMafRow> l) {
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

    public static void printCollection(Collection<OutputMafRow> l, String title) {
        System.out.println(title);
        for (OutputMafRow row : l) {
            System.out.println(row.toMafRow());
        }
    }
}
