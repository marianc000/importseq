/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package persistence;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author mcaikovs
 */
public class MyImportClinicalData {

    public MyImportClinicalData(String studyName) {
        this.studyName = studyName;
    }

    String studyName;

    String getTypeOfCancerId() {
        return studyName.substring(0, studyName.indexOf("_"));
    }

    public int getCancerStudyId(Connection con, String cancerStudyName) throws SQLException {

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

    public int getGeneticProfileId(Connection con, int cancerStudyId) throws SQLException {
        String sql = "SELECT GENETIC_PROFILE_ID FROM cbioportal.genetic_profile where CANCER_STUDY_ID= ?";
        try (PreparedStatement st = con.prepareStatement(sql)) {
            st.setInt(1, cancerStudyId);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                int geneticProfileId = rs.getInt(1);
                System.out.println("geneticProfileId: " + geneticProfileId);
                return geneticProfileId;
            }
            throw new SQLException("geneticProfileId for cancerStudyId " + cancerStudyId + " not found");
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
            rs.next();
            return rs.getInt(1);
        }
    }

    public Integer addSample(Connection con, Integer cancerStudyId, Integer internalPatientId, String stableSampleId) throws SQLException {
        String sql = "INSERT INTO sample ( `STABLE_ID`, `SAMPLE_TYPE`, `PATIENT_ID`, `TYPE_OF_CANCER_ID` )  VALUES (?,?,?,?)";

        try (PreparedStatement pstmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, stableSampleId);
            pstmt.setString(2, MySample.Type.PRIMARY_SOLID_TUMOR.toString());
            pstmt.setInt(3, internalPatientId);
            pstmt.setString(4, getTypeOfCancerId());
            pstmt.executeUpdate();
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {

                return rs.getInt(1);
            }

        }
        return null;
    }

    public Integer addSample(Connection con, int cancerStudyId, String stablePatientId, String stableSampleId) throws SQLException {

        Integer internalSampleId;
        Integer internalPatientId;

        //check if sample is not already added:
        if (doesSampleIdExistInCancerStudy(con, cancerStudyId, stableSampleId)) {
            throw new RuntimeException("sample " + stableSampleId + " for patient" + stablePatientId + " already exist, skipping");
        } else {
            internalPatientId = getPatientInternalId(con, cancerStudyId, stablePatientId);

            if (internalPatientId == null) {
                System.out.println("adding patient:" + stablePatientId);
                internalPatientId = addPatient(con, cancerStudyId, stablePatientId);
            }
            System.out.println("internalPatientId: " + internalPatientId);

            internalSampleId = addSample(con, cancerStudyId, internalPatientId, stableSampleId);
            System.out.println("internalSampleId: " + internalSampleId);
            return internalSampleId;
        }
    }

    public static final String SAMPLE_ID_COLUMN_NAME = "SAMPLE_ID";
    public static final String PATIENT_ID_COLUMN_NAME = "PATIENT_ID";

    public List<MyClinicalAttribute> grabAttrs(Connection con, List<String> headers, int cancerStudyId) throws SQLException, IOException {
        List<MyClinicalAttribute> attrs = new ArrayList<>();

        for (String header : headers) {
            MyClinicalAttribute attr = new MyClinicalAttribute(header, header, header, "STRING", false, "1", cancerStudyId);
            attrs.add(attr);
//            //skip PATIENT_ID / SAMPLE_ID columns, i.e. these are not clinical attributes but relational columns:
//            if (attr.getAttrId().equals(PATIENT_ID_COLUMN_NAME) // TODO: skip nip and sample name
//                    || attr.getAttrId().equals(SAMPLE_ID_COLUMN_NAME)) {
//                continue;
//            }

            if (!MyDaoClinicalAttributeMeta.doesAttributeExist(con, header, cancerStudyId)) {
                MyDaoClinicalAttributeMeta.addDatum(con, attr);
            }
        }
        return attrs;
    }
}
