/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package excel;

import marian.caikovski.mutations.VariantClassification;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author mcaikovs
 */
public class MutationNamesTest {

    public MutationNamesTest() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testNonsenseMutation() {
        MutationNames i = new MutationNames("c.2626C>T\n (p.R876*)");

        //System.out.println(i);
        assertEquals(i.getNucleotideMutation(), "c.2626C>T");
        assertEquals(i.getProteinMutation(), "p.R876*");
        assertEquals(i.getNucleotideMutationWithoutCoordinates(), "C>T");
        assertEquals(i.getRefAllele(), "C");
        assertEquals(i.getAltAllele(), "T");
        assertEquals(i.getRefAA(), "R");
        assertEquals(i.getAltAA(), "*");
        assertEquals(VariantClassification.NONSENSE_MUTATION, i.getVariantClassification());

        i = new MutationNames("c.1024C>T\n (p.R342*)");
        //System.out.println(i);
        assertEquals(i.getNucleotideMutation(), "c.1024C>T");
        assertEquals(i.getProteinMutation(), "p.R342*");
        assertEquals(i.getNucleotideMutationWithoutCoordinates(), "C>T");
        assertEquals(i.getRefAllele(), "C");
        assertEquals(i.getAltAllele(), "T");
        assertEquals(i.getRefAA(), "R");
        assertEquals(i.getAltAA(), "*");
        assertEquals(VariantClassification.NONSENSE_MUTATION, i.getVariantClassification());
    }

    @Test
    public void testMissenseMutation() {

        MutationNames i = new MutationNames("c.34G>A\n (p.G12S)");
        //System.out.println(i);
        assertEquals(i.getNucleotideMutation(), "c.34G>A");
        assertEquals(i.getProteinMutation(), "p.G12S");
        assertEquals(i.getNucleotideMutationWithoutCoordinates(), "G>A");
        assertEquals(i.getRefAllele(), "G");
        assertEquals(i.getAltAllele(), "A");
        assertEquals(i.getRefAA(), "G");
        assertEquals(i.getAltAA(), "S");
        assertEquals(VariantClassification.MISSENSE_MUTATION, i.getVariantClassification());

    }

    @Test
    public void testDoubleMissenseMutation() {
        MutationNames i = new MutationNames();
        assertTrue(i.setMutationsFromCell("c.[14A>G; 35G>A]\n p.[K5R;G12D]")); //H1710240-1A

//        MutationNames i = new MutationNames("c.[14A>G; 35G>A]\n p.[K5R;G12D]");
//        //System.out.println(i);
        assertEquals(i.getNucleotideMutation(), "c.[14A>G; 35G>A]");
        assertEquals(i.getProteinMutation(), "p.[K5R;G12D]");
        assertNull(i.getNucleotideMutationWithoutCoordinates());
        assertEquals(i.getRefAllele(), "");
        assertEquals(i.getAltAllele(), "");
        assertNull(i.getRefAA());
        assertNull(i.getAltAA());
        assertEquals(i.getVariantClassification(), VariantClassification.FAILED);

    }

    @Test
    public void testNonstopMutation() {

        MutationNames i = new MutationNames("c.885A>C\n (p.X295Y)");
        assertEquals(i.getRefAA(), "X");
        assertEquals(i.getAltAA(), "Y");
        assertEquals(VariantClassification.NONSTOP_MUTATION, i.getVariantClassification());

    }

    @Test
    public void testSplicingMutation() {

        MutationNames i = new MutationNames("c.3405-2A>T\n (Splicing)");
        //System.out.println(i);
        assertEquals(i.getNucleotideMutation(), "c.3405-2A>T");
        assertEquals(i.getProteinMutation(), "");
        assertEquals(i.getNucleotideMutationWithoutCoordinates(), "A>T");
        assertEquals(i.getRefAllele(), "A");
        assertEquals(i.getAltAllele(), "T");
        assertNull(i.getRefAA());
        assertNull(i.getAltAA());
        assertEquals(VariantClassification.SPLICE_SITE, i.getVariantClassification());

        i = new MutationNames("c.560-1G>A\n (Splicing)");
        assertEquals(i.getNucleotideMutation(), "c.560-1G>A");
        assertEquals(VariantClassification.SPLICE_SITE, i.getVariantClassification());

        i = new MutationNames("c.559+1G>A\n (Splicing)");
        //System.out.println(i);
        assertEquals(i.getNucleotideMutation(), "c.559+1G>A");
        assertEquals(i.getProteinMutation(), "");
        assertEquals(i.getNucleotideMutationWithoutCoordinates(), "G>A");
        assertEquals(i.getRefAllele(), "G");
        assertEquals(i.getAltAllele(), "A");
        assertNull(i.getRefAA());
        assertNull(i.getAltAA());
        assertEquals(VariantClassification.SPLICE_SITE, i.getVariantClassification());

    }

    @Test
    public void testFrameshiftDeletion() {

        MutationNames i = new MutationNames("c.4328delC\n (p.P1443Lfs*30)");
        assertEquals(i.getNucleotideMutation(), "c.4328delC");
        assertEquals(i.getProteinMutation(), "p.P1443Lfs*30");
        assertEquals(i.getNucleotideMutationWithoutCoordinates(), "delC");
        assertEquals(i.getRefAllele(), "C");
        assertEquals(i.getAltAllele(), "-");
        assertNull(i.getRefAA());
        assertNull(i.getAltAA());
        assertEquals(VariantClassification.FRAME_SHIFT_DEL, i.getVariantClassification());

        i = new MutationNames("c.4501del\n (p.S1501Lfs*6)");
        assertEquals(i.getNucleotideMutation(), "c.4501del");
        assertEquals(i.getProteinMutation(), "p.S1501Lfs*6");
        assertEquals(i.getNucleotideMutationWithoutCoordinates(), "del");
        assertEquals(i.getRefAllele(), "");
        assertEquals(i.getAltAllele(), "-");
        assertNull(i.getRefAA());
        assertNull(i.getAltAA());
        assertEquals(VariantClassification.FRAME_SHIFT_DEL, i.getVariantClassification());
    }

    @Test
    public void testInFrameDeletion() {
        MutationNames i = new MutationNames("c.754_762del\n (p.L252_I254del)");
        assertEquals(i.getNucleotideMutation(), "c.754_762del");
        assertEquals(i.getProteinMutation(), "p.L252_I254del");
        assertEquals(i.getNucleotideMutationWithoutCoordinates(), "del");
        assertEquals(i.getRefAllele(), "");
        assertEquals(i.getAltAllele(), "-");
        assertNull(i.getRefAA());
        assertNull(i.getAltAA());
        assertEquals(VariantClassification.IN_FRAME_DEL, i.getVariantClassification());

        i = new MutationNames("c.1203_1208del\n (p.D402_A403del)");
        assertEquals(VariantClassification.IN_FRAME_DEL, i.getVariantClassification());

        i = new MutationNames("c.532_534del\n (p.Y178del)");
        assertEquals(i.getNucleotideMutation(), "c.532_534del");
        assertEquals(i.getProteinMutation(), "p.Y178del");
        assertEquals(i.getNucleotideMutationWithoutCoordinates(), "del");
        assertEquals(i.getRefAllele(), "");
        assertEquals(i.getAltAllele(), "-");
        assertNull(i.getRefAA());
        assertNull(i.getAltAA());
        assertEquals(VariantClassification.IN_FRAME_DEL, i.getVariantClassification());
    }

    @Test
    public void testFrameshiftDeletionInsertion() {

        MutationNames i = new MutationNames("c.237_238delinsGT\n (p.R80*)");

        assertEquals(i.getNucleotideMutation(), "c.237_238delinsGT");
        assertEquals(i.getProteinMutation(), "p.R80*");
        assertEquals(i.getNucleotideMutationWithoutCoordinates(), "delinsGT");
        assertEquals(i.getRefAllele(), "");
        assertEquals(i.getAltAllele(), "GT");
        assertNull(i.getRefAA());
        assertNull(i.getAltAA());
        assertEquals(VariantClassification.FRAME_SHIFT_DEL, i.getVariantClassification());

        i = new MutationNames("c.166_176delinsG\n (p.S56Gfs*87)");

        assertEquals(i.getNucleotideMutation(), "c.166_176delinsG");
        assertEquals(i.getProteinMutation(), "p.S56Gfs*87");
        assertEquals(i.getNucleotideMutationWithoutCoordinates(), "delinsG");
        assertEquals(i.getRefAllele(), "");
        assertEquals(i.getAltAllele(), "G");
        assertNull(i.getRefAA());
        assertNull(i.getAltAA());
        assertEquals(VariantClassification.FRAME_SHIFT_DEL, i.getVariantClassification());

        i = new MutationNames("c.510_511delinsTT\n (p.E171*)");
        assertEquals(i.getNucleotideMutation(), "c.510_511delinsTT");
        assertEquals(i.getProteinMutation(), "p.E171*");
        assertEquals(i.getNucleotideMutationWithoutCoordinates(), "delinsTT");
        assertEquals(i.getRefAllele(), "");
        assertEquals(i.getAltAllele(), "TT");
        assertNull(i.getRefAA());
        assertNull(i.getAltAA());
        assertEquals(VariantClassification.FRAME_SHIFT_DEL, i.getVariantClassification());

        i = new MutationNames("c.286_308delinsCCTG\n (p.S96Pfs*21)");
        assertEquals(i.getNucleotideMutation(), "c.286_308delinsCCTG");
        assertEquals(i.getProteinMutation(), "p.S96Pfs*21");
        assertEquals(i.getNucleotideMutationWithoutCoordinates(), "delinsCCTG");
        assertEquals(i.getRefAllele(), "");
        assertEquals(i.getAltAllele(), "CCTG");
        assertNull(i.getRefAA());
        assertNull(i.getAltAA());
        assertEquals(VariantClassification.FRAME_SHIFT_DEL, i.getVariantClassification());
    }

    @Test
    public void testInFrameDeletionInsertion() {
        MutationNames i = new MutationNames("c.115_116delinsGC\n (p.R39A)");
        assertEquals(i.getNucleotideMutation(), "c.115_116delinsGC");
        assertEquals(i.getProteinMutation(), "p.R39A");
        assertEquals(i.getNucleotideMutationWithoutCoordinates(), "delinsGC");
        assertEquals(i.getRefAllele(), "");
        assertEquals(i.getAltAllele(), "GC");
        assertNull(i.getRefAA());
        assertNull(i.getAltAA());
        assertEquals(VariantClassification.IN_FRAME_DEL, i.getVariantClassification());

        i = new MutationNames("c.1798_1799delinsAA\n (p.V600K)");
        assertEquals(i.getNucleotideMutation(), "c.1798_1799delinsAA");
        assertEquals(i.getProteinMutation(), "p.V600K");
        assertEquals(i.getNucleotideMutationWithoutCoordinates(), "delinsAA");
        assertEquals(i.getRefAllele(), "");
        assertEquals(i.getAltAllele(), "AA");
        assertNull(i.getRefAA());
        assertNull(i.getAltAA());
        assertEquals(VariantClassification.IN_FRAME_DEL, i.getVariantClassification());
    }

    @Test
    public void testInFrameInsertion() {
        MutationNames i = new MutationNames("c.396_398dup\n (p.D133dup)");
        assertEquals(i.getNucleotideMutation(), "c.396_398dup");
        assertEquals(i.getProteinMutation(), "p.D133dup");
        assertEquals(i.getNucleotideMutationWithoutCoordinates(), "dup");
        assertEquals(i.getRefAllele(), "-");
        assertEquals(i.getAltAllele(), "");
        assertNull(i.getRefAA());
        assertNull(i.getAltAA());
        assertEquals(VariantClassification.IN_FRAME_INS, i.getVariantClassification());
    }

    @Test
    public void testFrameShiftInsertion() {
        MutationNames i = new MutationNames("c.340dup\n (p.Y114Lfs*7)");
        assertEquals(i.getNucleotideMutation(), "c.340dup");
        assertEquals(i.getProteinMutation(), "p.Y114Lfs*7");
        assertEquals(i.getNucleotideMutationWithoutCoordinates(), "dup");
        assertEquals(i.getRefAllele(), "-");
        assertEquals(i.getAltAllele(), "");
        assertNull(i.getRefAA());
        assertNull(i.getAltAA());
        assertEquals(VariantClassification.FRAME_SHIFT_INS, i.getVariantClassification());
    }

    @Test
    // changed length of mutation.validation_status in the database to 55
    public void testSetProteinAndNucleotideMutation() {
        MutationNames i = new MutationNames("c.115_116delinsGC\n (p.R39A)");
        assertEquals(i.getProteinAndNucleotideMutation(), "c.115_116delinsGC(p.R39A)");

        i = new MutationNames("c.1798_1799delinsAA\n (p.V600K)");
        assertEquals(i.getProteinAndNucleotideMutation(), "c.1798_1799delinsAA(p.V600K)");

        i.setProteinAndNucleotideMutation("1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890");
        assertEquals(i.getProteinAndNucleotideMutation(), "1234567890123456789012345678901234567890123456789012345");
    }

}
