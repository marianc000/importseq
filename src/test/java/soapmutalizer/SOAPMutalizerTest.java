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
public class SOAPMutalizerTest {

    public SOAPMutalizerTest() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testNumberConversion() {
        SOAPMutalizer i = new SOAPMutalizer();
        String r = i.numberConversion("hg19", "NM_002755.3:c.171G>T");
        System.out.println(r);
        assertEquals("NC_000015.9:g.66727455G>T", r);
        r = i.numberConversion("hg19", "NM_002253.2:c.1416A>T");
        System.out.println(r);
        assertEquals("NC_000004.11:g.55972974T>A", r);
        r = i.numberConversion("hg19", "NM_000038.4:c.4328delC");
        System.out.println(r);
        assertEquals("NC_000005.9:g.112175619delC", r);
    }

}
