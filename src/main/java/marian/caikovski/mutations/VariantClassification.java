/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package marian.caikovski.mutations;

/**
 *
 * @author mcaikovs
 */
public enum VariantClassification {
    // missense
    MISSENSE_MUTATION("Missense_Mutation"),//
    NONSENSE_MUTATION("Nonsense_Mutation"),//
    NONSTOP_MUTATION("Nonstop_Mutation"),//
    SPLICE_SITE("Splice_Site"), //
    // ins
    FRAME_SHIFT_DEL("Frame_Shift_Del"), //
    IN_FRAME_DEL("In_Frame_Del"),//
    // del
    FRAME_SHIFT_INS("Frame_Shift_Ins"),
    IN_FRAME_INS("In_Frame_Ins"); //

    String val;

    private VariantClassification(String val) {
        this.val = val;
    }

    @Override
    public String toString() {
        return val;
    }

}
