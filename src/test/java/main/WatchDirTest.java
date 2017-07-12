/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

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
public class WatchDirTest {

    public WatchDirTest() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testIsAcceptable() {
        String s = "H:\\cbioportal_import\\Mix_cell-line.hg19_coding01.Tab.xlsx";
        Path p = Paths.get(s);
        assertTrue(WatchDir.isAcceptable(p));
        s = "H:\\cbioportal_import\\Mix_cell-line.hg19_coding01.Tab.xlsx.out";
        p = Paths.get(s);
        assertFalse(WatchDir.isAcceptable(p));
    }

}
