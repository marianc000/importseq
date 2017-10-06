/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package files;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.nio.file.Files;
import utils.FileUtils;

/**
 *
 * @author mcaikovs
 */
public class ValFileTest {

    public ValFileTest() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testSaveStackTrace() throws IOException {
        String tmpDir = System.getProperty("java.io.tmpdir");
        System.out.println(tmpDir);
        Path tmpDirPath = Paths.get(tmpDir);
        FileUtils fu = new FileUtils(tmpDirPath);
        Path testFilePath = tmpDirPath.resolve("error.xlsx");
        ValFile f = new ValFile(testFilePath);
        System.out.println(f);
        f.saveStackTrace(new RuntimeException("TEST EXCEPTION"));
        assertTrue(Files.exists(fu.getErrorFilePath(testFilePath)));
    }

}
