/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package marian.caikovski.mutations;

import static marian.caikovski.mutations.VariantClassification.MISSENSE_MUTATION;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author mcaikovs
 */
public class VariantClassificationTest {

    public VariantClassificationTest() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testToString() {
        assertEquals(MISSENSE_MUTATION.toString(), "Missense_Mutation");
    }

}
