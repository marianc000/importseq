/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author mcaikovs
 */
public class MyCancerStudy {
        public static int getCancerStudyId(Connection con, String cancerStudyName) throws SQLException {

        String sql = "SELECT CANCER_STUDY_ID FROM cbioportal.cancer_study where CANCER_STUDY_IDENTIFIER=?";

        try (PreparedStatement st = con.prepareStatement(sql)) {

            st.setString(1, cancerStudyName);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                int cancerStudyId = rs.getInt(1);
                System.out.println("cancerStudyId: " + cancerStudyId);
                return cancerStudyId;
            }
            throw new SQLException("Marian: study id for study name " + cancerStudyName + " not found");
        }
    }
}
