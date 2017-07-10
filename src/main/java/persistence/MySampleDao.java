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
import java.sql.Statement;
import org.mskcc.cbio.portal.dao.DaoException;
import org.mskcc.cbio.portal.model.Sample;

/**
 *
 * @author mcaikovs
 */
public class MySampleDao {

    public static Sample getSampleById(Connection con, int sampleId) throws SQLException {
        String sql = "SELECT * FROM  sample where INTERNAL_ID=?";
        try (PreparedStatement st = con.prepareStatement(sql)) {
            st.setInt(1, sampleId);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                Sample s = new Sample(rs.getInt("INTERNAL_ID"),
                        rs.getString("STABLE_ID"),
                        rs.getInt("PATIENT_ID"),
                        rs.getString("TYPE_OF_CANCER_ID"));

                System.out.println("sample: " + s);
                return s;
            }
            throw new SQLException("geneticProfileId for cancerStudyId " + sampleId + " not found");
        }
    }

    public static Integer addPatient(Connection con, Integer cancerStudyId, String patientId) throws SQLException {

        String sql = "INSERT INTO patient (`STABLE_ID`, `CANCER_STUDY_ID`) VALUES (?,?)";
        try (PreparedStatement pstmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, patientId);
            pstmt.setInt(2, cancerStudyId);
            pstmt.executeUpdate();
            ResultSet rs = pstmt.getGeneratedKeys();
            rs.next();

            return rs.getInt(1);

        }
    }

    public static int addSampleProfile(Connection con, int sampleId, int geneticProfileId) throws DaoException, SQLException {
      //  getSampleProfileBySampleById(con, sampleId);
        String sql = "INSERT INTO sample_profile (`SAMPLE_ID`, `GENETIC_PROFILE_ID` ) VALUES (?,? )";
        try (PreparedStatement pstmt = con.prepareStatement(sql )) {

            pstmt.setInt(1, sampleId);
            pstmt.setInt(2, geneticProfileId);
 
            return pstmt.executeUpdate();

        }
    }

    public static boolean getSampleProfileBySampleById(Connection con, int sampleId) throws SQLException {
        String sql = "SELECT * FROM  sample_profile where SAMPLE_ID=?";
        try (PreparedStatement st = con.prepareStatement(sql)) {
            st.setInt(1, sampleId);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                System.out.println("GENETIC_PROFILE_ID: " + rs.getInt("GENETIC_PROFILE_ID"));
                throw new SQLException("SampleProfileId for sampleId " + sampleId + "  found");
            }
            return false;
        }
    }
}
