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

    @Test
    public void testTransformDeletionMutationName() throws IOException {
        ExcelAdaptor a = new ExcelAdaptor(null);
        String s = "p.D402_A403del";

        assertEquals(a.transformDeletionMutationName(s), "p.402_403del");
        s = "p.Q472H";
        assertEquals(a.transformDeletionMutationName(s), s);
        s = "p.H1047R";
        assertEquals(a.transformDeletionMutationName(s), s);
        s = "p.L252_I254del";
        assertEquals(a.transformDeletionMutationName(s), "p.252_254del");
    }

    @Test
    public void testTransformStopMutationName() throws IOException {
        ExcelAdaptor a = new ExcelAdaptor(null);
        String s = "p.R98X";

        assertEquals(a.transformStopMutationName(s), "p.R98*");
        s = "p.Q472H";
        assertEquals(a.transformStopMutationName(s), s);

    }
}
