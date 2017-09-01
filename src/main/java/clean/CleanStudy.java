/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clean;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import static main.MyImportManyWithClinicalData.RRO_STUDY_NAME;
import persistence.MyCancerStudy;
import static persistence.MyConnection.getConnection;
import persistence.MyImportClinicalData;

/**
 *
 * @author mcaikovs
 */
public class CleanStudy {

    MyImportClinicalData cd;
    String[] sqls = {"DELETE FROM sample_list_list WHERE LIST_ID IN (SELECT LIST_ID FROM sample_list WHERE CANCER_STUDY_ID=179)",
        //  "DELETE FROM sample_list WHERE CANCER_STUDY_ID=179",
        "DELETE FROM mutation_count WHERE GENETIC_PROFILE_ID IN (SELECT GENETIC_PROFILE_ID FROM genetic_profile WHERE CANCER_STUDY_ID=179)",
        "DELETE FROM mutation WHERE GENETIC_PROFILE_ID IN (SELECT GENETIC_PROFILE_ID FROM genetic_profile WHERE CANCER_STUDY_ID=179)",
        "DELETE FROM sample_profile WHERE GENETIC_PROFILE_ID IN (SELECT GENETIC_PROFILE_ID FROM genetic_profile WHERE CANCER_STUDY_ID=179)",
        "DELETE FROM clinical_sample WHERE INTERNAL_ID IN (SELECT INTERNAL_ID FROM sample WHERE PATIENT_ID IN (SELECT INTERNAL_ID FROM patient WHERE CANCER_STUDY_ID=179))",
        "DELETE FROM sample WHERE PATIENT_ID IN (SELECT INTERNAL_ID FROM patient WHERE CANCER_STUDY_ID=179)",
        "DELETE FROM patient WHERE CANCER_STUDY_ID=179",
        "DELETE FROM clinical_attribute_meta WHERE CANCER_STUDY_ID=179",
        "DELETE FROM mutation_event WHERE NOT EXISTS (SELECT * FROM mutation WHERE mutation.MUTATION_EVENT_ID = mutation_event.MUTATION_EVENT_ID)"
    };

// 10:53:58.775	DELETE FROM genetic_alteration WHERE GENETIC_PROFILE_ID IN (SELECT GENETIC_PROFILE_ID FROM genetic_profile WHERE CANCER_STUDY_ID=179)
// DELETE FROM genetic_profile_samples WHERE GENETIC_PROFILE_ID IN (SELECT GENETIC_PROFILE_ID FROM genetic_profile WHERE CANCER_STUDY_ID=179)
// DELETE FROM cancer_study WHERE CANCER_STUDY_ID=179;
//	
    public void clean(String studyName) throws SQLException {
        try (Connection con = getConnection()) {
            con.setAutoCommit(false);
            int cancerStudyId = MyCancerStudy.getCancerStudyId(con, studyName);
            for (String s : sqls) {
                String sql = s.replaceAll("179", String.valueOf(cancerStudyId));
                System.out.println(sql);
                try (PreparedStatement pstmt = con.prepareStatement(sql)) {
                    System.out.println(pstmt.executeUpdate());
                }
            }
            con.commit();
        }
    }
    
 
//
//    public static void main(String... args) throws IOException, InvalidFormatException, SQLException {
//        new CleanStudy().clean();
//    }
}
