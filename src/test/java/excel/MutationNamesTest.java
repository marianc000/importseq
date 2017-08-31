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
       assertNull(i.getNucleotideMutationWithoutCoordinates() );
        assertEquals(i.getRefAllele(), "");
        assertEquals(i.getAltAllele(), "");
        assertNull(i.getRefAA());
        assertNull(i.getAltAA());
        assertNull(i.getVariantClassification());

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
        assertEquals(i.getProteinMutation(), "Splicing");
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
        assertEquals(i.getProteinMutation(), "Splicing");
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

}
