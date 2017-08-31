/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package soapmutalizer;

import java.util.regex.Pattern;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;
import static soapmutalizer.ChromosomeCoordinates.FAILED;

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

    @Ignore
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

    @Ignore
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

    @Ignore
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

    @Ignore
    @Test
    public void testConstructor() {
        ChromosomeCoordinates i = new ChromosomeCoordinates("NM_002755.3", "c.171G>T");
        assertEquals(66727455, i.getStartPostion());
        assertEquals(66727455, i.getEndPosition());
        assertEquals("15", i.getChromosome());

        i = new ChromosomeCoordinates("NM_002253.2", "c.1416A>T");
        assertEquals(55972974, i.getStartPostion());
        assertEquals(55972974, i.getEndPosition());
        assertEquals("4", i.getChromosome());

        i = new ChromosomeCoordinates("NM_000038.4", "c.4328delC");
        assertEquals(112175619, i.getStartPostion());
        assertEquals(112175619, i.getEndPosition());
        assertEquals("5", i.getChromosome());

        i = new ChromosomeCoordinates("NM_023110.2", "c.396_398dup");
        assertEquals(38285914, i.getStartPostion());
        assertEquals(38285916, i.getEndPosition());
        assertEquals("8", i.getChromosome());
    }

    @Ignore
    @Test
    public void testEmptyResponse() {
        ChromosomeCoordinates i = new ChromosomeCoordinates("NM_033360.3", "c.34G>A");
        assertEquals(FAILED, i.getStartPostion());
        assertEquals(String.valueOf(FAILED), i.getChromosome());
        assertEquals(FAILED, i.getEndPosition());
    }

    //@Ignore
    @Test
    public void testIsMutationComplex() {
        ChromosomeCoordinates i = new ChromosomeCoordinates();
        assertTrue(i.isMutationComplex("c.[14A>G; 35G>A]"));
        assertFalse(i.isMutationComplex("c.1513C>G"));
        assertFalse(i.isMutationComplex("c.237_238delinsGT"));
        assertFalse(i.isMutationComplex("c.1385G>C"));
    }

    // @Ignore
    @Test
    public void testAllValuesCanBeProcessed() {
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>testAllValuesCanBeProcessed: ");
        for (String s : input) {
            System.out.println(">processing: " + s);
            String[] refSeqWithNucleotideMutation = s.split(Pattern.quote(":"));
            ChromosomeCoordinates i = new ChromosomeCoordinates(refSeqWithNucleotideMutation[0], refSeqWithNucleotideMutation[1]);

            if (FAILED == (i.getStartPostion())) {
                System.out.println("XXX FAILED: " + s);
                assertEquals(String.valueOf(FAILED), i.getChromosome());
                assertEquals(FAILED, i.getEndPosition());
            }
        }
        System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<testAllValuesCanBeProcessed: ");
    }
    //
//NC_000004.11:g.55972974T>A
//NC_000005.9:g.112175619delC
    String[] input = {"NM_000249.3:c.1153C>T",
        "NM_002253.2:c.2935G>A",
        "NM_004333.4:c.1799T>A",
        "NM_000546.5:c.286_308delinsCCTG",
        "NM_000038.4:c.2626C>T",
        "NM_000546.5:c.730G>A",
        "NM_005359.5:c.340dup",
        "NM_020975.4:c.1942G>A",
        "NM_004333.4:c.1405G>A",
        "NM_000546.5:c.818G>T",
        "NM_006182.2:c.1323G>A",
        "NM_000321.2:c.947A>T",
        "NM_000546.5:c.853G>A",
        "NM_006218.2:c.1031T>G",
        "NM_006218.2:c.1173A>G",
        "NM_004333.4:c.1785T>G",
        "NM_000546.5:c.833C>T",
        "NM_000546.5:c.535C>T",
        "NM_000321.2:c.987del",
        "NM_000314.4:c.71A>T",
        "NM_004333.4:c.1798_1799delinsAA",
        "NM_005359.5:c.353C>T",
        "NM_005359.5:c.1157G>T",
        "NM_000455.4:c.975_976delinsTT",
        "NM_000546.5:c.733G>A",
        "NM_000077.4:c.166_176delinsG",
        "NM_000546.5:c.493C>T",
        "NM_005359.5:c.320dup",
        "NM_002524.3:c.155T>C",
        "NM_004333.4:c.1400C>T",
        "NM_033360.3:c.182A>G",
        "NM_000546.5:c.487T>C",
        "NM_000142.4:c.1150T>C",
        "NM_000455.4:c.598-2A>T",
        "NM_004333.4:c.1742A>G",
        "NM_006218.2:c.263G>A",
        "NM_000546.5:c.560-1G>A",
        "NM_000038.4:c.3949G>C",
        "NM_058195.3*:c.292C>T",
        "NM_001014432.1:c.49G>A",
        "NM_000546.5:c.1024C>T",
        "NM_002253.2:c.1416A>T",
        "NM_000051.3:c.1035G>T",
        "NM_000546.5:c.638G>A",
        "NM_000546.5:c.707A>G",
        "NM_000546.5:c.742C>T",
        "NM_005343.2:c.37G>C",
        "NM_033360.3:c.35G>T",
        "NM_000051.3:c.998C>T",
        "NM_005157.4:c.740A>G",
        "NM_006218.2:c.1624G>A",
        "NM_002524.3:c.182A>G",
        "NM_033360.3:c.35G>A",
        "NM_033360.3:c.35G>C",
        "NM_002253.2:c.794C>T",
        "NM_006218.2:c.3140A>G",
        "NM_000077.4:c.210del",
        "NM_000546.5:c.524G>A",
        "NM_000314.4:c.389G>A",
        "NM_020975.4:c.2302G>A",
        "NM_000546.5:c.731G>A",
        "NM_000546.5:c.581T>A",
        "NM_000038.4:c.4328delC",
        "NM_000222.2:c.1686G>C",
        "NM_000314.4:c.968dup",
        "NM_000321.2:c.1030C>T",
        "NM_005359.5:c.346C>T",
        "NM_000546.5:c.613T>A",
        "NM_000038.4:c.3920T>A",
        "NM_000038.4:c.3956del",
        "NM_000546.5:c.527G>T",
        "NM_000546.5:c.706T>C",
        "NM_001127500.1:c.3029C>T",
        "NM_033360.3:c.38G>A",
        "NM_000051.3:c.1229T>C",
        "NM_006218.2:c.323G>T",
        "NM_000546.5:c.586C>T",
        "NM_000546.5:c.584T>C",
        "NM_000546.5:c.844C>T",
        "NM_000455.4:c.595del",
        "NM_002524.3:c.181C>A",
        "NM_000038.4:c.3927_3931del",
        "NM_000546.5:c.578A>T",
        "NM_001904.3:c.134C>T",
        "NM_000546.5:c.734G>A",
        "NM_004119.2:c.1781T>C",
        "NM_005373.2:c.1547A>T",
        "NM_000546.5:c.754_762del",
        "NM_000546.5:c.468_469del",
        "NM_004119.2:c.1385G>C",
        "NM_005228.3:c.326G>A",
        "NM_002067.2:c.626A>T",
        "NM_002524.3:c.35G>A",
        "NM_000546.5:c.510_511delinsTT",
        "NM_006218.2:c.1035T>A",
        "NM_000546.5:c.722C>T",
        "NM_000546.5:c.215C>G",
        "NM_000038.4:c.4277del",
        "NM_000077.4:c.250G>A",
        "NM_000077.4:c.296G>C",
        "NM_000222.2:c.1621A>C",
        "NM_004304.3:c.3617C>T",
        "NM_000314.4:c.376G>A",
        "NM_000546.5:c.743G>A",
        "NM_004448.3:c.2617G>C",
        "NM_000051.3:c.1814A>G",
        "NM_000038.4:c.4033G>T",
        "NM_000455.4:c.115_116delinsGC",
        "NM_000546.5:c.400T>C",
        "NM_006218.2:c.1633G>A",
        "NM_002834.3:c.226G>A",
        "NM_000546.5:c.839G>A",
        "NM_006218.2:c.344G>C",
        "NM_000546.5:c.916C>T",
        "NM_000077.4:c.341C>T",
        "NM_002253.2:c.3405-2A>T",
        "NM_033360.3:c.436G>A",
        "NM_005631.4 :c.580G>A",
        "NM_000546.5:c.559+1G>A",
        "NM_002755.3:c.171G>T",
        "NM_000321.2:c.1727C>G",
        "NM_000314.4:c.373A>T",
        "NM_000546.5:c.528C>G",
        "NM_000077.4:c.237_238delinsGT",
        "NM_001127500.1:c.1124A>G",
        "NM_033632.3:c.1513C>G",
        "NM_004360.3:c.1203_1208del",
        "NM_002524.3:c.[14A>G; 35G>A]",
        "NM_005896.3:c.394C>T",
        "NM_033360.3:c.34G>C",
        "NM_000546.5:c.749C>T",
        "NM_000546.5:c.821T>C",
        "NM_033360.3:c.34G>A",
        "NM_023110.2:c.396_398dup",
        "NM_000077.4:c.247C>T",
        "NM_002524.3:c.38G>T",
        "NM_000051.3:c.5188C>T",
        "NM_000455.4:c.587G>T",
        "NM_000546.5:c.817C>T",
        "NM_000077.4:c.179C>A",
        "NM_022970.3:c.755C>G",
        "NM_000051.3:c.1009C>T",
        "NM_000038.4:c.3340C>T",
        "NM_033360.3:c.34G>T",
        "NM_001014432.1:c.57C>G",
        "NM_000314.4:c.532_534del",
        "NM_000546.5:c.523C>G",
        "NM_000546.5:c.716_718dup",
        "NM_000051.3:c.5071A>C",
        "NM_000314.4:c.388C>G",
        "NM_006182.2:c.383G>A"};
}
