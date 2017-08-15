/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package excel;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author mcaikovs
 */
public class LoadValTest {

    public LoadValTest() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testSomeMethod() {
        LoadVal i = new LoadVal();
        String s = "c.171G>T\n (p.K57N)";
        assertEquals(i.getMutationFromCell(s), "p.K57N");
        s = "c.818G>T\n (p.R273L)";
        assertEquals(i.getMutationFromCell(s), "p.R273L");
        s = "c.215C>G\n (p.P72R)";
        assertEquals(i.getMutationFromCell(s), "p.P72R");
     
    }

}
