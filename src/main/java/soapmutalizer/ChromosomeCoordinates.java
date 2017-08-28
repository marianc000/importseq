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
    int chromosome;
    int coordinate;

    public int getChromosome() {
        return chromosome;
    }

    public int getCoordinate() {
        return coordinate;
    }
 
    public ChromosomeCoordinates() {
    }

    public ChromosomeCoordinates(String transcript, String nucleotideMutation) {
        String combined = transcript + ":" + nucleotideMutation;
        String response = numberConversion(combined);
        this.chromosome=extractChromosomeFromResponse(response);
        this.coordinate=extractCoordinateFromResponse(response);
    }
//NC_000015.9:g.66727455G>T
//NC_000004.11:g.55972974T>A
//NC_000005.9:g.112175619delC

    final String numberConversion(String variant) {
        return port.numberConversion("hg19", variant, null).getString().get(0);
    }

    final int extractChromosomeFromResponse(String val) {
        String r = val.split(Pattern.quote(":"))[0];
        r = r.split(Pattern.quote("."))[0].replace("NC_", "");
        return Integer.valueOf(r);
    }
    Pattern numbersOnlyPattern = Pattern.compile("g\\.(\\d+)\\D+");

    final int extractCoordinateFromResponse(String val) {
        System.out.println("val=" + val);
        String r = val.split(Pattern.quote(":"))[1];

        Matcher m = numbersOnlyPattern.matcher(r);
        if (m.matches()) {
            System.out.println("matches");
            return Integer.valueOf(m.group(1));
        }

        throw new RuntimeException("Cannot extract coordinate from: " + r);
    }
}
