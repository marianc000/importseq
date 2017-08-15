/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package folder;

import java.io.IOException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author mcaikovs
 */
public class ExcelAdaptorTest {

    public ExcelAdaptorTest() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testSelectCanonicalTranscriptForGeneWithoutCanonicalTranscript() throws IOException {
        ExcelAdaptor a = new ExcelAdaptor(null);
        String s = "PTPN11:NM_002834:exon3:c.202delG:p.G68fs,PTPN11:NM_080601:exon3:c.202delG:p.G68fs";
        assertEquals(a.selectCanonicalTranscriptForGeneWithoutCanonicalTranscript(s), "p.G68fs");
        s = "PTPN11:NM_002834:exon3:c.202delG:p.G68fs,PTPN11:NM_080601:exon3:c.202delG:p.G69fs";
        assertNull(a.selectCanonicalTranscriptForGeneWithoutCanonicalTranscript(s));
    }

}
