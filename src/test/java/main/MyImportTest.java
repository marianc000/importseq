/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

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
    public void testGetSampleName() {
        Path p = Paths.get("C:\\Projects\\cBioPortal\\data sample\\Mix_cell-line.hg19_coding01.Tab.xlsx.out");
        MyImport i = new MyImport();
        assertEquals(i.getSampleName(p), "Mix_cell-line");
    }

}
