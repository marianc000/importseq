/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package persistence;

import java.sql.Connection;
import java.sql.SQLException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import static persistence.MyConnection.getConnection;

/**
 *
 * @author mcaikovs
 */
public class MyImportClinicalDataTest {

    public MyImportClinicalDataTest() {
    }

    MyImportClinicalData i;
    String STUDY_NAME = "acc_tcga_chuv2";
    String SAMPLE_NAME = "H1975";
    String PATIENT_NAME = SAMPLE_NAME;
    static Connection con;

    @BeforeClass
    static public void beforeClass() throws SQLException {
        con = getConnection();
    }

    @After
    public void after() throws SQLException {

    }

    @Before
    public void before() throws SQLException {
        i = new MyImportClinicalData(STUDY_NAME);
    }

    @AfterClass
    static public void afterClass() throws SQLException {
        con.close();
    }

    @Test
    public void testGetCancerStudyId() throws SQLException {
        Integer id = i.getCancerStudyId(con, STUDY_NAME);
        System.out.println(id);
        assertNotNull(id);
    }

    @Test
    public void testGetGeneticProfileId() throws SQLException {
        Integer id = i.getCancerStudyId(con, STUDY_NAME);
        Integer pid = i.getGeneticProfileId(con, id);
        System.out.println(pid);
        assertNotNull(id);
    }

    @Test
    public void testDoesSampleIdExistInCancerStudy() throws SQLException {
        Integer id = i.getCancerStudyId(con, STUDY_NAME);
        assertTrue(i.doesSampleIdExistInCancerStudy(con, id, SAMPLE_NAME));
        assertFalse(i.doesSampleIdExistInCancerStudy(con, id, "gggggggggg"));
    }

    @Test
    public void testGetPatientInternalId() throws SQLException {
        Integer id = i.getCancerStudyId(con, STUDY_NAME);
        Integer patientId = i.getPatientInternalId(con, id, PATIENT_NAME);
        System.out.println(patientId);
        assertNotNull(patientId);
        assertNull(i.getPatientInternalId(con, id, "gsfgsdgsdfg"));
    }

    @Test
    public void testTypeOfCancer() throws SQLException {

        assertEquals(i.getTypeOfCancerId(), "acc");
    }

}
