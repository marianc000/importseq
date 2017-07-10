/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package persistence;

import java.sql.SQLException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.mskcc.cbio.portal.model.ExtendedMutation;
import static persistence.MyConnection.getConnection;
import static persistence.MyImportClinicalDataTest.con;

/**
 *
 * @author mcaikovs
 */
public class MyMutationEventDaoTest {
        @BeforeClass
    static public void beforeClass() throws SQLException {
        con = getConnection();
    }

    @After
    public void after() throws SQLException {

    }

    @Before
    public void before() throws SQLException {
 
    }

    @AfterClass
    static public void afterClass() throws SQLException {
        con.close();
    }

    @Test
    public void testGetMutationEvent() throws SQLException {
        //SELECT * FROM  mutation_event where ENTREZ_GENE_ID=2064 and CHR='17' and START_POSITION=37880235 and END_POSITION=37880235 and PROTEIN_CHANGE='S760fs' and TUMOR_SEQ_ALLELE='-' and MUTATION_TYPE='In_Frame_Del'
      ExtendedMutation.MutationEvent e= MyMutationEventDao.getMutationEvent(con, 2064, "17", 37880235, 37880235, "S760fs", "-", "In_Frame_Del");
      //  SELECT * FROM  mutation_event where 
     // ENTREZ_GENE_ID=2064 and CHR='17' and START_POSITION=37880235 
       //       and END_POSITION=37880235 and PROTEIN_CHANGE='S760fs' 
       //       and TUMOR_SEQ_ALLELE='-' and MUTATION_TYPE='In_Frame_Del'
       System.out.println(e);
       assertNotNull(e);
    }
    
}
