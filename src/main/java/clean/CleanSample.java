/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clean;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import persistence.MyCancerStudy;
import static persistence.MyConnection.getConnection;
import persistence.MyImportClinicalData;

/**
 *
 * @author mcaikovs
 */
public class CleanSample {

    MyImportClinicalData cd;
    String[] sqls = {"delete FROM  sample where INTERNAL_ID in(select INTERNAL_ID from ("
        + "SELECT s.INTERNAL_ID FROM  sample s join patient p on s.PATIENT_ID=p.INTERNAL_ID "
        + "where cancer_study_id=" + STUDY_ID_PLACEHOLDER + " and p.stable_id='" + PATIENT_ID_PLACEHOLDER + "' and s.stable_id='" + SAMPLE_ID_PLACEHOLDER + "') a)"
    };
    static String STUDY_ID_PLACEHOLDER = "STUDY_ID_PLACEHOLDER", PATIENT_ID_PLACEHOLDER = "PATIENT_ID_PLACEHOLDER", SAMPLE_ID_PLACEHOLDER = "SAMPLE_ID_PLACEHOLDER";
// 10:53:58.775	DELETE FROM genetic_alteration WHERE GENETIC_PROFILE_ID IN (SELECT GENETIC_PROFILE_ID FROM genetic_profile WHERE CANCER_STUDY_ID=179)
// DELETE FROM genetic_profile_samples WHERE GENETIC_PROFILE_ID IN (SELECT GENETIC_PROFILE_ID FROM genetic_profile WHERE CANCER_STUDY_ID=179)
// DELETE FROM cancer_study WHERE CANCER_STUDY_ID=179;
//	

    public void cleanSample(Connection con, int studyId, String patientId, String sampleId) throws SQLException {
        System.out.println(">cleanSample: Removing sample: " + sampleId);
        for (String s : sqls) {
            String sql = s.replace(STUDY_ID_PLACEHOLDER, String.valueOf(studyId)).replace(PATIENT_ID_PLACEHOLDER, patientId).replace(SAMPLE_ID_PLACEHOLDER, sampleId);
            System.out.println(sql);
            try (PreparedStatement pstmt = con.prepareStatement(sql)) {
                System.out.println("Removed "+pstmt.executeUpdate()+" rows");
            }
        }
        System.out.println("<cleanSample: Removing sample: " + sampleId);
    }

    public void cleanSample(int studyId, String patientId, String sampleId) throws SQLException {
        try (Connection con = getConnection()) {
            con.setAutoCommit(false);
            cleanSample(con, studyId, patientId, sampleId);
            con.commit();
        }
    }

//
//    public static void main(String... args) throws IOException, InvalidFormatException, SQLException {
//        new CleanStudy().clean();
//    }
}
