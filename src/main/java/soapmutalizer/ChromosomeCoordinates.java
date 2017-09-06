/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package soapmutalizer;

import auxiliary.Mutalyzer;
import auxiliary.MutalyzerService;
import static excel.MutationNames.missenseNucleotideMutationWithoutCoordinatesPattern;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author mcaikovs
 */
public class ChromosomeCoordinates {

    public static int FAILED = 1;
    static String FAILED_CHROMOSOME = "NA";
    static Mutalyzer port = new MutalyzerService().getMutalyzer();
    //  NC_000004.11:g.55972974T>A
    String chromosome = FAILED_CHROMOSOME;
    int startPosition = FAILED, endPosition = FAILED;
    String refAllele = "", altAllele = "";

    public String getRefAllele() {
        return refAllele;
    }

    public String getAltAllele() {
        return altAllele;
    }

    public String getChromosome() {
        return chromosome;
    }

    public int getStartPostion() {
        return /*String.valueOf*/ (startPosition);
    }

    public final void setPosition(String val) {
        if (val.contains("_")) {
            String[] startEnd = val.split(Pattern.quote("_"));
            startPosition = Integer.valueOf(startEnd[0]);
            endPosition = Integer.valueOf(startEnd[1]);
        } else {
            startPosition = endPosition = Integer.valueOf(val);
        }
    }

    public int getEndPosition() {
        return /*String.valueOf*/ (endPosition);
    }

    public ChromosomeCoordinates() {
    }

    public static String combineRefSeqWithNucleotideMutation(String transcript, String nucleotideMutation) {
        return transcript + ":" + nucleotideMutation;
    }

    public static Pattern nucleotideMutationWithoutCoordinatesPattern = Pattern.compile("g\\.\\d+([-+_]\\d+)*(\\D+)"); // c.3405-2A>T, c.396_398dup

    String setNucleotideMutationWithoutCoordinates(String response) {
        Matcher m = nucleotideMutationWithoutCoordinatesPattern.matcher(response);
        if (m.matches()) {
            return m.group(2);
        }
        System.out.println("could not determine NucleotideMutationWithoutCoordinates for: " + response);
        return null;
    }

    void setRefAltAlleles(String mutation) {
        String nucleotideMutationWithoutCoordinates = setNucleotideMutationWithoutCoordinates(mutation);
        if (nucleotideMutationWithoutCoordinates != null) {
            setRefAltAlleles2(nucleotideMutationWithoutCoordinates);
        }
    }

    void setRefAltAlleles2(String nucleotideMutationWithoutCoordinates) {

        if (setRefAltAllelesForMissenseMutation(nucleotideMutationWithoutCoordinates)) {

        } else if (setRefAltAllelesForDeletion(nucleotideMutationWithoutCoordinates)) {

        } else if (setRefAltAllelesForDuplication(nucleotideMutationWithoutCoordinates)) {

        } else if (setRefAltAllelesForDeletionInsertion(nucleotideMutationWithoutCoordinates)) {

        }

    }

    boolean setRefAltAllelesForMissenseMutation(String nucleotideMutationWithoutCoordinates) {
        //  System.out.println(">setRefAltAllelesForMissenseMutation: " + getNucleotideMutationWithoutCoordinates());
        Matcher m = missenseNucleotideMutationWithoutCoordinatesPattern.matcher(nucleotideMutationWithoutCoordinates);
        if (m.matches()) {
            refAllele = m.group(1);
            altAllele = m.group(2);
            return true;
        }
        return false;
    }
    static Pattern deletionNucleotideMutationWithoutCoordinatesPattern = Pattern.compile("del([A-Z]*)");

    boolean setRefAltAllelesForDeletion(String nucleotideMutationWithoutCoordinates) {
        //  System.out.println("setRefAltAllelesForDeletion: " + getNucleotideMutationWithoutCoordinates());
        Matcher m = deletionNucleotideMutationWithoutCoordinatesPattern.matcher(nucleotideMutationWithoutCoordinates);
        if (m.matches()) {
            refAllele = m.group(1); // TODO:it can be null, call ref retriever
            altAllele = "-";
            return true;
        }
        return false;
    }
    static Pattern deletionInsertionNucleotideMutationWithoutCoordinatesPattern = Pattern.compile("delins([A-Z]*)");

    boolean setRefAltAllelesForDeletionInsertion(String nucleotideMutationWithoutCoordinates) {

        Matcher m = deletionInsertionNucleotideMutationWithoutCoordinatesPattern.matcher(nucleotideMutationWithoutCoordinates);
        if (m.matches()) {
            refAllele = "";
            altAllele = m.group(1);
            return true;
        }
        return false;
    }
    static Pattern duplicationNucleotideMutationWithoutCoordinatesPattern = Pattern.compile("dup([A-Z]*)");
    //  static String DUPLICATION_MARK = "dup";

    boolean setRefAltAllelesForDuplication(String nucleotideMutationWithoutCoordinates) {
        Matcher m = duplicationNucleotideMutationWithoutCoordinatesPattern.matcher(nucleotideMutationWithoutCoordinates);
        if (m.matches()) {
            refAllele = "-"; // TODO:it can be null, call ref retriever
            altAllele = m.group(1);

            return true;
        }
        return false;
    }

    public ChromosomeCoordinates(String transcript, String nucleotideMutation) {
        String refSeqWithNucleotideMutation = (combineRefSeqWithNucleotideMutation(transcript, nucleotideMutation));
        // System.out.println(">ChromosomeCoordinates: refSeqWithNucleotideMutation=" + refSeqWithNucleotideMutation);
        if (isMutationComplex(nucleotideMutation)) {
            System.out.println("<ChromosomeCoordinates: refSeqWithNucleotideMutation=" + refSeqWithNucleotideMutation + "; mutation is complex");
            return;
        }
        String response = numberConversion(refSeqWithNucleotideMutation);
        System.out.println(refSeqWithNucleotideMutation + "\t" + response);
        if (!response.isEmpty()) {
            // System.out.println(">>ChromosomeCoordinates: refSeqWithNucleotideMutation=" + refSeqWithNucleotideMutation + "; response=" + response);
            String[] vals = response.split(Pattern.quote(":"));
            String refSeq = vals[0];
            String mutation = vals[1];
            chromosome = extractChromosomeFromResponse(refSeq);
            setPosition(extractCoordinateFromNucleotideMutation(mutation));
            setRefAltAlleles(mutation);
        }
    }
//NC_000015.9:g.66727455G>T
//NC_000004.11:g.55972974T>A
//NC_000005.9:g.112175619delC

    final String numberConversion(String variant) {
        // System.out.println(">ChromosomeCoordinates:numberConversion variant=" + variant);
        return port.numberConversion("hg19", variant, null).getString().get(0);
    }

    final String extractChromosomeFromResponse(String val) {
        String r = val.split(Pattern.quote("."))[0].replace("NC_", "");
        return (r.replaceAll("^0+", ""));
    }
    Pattern numbersOnlyPattern = Pattern.compile("g\\.([0-9_]+)\\D+");

    final String extractCoordinateFromNucleotideMutation(String val) {

        Matcher m = numbersOnlyPattern.matcher(val);
        if (m.matches()) {
            //  System.out.println("matches");
            return (m.group(1));
        }
        throw new RuntimeException("Cannot extract coordinate from: " + val);
    }
    Pattern complexMutationPattern = Pattern.compile("c\\.\\[.+;.+\\]");

    final boolean isMutationComplex(String nucleotideMutation) { //c.[14A>G; 35G>A]
        //   System.out.println(">isMutationComplex: val=" + nucleotideMutation);

        Matcher m = complexMutationPattern.matcher(nucleotideMutation);
        return (m.matches());
    }
}
