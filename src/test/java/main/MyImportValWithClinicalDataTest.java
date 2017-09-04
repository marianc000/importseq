/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author mcaikovs
 */
public class MyImportValWithClinicalDataTest {
    
    public MyImportValWithClinicalDataTest() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void testRemoveLeadingZeros() {
        MyImportValWithClinicalData i = new MyImportValWithClinicalData();
        assertEquals(i.removeLeadingZeros("0003128042"), "3128042");
        assertEquals(i.removeLeadingZeros("0000076823"), "76823");
        assertEquals(i.removeLeadingZeros("0000803120"), "803120");
        assertEquals(i.removeLeadingZeros("unknown1"), "unknown1");

    }
}
