/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package soapmutalizer;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author mcaikovs
 */
public class ChromosomeCoordinatesTest {

    public ChromosomeCoordinatesTest() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testExtractChromosomeFromSequenceName() {
        ChromosomeCoordinates i = new ChromosomeCoordinates(null, null);
        int r = i.extractChromosomeFromSequenceName("NC_000015.9");
        assertEquals(15, r);

        r = i.extractChromosomeFromSequenceName("NC_000004.11");
        assertEquals(4, r);

        r = i.extractChromosomeFromSequenceName("NC_000005.9");
        assertEquals(5, r);

    }

}
