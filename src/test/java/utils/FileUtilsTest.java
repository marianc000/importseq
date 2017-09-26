/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import files.MySourceFile;
import java.nio.file.Paths;
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

    @Test
    public void testGetFileDir() {
        FileUtils i = new FileUtils(Paths.get("C:\\Projects\\cBioPortal\\data sample\\SECOND SAMPLES\\corrected"));

        assertEquals(i.getImported(), Paths.get("C:\\Projects\\cBioPortal\\data sample\\SECOND SAMPLES\\corrected\\imported"));
        assertEquals(i.getRejected(), Paths.get("C:\\Projects\\cBioPortal\\data sample\\SECOND SAMPLES\\corrected\\rejected"));

    }
}
