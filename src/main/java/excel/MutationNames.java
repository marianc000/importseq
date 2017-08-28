/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package excel;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author mcaikovs
 */
public final class MutationNames {

    String proteinMutation, nucleotideMutation, refAllele, altAllele;

    public MutationNames(String proteinAndNucleotideMutation) {
        getMutationsFromCell(proteinAndNucleotideMutation);
    }

    public String getRefAllele() {
        return refAllele;
    }

    void setRefAltAlleles(String nucleotideMutation) {

    }

    public String getAltAllele() {
        return altAllele;
    }

    public String getProteinMutation() {
        return proteinMutation;
    }

    public String getNucleotideMutation() {
        return nucleotideMutation;
    }

    boolean getNucleotideMutationWithoutCoordinates(String s) {
        Matcher m = nucleotideMutationWithoutCoordinatesPattern.matcher(s);
        if (m.matches()) {
            String mutation = m.group(1);
            System.out.println(s + " matches: " + mutation);

            return true;
        }
        return false;
    }

    boolean getMutationsFromCell(String s) {
        Matcher m = p.matcher(s);
        if (m.matches()) {
            System.out.println("matches");
            this.proteinMutation = m.group(2);
            this.nucleotideMutation = m.group(1).trim();
            return true;
        }
        return false;
    }
    static Pattern p = Pattern.compile("(c\\..+)\\((.+)\\)", Pattern.DOTALL);
    static Pattern nucleotideMutationWithoutCoordinatesPattern = Pattern.compile("c\\.\\d+(\\D+)", Pattern.DOTALL);
   static Pattern missenseNucleotideMutationWithoutCoordinatesPattern = Pattern.compile("c\\.\\d+(\\D+)", Pattern.DOTALL);fdddddddddddddddddddddddddd
    @Override
    public String toString() {
        return "MutationNames{" + "proteinMutation=" + proteinMutation + ", nucleotideMutation=" + nucleotideMutation + '}';
    }

}
