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

    static Mutalyzer port = new MutalyzerService().getMutalyzer();
    //  NC_000004.11:g.55972974T>A
    String chromosome;
    int startPosition, endPosition;

    public String getChromosome() {
        return chromosome;
    }

    public int getStartPostion() {
        return startPosition;
    }

    public void setPosition(String val) {
        if (val.contains("_")) {
            String[] startEnd = val.split(Pattern.quote("_"));
            startPosition = Integer.valueOf(startEnd[0]);
            endPosition = Integer.valueOf(startEnd[1]);
        } else {
            startPosition = endPosition = Integer.valueOf(val);
        }
    }

    public int getEndPosition() {
        return endPosition;
    }

    public ChromosomeCoordinates() {
    }

    public ChromosomeCoordinates(String transcript, String nucleotideMutation) {
        String combined = transcript + ":" + nucleotideMutation;
        String response = numberConversion(combined);
        chromosome = extractChromosomeFromResponse(response);
        setPosition(extractCoordinateFromResponse(response));
    }
//NC_000015.9:g.66727455G>T
//NC_000004.11:g.55972974T>A
//NC_000005.9:g.112175619delC

    final String numberConversion(String variant) {
        return port.numberConversion("hg19", variant, null).getString().get(0);
    }

    final String extractChromosomeFromResponse(String val) {
        String r = val.split(Pattern.quote(":"))[0];
        r = r.split(Pattern.quote("."))[0].replace("NC_", "");
        return (r.replaceAll("^0+", ""));
    }
    Pattern numbersOnlyPattern = Pattern.compile("g\\.([0-9_]+)\\D+");

    final String extractCoordinateFromResponse(String val) {
        System.out.println("val=" + val);
        String r = val.split(Pattern.quote(":"))[1];

        Matcher m = numbersOnlyPattern.matcher(r);
        if (m.matches()) {
            System.out.println("matches");
            return (m.group(1));
        }

        throw new RuntimeException("Cannot extract coordinate from: " + r);
    }
}
