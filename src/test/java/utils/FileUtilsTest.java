/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import files.MySourceFile;
import files.ValFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static utils.FileUtils.ERROR_FILE_EXTENSION;
import static utils.FileUtils.REJECTED_FOLDER_NAME;
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

    @Test
    public void testGetErrorFilePath() throws IOException {
        String tmpDir = System.getProperty("java.io.tmpdir");
        System.out.println(tmpDir);
        Path tmpDirPath = Paths.get(tmpDir);
        FileUtils i = new FileUtils(tmpDirPath);
        String fileName = "error.xlsx";
        Path testFilePath = tmpDirPath.resolve(fileName);
        assertEquals(i.getErrorFilePath(testFilePath), tmpDirPath.resolve(REJECTED_FOLDER_NAME).resolve(fileName + ERROR_FILE_EXTENSION));
    }

    @Test
    public void testSaveStackTrace() throws IOException {
        String tmpDir = System.getProperty("java.io.tmpdir");
        System.out.println(tmpDir);
        Path tmpDirPath = Paths.get(tmpDir);
        Path testFilePath = tmpDirPath.resolve("error.xlsx");
        FileUtils i = new FileUtils(tmpDirPath);

        i.saveStackTrace(testFilePath, new RuntimeException("TEST EXCEPTION"));
        assertTrue(Files.exists(i.getErrorFilePath(testFilePath)));
    }
}
