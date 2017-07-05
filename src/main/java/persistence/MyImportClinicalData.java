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
import org.mskcc.cbio.portal.dao.DaoPatient;
import org.mskcc.cbio.portal.dao.DaoSample;
import org.mskcc.cbio.portal.dao.JdbcUtil;
import org.mskcc.cbio.portal.model.Sample;
import org.mskcc.cbio.portal.util.ProgressMonitor;

/**
 *
 * @author mcaikovs
 */
public class MyImportClinicalData {

    String STUDY_NAME = "acc_tcga_chuv2";
    String TYPE_OF_CANCER_ID = STUDY_NAME.substring(0, STUDY_NAME.indexOf("_"));

    public Integer getCancerStudyId(Connection con, String cancerStudyName) throws SQLException {

        String sql = "SELECT CANCER_STUDY_ID FROM cbioportal.cancer_study where CANCER_STUDY_IDENTIFIER=?";

        try (PreparedStatement st = con.prepareStatement(sql)) {

            st.setString(1, cancerStudyName);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            return null;
        }
    }

    public Integer getGeneticProfileId(Connection con, int cancerStudyId) throws SQLException {

        String sql = "SELECT GENETIC_PROFILE_ID FROM cbioportal.genetic_profile where CANCER_STUDY_ID= ?";

        try (PreparedStatement st = con.prepareStatement(sql)) {

            st.setInt(1, cancerStudyId);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            return null;
        }
    }

    public Integer getPatientInternalId(Connection con, int cancerStudyId, String stablePatientId) throws SQLException {

        String sql = "SELECT INTERNAL_ID  FROM cbioportal.patient where CANCER_STUDY_ID=? and STABLE_ID=?;";

        try (PreparedStatement st = con.prepareStatement(sql)) {
            st.setInt(1, cancerStudyId);
            st.setString(2, stablePatientId);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            return null;
        }
    }

    public boolean doesSampleIdExistInCancerStudy(Connection con, int cancerStudyId, String sampleId) throws SQLException {

        String sql = "SELECT * FROM  sample where PATIENT_ID in \n"
                + "(SELECT INTERNAL_ID FROM  patient where CANCER_STUDY_ID=?)\n"
                + "and STABLE_ID=?;";
        con.setAutoCommit(false);
        try (PreparedStatement st = con.prepareStatement(sql)) {
            st.setInt(1, cancerStudyId);
            st.setString(2, sampleId);
            ResultSet rs = st.executeQuery();
            return rs.next();
        }
    }

    public static Integer addPatient(Connection con, Integer cancerStudyId, String patientId) throws SQLException {

        String sql = "INSERT INTO patient (`STABLE_ID`, `CANCER_STUDY_ID`) VALUES (?,?)";
        try (PreparedStatement pstmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, patientId);
            pstmt.setInt(2, cancerStudyId);
            pstmt.executeUpdate();
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {

                return rs.getInt(1);
            }
            return null;
        }
    }

    public Integer addSample(Connection con, Integer cancerStudyId, Integer internalPatientId, String stableSampleId) throws SQLException {
        String sql = "INSERT INTO sample ( `STABLE_ID`, `SAMPLE_TYPE`, `PATIENT_ID`, `TYPE_OF_CANCER_ID` )  VALUES (?,?,?,?)";

        try (PreparedStatement pstmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, stableSampleId);
            pstmt.setString(2, Sample.Type.PRIMARY_SOLID_TUMOR.toString());
            pstmt.setInt(3, internalPatientId);
            pstmt.setString(4, TYPE_OF_CANCER_ID);
            pstmt.executeUpdate();
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {

                return rs.getInt(1);
            }

        }
        return null;
    }

    private boolean addSample(Connection con, String cancerStudyName, String stablePatientId, String stableSampleId) throws SQLException {

        Integer cancerStudyId = getCancerStudyId(con, cancerStudyName);

        Integer internalSampleId;
        Integer internalPatientId;

        //check if sample is not already added:
        if (doesSampleIdExistInCancerStudy(con, cancerStudyId, stableSampleId)) {
            System.out.println("sample already exist, skipping");
            return false;
        } else {
            internalPatientId = getPatientInternalId(con, cancerStudyId, stablePatientId);
            if (internalPatientId == null) {

                //add patient:
                internalPatientId = addPatient(con, cancerStudyId, stablePatientId);
            }
            // sample is new, so attempt to add to DB
            internalSampleId = addSample(con, cancerStudyId, internalPatientId, stableSampleId);
            return true;
        }
    }
}
