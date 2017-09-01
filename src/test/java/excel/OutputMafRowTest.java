/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package excel;

import org.junit.After;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author mcaikovs
 */
public class OutputMafRowTest {
    
    public OutputMafRowTest() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void testCleanRefSeq() {
        OutputMafRow  i=new OutputMafRow() ;
        assertEquals(i.cleanRefSeq("NM_058195.3*"),"NM_058195.3"); assertEquals(i.cleanRefSeq("NM_000455.4"),"NM_000455.4");
    }
    
}
