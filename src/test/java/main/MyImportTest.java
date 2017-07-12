/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author mcaikovs
 */
public class MyImportTest {

    @Test
    public void testGetSampleName() throws IOException {
        String sourceFilePath = "C:\\Projects\\cBioPortal\\data sample\\Mix_cell-line13.hg19_coding01.Tab.xlsx";
        Path p = Paths.get(sourceFilePath);
        MyImport i = new MyImport(null);
        assertEquals(i.getSampleName(p), "Mix_cell-line13");
    }

}
