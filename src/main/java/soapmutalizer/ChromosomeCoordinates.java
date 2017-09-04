/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package soapmutalizer;

import auxiliary.Mutalyzer;
import auxiliary.MutalyzerService;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author mcaikovs
 */
public class ChromosomeCoordinates {

    public static int FAILED = 1;

    static Mutalyzer port = new MutalyzerService().getMutalyzer();
    //  NC_000004.11:g.55972974T>A
    String chromosome = String.valueOf(FAILED);
    int startPosition = FAILED, endPosition = FAILED;

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

    public ChromosomeCoordinates(String transcript, String nucleotideMutation) {
        String refSeqWithNucleotideMutation = (combineRefSeqWithNucleotideMutation(transcript, nucleotideMutation));
       // System.out.println(">ChromosomeCoordinates: refSeqWithNucleotideMutation=" + refSeqWithNucleotideMutation);
        if (isMutationComplex(nucleotideMutation)) {
             System.out.println("<ChromosomeCoordinates: refSeqWithNucleotideMutation=" + refSeqWithNucleotideMutation+"; mutation is complex");
            return;
        }
        String response = numberConversion(refSeqWithNucleotideMutation);
        if (!response.isEmpty()) {
          // System.out.println(">>ChromosomeCoordinates: refSeqWithNucleotideMutation=" + refSeqWithNucleotideMutation + "; response=" + response);
            chromosome = extractChromosomeFromResponse(response);
            setPosition(extractCoordinateFromResponse(response));
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
        String r = val.split(Pattern.quote(":"))[0];
        r = r.split(Pattern.quote("."))[0].replace("NC_", "");
        return (r.replaceAll("^0+", ""));
    }
    Pattern numbersOnlyPattern = Pattern.compile("g\\.([0-9_]+)\\D+");

    final String extractCoordinateFromResponse(String val) {
       //  System.out.println(">extractCoordinateFromResponse:val=" + val);
        String r = val.split(Pattern.quote(":"))[1];

        Matcher m = numbersOnlyPattern.matcher(r);
        if (m.matches()) {
            //  System.out.println("matches");
            return (m.group(1));
        }

        throw new RuntimeException("Cannot extract coordinate from: " + r+"; for: "+val);
    }
    Pattern complexMutationPattern = Pattern.compile("c\\.\\[.+;.+\\]");

    final boolean isMutationComplex(String nucleotideMutation) { //c.[14A>G; 35G>A]
     //   System.out.println(">isMutationComplex: val=" + nucleotideMutation);

        Matcher m = complexMutationPattern.matcher(nucleotideMutation);
        return (m.matches());
    }
}
