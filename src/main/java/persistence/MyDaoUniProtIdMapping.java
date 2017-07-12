package persistence;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Data access object for uniprot_id_mapping table.
 */
public final class MyDaoUniProtIdMapping {
 

    public static String mapFromUniprotIdToAccession(Connection con, String uniprotId) throws SQLException {
        String sql = "select UNIPROT_ACC from uniprot_id_mapping where UNIPROT_ID = ?";
        try (PreparedStatement st = con.prepareStatement(sql)) {
            st.setString(1, uniprotId);
            ResultSet rs = st.executeQuery();
            String r = null;
            if (rs.next()) {
                r = rs.getString(1);
            }
           // System.out.println("mapFromUniprotIdToAccession: " + r);
            return r;
        }

    }
 
}
