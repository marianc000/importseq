package persistence;
 

import java.sql.*;
import java.util.*;

/**
 * Data Access Object for `clinical_attribute_meta` table
 *
 * @author Gideon Dresdner
 */
public class MyDaoClinicalAttributeMeta {

    public static int addDatum(Connection con, MyClinicalAttribute attr) throws SQLException {
        String sql = "INSERT INTO clinical_attribute_meta("
                + "`ATTR_ID`,"
                + "`DISPLAY_NAME`,"
                + "`DESCRIPTION`,"
                + "`DATATYPE`,"
                + "`PATIENT_ATTRIBUTE`,"
                + "`PRIORITY`,"
                + "`CANCER_STUDY_ID`)"
                + " VALUES(?,?,?,?,?,?,?)";

        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, attr.getAttrId());
            pstmt.setString(2, attr.getDisplayName());
            pstmt.setString(3, attr.getDescription());
            pstmt.setString(4, attr.getDatatype());
            pstmt.setBoolean(5, attr.isPatientAttribute());
            pstmt.setString(6, attr.getPriority());
            pstmt.setInt(7, attr.getCancerStudyId());
            return pstmt.executeUpdate();
        }
    }

 

    public static boolean doesAttributeExist(Connection con, String attrId, Integer cancerStudyId) throws SQLException {

        String sql = "SELECT * FROM clinical_attribute_meta WHERE ATTR_ID =?  AND CANCER_STUDY_ID=?";
        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, attrId);
            pstmt.setInt(2, cancerStudyId);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        }
    }
 
}
