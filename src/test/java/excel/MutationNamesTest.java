/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package excel;

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
    public void testGetMutationsFromCell() {
        MutationNames i = new MutationNames("c.2626C>T\n (p.R876*)");

        System.out.println(i);
        assertEquals(i.getNucleotideMutation(), "c.2626C>T");
        assertEquals(i.getProteinMutation(), "p.R876*");
        assertTrue(i.getNucleotideMutationWithoutCoordinates(i.getNucleotideMutation()));
        
        
        i = new MutationNames("c.34G>A\n (p.G12S)");
        System.out.println(i);
        assertEquals(i.getNucleotideMutation(), "c.34G>A");
        assertEquals(i.getProteinMutation(), "p.G12S");

        i = new MutationNames("c.4328delC\n (p.P1443Lfs*30)");

        System.out.println(i);
        assertEquals(i.getNucleotideMutation(), "c.4328delC");
        assertEquals(i.getProteinMutation(), "p.P1443Lfs*30");

        i = new MutationNames("c.396_398dup\n (p.D133dup)");
        System.out.println(i);
        assertEquals(i.getNucleotideMutation(), "c.396_398dup");
        assertEquals(i.getProteinMutation(), "p.D133dup");

        i = new MutationNames("c.1024C>T\n (p.R342*)");
        System.out.println(i);
        assertEquals(i.getNucleotideMutation(), "c.1024C>T");
        assertEquals(i.getProteinMutation(), "p.R342*");

        i = new MutationNames("c.1621A>C\n (p.M541L)");
        System.out.println(i);
        assertEquals(i.getNucleotideMutation(), "c.1621A>C");
        assertEquals(i.getProteinMutation(), "p.M541L");

    }

}
