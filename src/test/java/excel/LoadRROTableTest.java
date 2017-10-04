/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package excel;

import static excel.LoadRROTable.VALUE_SEPARATOR;
import java.util.Arrays;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author mcaikovs
 */
public class LoadRROTableTest {

    public LoadRROTableTest() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testMergeTwoRows() {
        LoadRROTable i = new LoadRROTable();
        List<String> row1 = Arrays.asList("aaa", "bbb", "ccc");
        List<String> row2 = Arrays.asList("aaa1", "bbb1", "ccc1");
        List<String> row = i.mergeTwoRows(row1, row2);
        assertEquals(row, Arrays.asList("aaa" + VALUE_SEPARATOR + "aaa1", "bbb" + VALUE_SEPARATOR + "bbb1", "ccc" + VALUE_SEPARATOR + "ccc1"));

        row1 = Arrays.asList("aaa", "bbb", "ccc");
        row2 = Arrays.asList("aaa", "bbb1", "ccc");
        row = i.mergeTwoRows(row1, row2);
        assertEquals(row, Arrays.asList("aaa", "bbb" + VALUE_SEPARATOR + "bbb1", "ccc"));

        row1 = Arrays.asList("", "bbb", "ccc");
        row2 = Arrays.asList("aaa1", "bbb1", "ccc");
        row = i.mergeTwoRows(row1, row2);
        assertEquals(row, Arrays.asList("aaa1", "bbb" + VALUE_SEPARATOR + "bbb1", "ccc"));

        row1 = Arrays.asList("aaa", "bbb", "ccc");
        row2 = Arrays.asList("", "bbb1", "ccc");
        row = i.mergeTwoRows(row1, row2);
        assertEquals(row, Arrays.asList("aaa", "bbb" + VALUE_SEPARATOR + "bbb1", "ccc"));
    }

    @Test
    public void extractAncientReference() {
        LoadRROTable i = new LoadRROTable();
        String s = "H1610768-1A (ancienne réf. IPA H1600052-1A)";
        String r = i.extractAncientReference(s);
        System.out.println(r);
        assertEquals(r, "IPA H1600052-1A");
        s = "H1616393-1A (ancienne réf. IPA H0903652-1A)";
        r = i.extractAncientReference(s);
        System.out.println(r);
        assertEquals(r, "IPA H0903652-1A");
        s = "H1616791-1A (réf. ARGOT LAB : P7819.15)";
        r = i.extractAncientReference(s);
        System.out.println(r);
        assertNull(r);
    }

    @Test
    public void extractExternalReference() {
        LoadRROTable i = new LoadRROTable();
        String s = "H1610768-1A (ancienne réf. IPA H1600052-1A)";
        String r = i.extractExternalReference(s);
        System.out.println(r);
        assertNull(r);
        s = "H1707522-1A (réf. PROMED : P830/16-F)";
        r = i.extractExternalReference(s);
        System.out.println(r);
        assertEquals(r, "PROMED : P830/16-F");
        s = "H1616791-1A (réf. ARGOT LAB : P7819.15)";
        r = i.extractExternalReference(s);
        System.out.println(r);
        assertEquals(r, "ARGOT LAB : P7819.15");
    }
}
