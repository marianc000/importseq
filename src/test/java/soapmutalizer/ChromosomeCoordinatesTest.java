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
    public void testExtractChromosomeFromResponse() {
        ChromosomeCoordinates i = new ChromosomeCoordinates();
        String r = i.extractChromosomeFromResponse("NC_000015.9:g.66727455G>T");
        assertEquals("15", r);

        r = i.extractChromosomeFromResponse("NC_000004.11:g.55972974T>A");
        assertEquals("4", r);

        r = i.extractChromosomeFromResponse("NC_000005.9:g.112175619delC");
        assertEquals("5", r);

    }

    @Test
    public void testNumberConversion() {
        ChromosomeCoordinates i = new ChromosomeCoordinates();
        String r = i.numberConversion("NM_002755.3:c.171G>T");
        System.out.println(r);
        assertEquals("NC_000015.9:g.66727455G>T", r);
        r = i.numberConversion("NM_002253.2:c.1416A>T");
        System.out.println(r);
        assertEquals("NC_000004.11:g.55972974T>A", r);
        r = i.numberConversion("NM_000038.4:c.4328delC");
        System.out.println(r);
        assertEquals("NC_000005.9:g.112175619delC", r);
        r = i.numberConversion("NM_023110.2:c.396_398dup");
        System.out.println(r);
        assertEquals("NC_000008.10:g.38285914_38285916dup", r);
    }

    @Test
    public void testExtractCoordinateFromResponse() {
        ChromosomeCoordinates i = new ChromosomeCoordinates();
        String r = i.extractCoordinateFromResponse("NC_000015.9:g.66727455G>T");
        System.out.println(r);
        assertEquals("66727455", r);
        r = i.extractCoordinateFromResponse("NC_000004.11:g.55972974T>A");
        System.out.println(r);
        assertEquals("55972974", r);
        r = i.extractCoordinateFromResponse("NC_000005.9:g.112175619delC");
        System.out.println(r);
        assertEquals("112175619", r);
        r = i.extractCoordinateFromResponse("NC_000008.10:g.38285914_38285916dup");
        System.out.println(r);
        assertEquals("38285914_38285916", r);
    }

    @Test
    public void testConstructor() {
        ChromosomeCoordinates i = new ChromosomeCoordinates("NM_002755.3", "c.171G>T");
        assertEquals(66727455, i.getStartPostion());
        assertEquals("15", i.getChromosome());

        i = new ChromosomeCoordinates("NM_002253.2", "c.1416A>T");
        assertEquals(55972974, i.getStartPostion());
        assertEquals("4", i.getChromosome());

        i = new ChromosomeCoordinates("NM_000038.4", "c.4328delC");
        assertEquals(112175619, i.getStartPostion());
        assertEquals("5", i.getChromosome());

        i = new ChromosomeCoordinates("NM_023110.2", "c.396_398dup");
        assertEquals(38285914, i.getStartPostion());
        assertEquals(38285916, i.getEndPosition());
        assertEquals("8", i.getChromosome());
    }
    //
//NC_000004.11:g.55972974T>A
//NC_000005.9:g.112175619delC
}
