/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package files;

import java.nio.file.Paths;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author mcaikovs
 */
public class MySourceFileTest {
    
    public MySourceFileTest() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

     @Test
    public void testGetFileDir () {
         MySourceFile i = new MySourceFile("C:\\Projects\\cBioPortal\\data sample\\SECOND SAMPLES\\corrected\\H1703061-1A.chuv_val2.xlsx");
         
        assertEquals(i.getFileDir(),Paths.get("C:\\Projects\\cBioPortal\\data sample\\SECOND SAMPLES\\corrected"));
       

    }
}
