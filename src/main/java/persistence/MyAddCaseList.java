package persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Command Line tool to Add new case lists by generating them based on some
 * rules.
 */
public class MyAddCaseList {

    public Integer addSampleToList(Connection con, int cancerStudyId, int sampleId) throws SQLException {
        String sql = "INSERT INTO sample_list_list (`LIST_ID`, `SAMPLE_ID`)   VALUES (?,? )";
        int sampleListId = getListId(con, cancerStudyId);
        try (PreparedStatement pstmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, sampleListId);
            pstmt.setInt(2, sampleId);

            pstmt.executeUpdate();
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {

                return rs.getInt(1);
            }
        }
        return null;
    }

    public Integer getListId(Connection con, int cancerStudyId) throws SQLException {

        String sql = "SELECT LIST_ID FROM sample_list WHERE CANCER_STUDY_ID=?";

        try (PreparedStatement st = con.prepareStatement(sql)) {
            st.setInt(1, cancerStudyId);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            return null;
        }
    }
}
