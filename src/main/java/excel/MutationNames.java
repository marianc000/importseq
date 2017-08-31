/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package excel;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import marian.caikovski.mutations.VariantClassification;

/**
 *
 * @author mcaikovs
 */
public final class MutationNames {

    String proteinMutation, nucleotideMutation, refAllele="", altAllele="", refAA, altAA, nucleotideMutationWithoutCoordinates;

    VariantClassification variantClassification;

    MutationNames() { //for tests
    }

    public MutationNames(String proteinAndNucleotideMutation) {
        System.out.println(">MutationNames: proteinAndNucleotideMutation=" + proteinAndNucleotideMutation);
        setMutationsFromCell(proteinAndNucleotideMutation);

        if (setNucleotideMutationWithoutCoordinates()) {
            setRefAltAlleles();
        } else {
            System.out.println("could not determine NucleotideMutationWithoutCoordinates for: " + getNucleotideMutation());
        }
    }

    public VariantClassification getVariantClassification() {
        return variantClassification;
    }

    public String getRefAA() {
        return refAA;
    }

    public String getAltAA() {
        return altAA;
    }

    public String getRefAllele() {
        return refAllele;
    }

    void setRefAltAlleles() {
        if (setRefAltAllelesForMissenseMutation()) {
            return;
        }
        if (setRefAltAllelesForDeletion()) {
            return;
        }
        if (setRefAltAllelesForDuplication()) {
            return;
        }
    }

    public String getNucleotideMutationWithoutCoordinates() {
        return nucleotideMutationWithoutCoordinates;
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

    static Pattern nucleotideMutationWithoutCoordinatesPattern = Pattern.compile("c\\.\\d+([-+_]\\d+)*(\\D+)"); // c.3405-2A>T, c.396_398dup

    boolean setNucleotideMutationWithoutCoordinates() {
        Matcher m = nucleotideMutationWithoutCoordinatesPattern.matcher(getNucleotideMutation());
        if (m.matches()) {
            nucleotideMutationWithoutCoordinates = m.group(2);
            return true;
        }
        return false;
    }
    static Pattern aaChangeForMissenseNucleotideMutation = Pattern.compile("p\\.([A-Z])\\d+([*a-zA-Z].*)");
    static String SPLICING_AA_CHANGE_IN_VAL = "Splicing";

    boolean setAAChange() {
        System.out.println(">setAAChange: " + getProteinMutation());
        if (getProteinMutation().equals(SPLICING_AA_CHANGE_IN_VAL)) { //"c.3405-2A>T\n (Splicing)"
            return false;
        }
        Matcher m = aaChangeForMissenseNucleotideMutation.matcher(getProteinMutation());
        if (m.matches()) {
            refAA = m.group(1);
            altAA = m.group(2);
            return true;
        }
        throw new RuntimeException("cannot isolate aa change");
    }

    static Pattern missenseNucleotideMutationWithoutCoordinatesPattern = Pattern.compile("([A-Z])>([A-Z])");

    boolean setRefAltAllelesForMissenseMutation() {
        System.out.println(">setRefAltAllelesForMissenseMutation: " + getNucleotideMutationWithoutCoordinates());
        Matcher m = missenseNucleotideMutationWithoutCoordinatesPattern.matcher(getNucleotideMutationWithoutCoordinates());
        if (m.matches()) {
            refAllele = m.group(1);
            altAllele = m.group(2);
            setAAChange();
            if (getProteinMutation().equals(SPLICING_AA_CHANGE_IN_VAL)) {
                variantClassification = VariantClassification.SPLICE_SITE;
            } else if (getAltAA().equals("*") || getAltAA().equals("X")) {
                variantClassification = VariantClassification.NONSENSE_MUTATION;
            } else if (getRefAA().equals("*") || getRefAA().equals("X")) {
                variantClassification = VariantClassification.NONSTOP_MUTATION;
            } else {
                variantClassification = VariantClassification.MISSENSE_MUTATION;
            }
            return true;
        }
        return false;
    }
    static Pattern deletionNucleotideMutationWithoutCoordinatesPattern = Pattern.compile("del([A-Z]*)");
    static String FRAMESHIFT_MARK = "fs";

    boolean setRefAltAllelesForDeletion() {
        System.out.println("setRefAltAllelesForDeletion: " + getNucleotideMutationWithoutCoordinates());
        Matcher m = deletionNucleotideMutationWithoutCoordinatesPattern.matcher(getNucleotideMutationWithoutCoordinates());
        if (m.matches()) {
            refAllele = m.group(1); // TODO:it can be null, call ref retriever
            altAllele = "-";
            if (getProteinMutation().contains(FRAMESHIFT_MARK)) {
                variantClassification = VariantClassification.FRAME_SHIFT_DEL;
            } else {
                variantClassification = VariantClassification.IN_FRAME_DEL;
            }
            return true;
        }
        return false;
    }

    static Pattern duplicationNucleotideMutationWithoutCoordinatesPattern = Pattern.compile("dup([A-Z]*)");
    //  static String DUPLICATION_MARK = "dup";

    boolean setRefAltAllelesForDuplication() {
        Matcher m = duplicationNucleotideMutationWithoutCoordinatesPattern.matcher(getNucleotideMutationWithoutCoordinates());
        if (m.matches()) {
            refAllele = "-"; // TODO:it can be null, call ref retriever
            altAllele = m.group(1);
            if (getProteinMutation().contains(FRAMESHIFT_MARK)) {
                variantClassification = VariantClassification.FRAME_SHIFT_INS;
            } else {
                variantClassification = VariantClassification.IN_FRAME_INS;
            }
            return true;
        }
        return false;
    }

    static Pattern mutationCellInValPattern = Pattern.compile("(c\\..+)\\((.+)\\)", Pattern.DOTALL);
    static Pattern mutationCellInValPatternSpecial = Pattern.compile("(c\\..+) (p\\..+)", Pattern.DOTALL);

    boolean setMutationsFromCell(String s) {

        if (setMutationsFromCellHelper(mutationCellInValPattern, s)) {
            return true;
        }
// special case
        if (setMutationsFromCellHelper(mutationCellInValPatternSpecial, s)) {
            return true;
        }

        return false;
    }

    boolean setMutationsFromCellHelper(Pattern p, String s) {
        Matcher m = p.matcher(s);
        if (m.matches()) {
            this.proteinMutation = m.group(2);
            this.nucleotideMutation = m.group(1).trim();
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "MutationNames{" + "proteinMutation=" + proteinMutation + ", nucleotideMutation=" + nucleotideMutation + '}';
    }

}
