package persistence;


import java.sql.*;

/**
 * Data Access Object for `clinical` table
 *
 * @author Gideon Dresdner dresdnerg@cbio.mskcc.org
 */
public final class MyDaoClinicalData {

    public static void addSampleDatum(Connection con, int internalSampleId, String attrId, String attrVal) throws SQLException {

        String sql = "INSERT INTO clinical_sample(INTERNAL_ID,ATTR_ID,ATTR_VALUE) VALUES(?,?,?)";
        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setInt(1, internalSampleId);
            pstmt.setString(2, attrId);
            pstmt.setString(3, attrVal);
            pstmt.executeUpdate();

        }
    }
}
