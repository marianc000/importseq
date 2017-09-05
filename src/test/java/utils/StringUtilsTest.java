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

/**
 *
 * @author mcaikovs
 */
public class StringUtilsTest {

    public StringUtilsTest() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testTruncate() {
        String s = "1234567890";
        assertEquals(StringUtils.truncate(s, 5), "12345");
        assertEquals(StringUtils.truncate(s, 15), s);
    }

}
