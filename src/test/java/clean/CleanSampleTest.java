/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clean;

import java.sql.SQLException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author mcaikovs
 */
public class CleanSampleTest {

    public CleanSampleTest() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testCleanSample() throws SQLException {
        CleanSample i = new CleanSample();
        i.cleanSample(173, "H19756", "H19756");
        assertTrue(true);
    }

}
