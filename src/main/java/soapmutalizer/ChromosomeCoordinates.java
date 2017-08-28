/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package soapmutalizer;

import java.util.regex.Pattern;

/**
 *
 * @author mcaikovs
 */
public class ChromosomeCoordinates {

    //  NC_000004.11:g.55972974T>A
    int chromosome;
    int coordinate;
    String nucleotideChange;
    String sourceString;

    public ChromosomeCoordinates(String transcript, String nucleotideMutation ) {
         String combined=transcript+":"+nucleotideMutation;

    }

 
    int extractChromosomeFromSequenceName(String name) {
        //NC_000004.11 
        String r = name.split(Pattern.quote("."))[0].replace("NC_", "");
        return Integer.valueOf(r);
    }
}
