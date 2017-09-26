/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package files;

import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author mcaikovs
 */
public class ClinicalDataFileTest {

    public ClinicalDataFileTest() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testGetStudyNameFromFileName() {
        ClinicalDataFile i = new ClinicalDataFile("dddd");
        String r = i.getStudyNameFromFileName("H1703061-1A.chuv_val2.xlsx");
        assertEquals(r, "chuv_val2");
        r = i.getStudyNameFromFileName("H1708135-1A.chuv_val.clinical.xlsx");
        assertEquals(r, "chuv_val");

    }

    @Test
    public void testGetStudyName() {
        ClinicalDataFile i = new ClinicalDataFile("gggg");
        String r = i.getStudyName("C:\\Projects\\cBioPortal\\data sample\\SECOND SAMPLES\\corrected\\H1703061-1A.chuv_val2.xlsx");
        assertEquals(r, "chuv_val2");
        r = i.getStudyName("C:\\Projects\\cBioPortal\\data sample\\SECOND SAMPLES\\corrected\\H1708135-1A.chuv_val.clinical.xlsx");
        assertEquals(r, "chuv_val");

    }
}
