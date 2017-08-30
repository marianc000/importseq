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
public class AlleleFrequencyTest {

    public AlleleFrequencyTest() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testConstructor() {
        AlleleFrequency i = new AlleleFrequency("37.1%", "2430X");
        System.out.println(i);
        i = new AlleleFrequency("35.4%", "398X");
        assertEquals(i.getRefCount(), 257);
        assertEquals(i.getAltCount(), 141);

        //   System.out.println(i);
        i = new AlleleFrequency("37.1%", "2430X");
        System.out.println(i);
        i = new AlleleFrequency("18.7%", "685X");
        System.out.println(i);
        i = new AlleleFrequency("67.2%", "830X");
        assertEquals(i.getRefCount(), 272);
        assertEquals(i.getAltCount(), 558);

        //     System.out.println(i);
        i = new AlleleFrequency("16.7%", "1093X");
        System.out.println(i);
        i = new AlleleFrequency("54.5%", "246X");
        // System.out.println(i);
        assertEquals(i.getRefCount(), 112);
        assertEquals(i.getAltCount(), 134);
        assertTrue(true);
    }

}
