/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static utils.FileUtils.convertFilePathToSampleName;

/**
 *
 * @author mcaikovs
 */
public class FileUtilsTest {

    public FileUtilsTest() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testConvertFilePathToSampleName() {
        String s = "C:\\Projects\\cBioPortal\\data sample\\SECOND SAMPLES\\corrected\\H1703061-1A.hg19_coding01.Val.xlsx";
        assertEquals(convertFilePathToSampleName(s), "H1703061-1A");
    }

}
