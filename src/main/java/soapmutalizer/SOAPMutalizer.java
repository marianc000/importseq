/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package soapmutalizer;

import auxiliary.MutalyzerService;
import auxiliary.StringArray;
import auxiliary.Mutalyzer;

/**
 *
 * @author mcaikovs
 */
public class SOAPMutalizer {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String r = new SOAPMutalizer().numberConversion("hg19", "NM_002755.3:c.171G>T");
        System.out.println(r);
    }

    public String numberConversion(String build, String variant) {

        Mutalyzer port = new MutalyzerService().getMutalyzer();
        return port.numberConversion(build, variant, null).getString().get(0);
    }

}
