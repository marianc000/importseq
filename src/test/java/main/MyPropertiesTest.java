/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;

/**
 *
 * @author mcaikovs
 */
public class MyPropertiesTest {

    public MyPropertiesTest() {
    }
    MyProperties i;

    @Before
    public void setUp() throws IOException {
        i = new MyProperties();
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testAllKeysAreLoaded() {
       // String s = i.getImportedDir();
      //  System.out.println(s);
      //  assertNotNull(s);
     //   s = i.getRejectedDir();
      //  System.out.println(s);
      //  assertNotNull(s);
      String  s = i.getSourceDir();
        System.out.println(s);
        assertNotNull(s);

    }

 @Ignore   @Test
    public void testGetSourceFilePath() {
        String fileName = "Mix_cell-line43.hg19_coding01.Tab.xlsx";
        String s = i.getSourceDir() + File.separator + fileName;
        System.out.println(s);
      //  assertEquals(i.getSourceFilePath(fileName), Paths.get(s));
    }
}
