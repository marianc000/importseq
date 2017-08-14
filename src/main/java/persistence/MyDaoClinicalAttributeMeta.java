

package persistence;

import org.mskcc.cbio.portal.model.*;

import com.google.inject.internal.Join;
import org.apache.commons.lang.StringUtils;

import java.sql.*;
import java.util.*;
import org.mskcc.cbio.portal.util.InternalIdUtil;

/**
 * Data Access Object for `clinical_attribute_meta` table
 *
 * @author Gideon Dresdner
 */
public class MyDaoClinicalAttributeMeta {

    public static int addDatum(ClinicalAttribute attr)  throws SQLException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = JdbcUtil.getDbConnection(MyDaoClinicalAttributeMeta.class);
            pstmt = con.prepareStatement
                    ("INSERT INTO clinical_attribute_meta(" +
                            "`ATTR_ID`," +
                            "`DISPLAY_NAME`," +
                            "`DESCRIPTION`," +
                            "`DATATYPE`," +
                            "`PATIENT_ATTRIBUTE`," +
                            "`PRIORITY`," +
                            "`CANCER_STUDY_ID`)" +
                            " VALUES(?,?,?,?,?,?,?)");
            pstmt.setString(1, attr.getAttrId());
            pstmt.setString(2, attr.getDisplayName());
            pstmt.setString(3, attr.getDescription());
            pstmt.setString(4, attr.getDatatype());
            pstmt.setBoolean(5, attr.isPatientAttribute());
            pstmt.setString(6, attr.getPriority());
            pstmt.setInt(7, attr.getCancerStudyId());
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException(e);
        } finally {
            JdbcUtil.closeAll(MyDaoClinicalAttributeMeta.class, con, pstmt, rs);
        }
    }

    private static ClinicalAttribute unpack(ResultSet rs) throws SQLException {
        return new ClinicalAttribute(rs.getString("ATTR_ID"),
            rs.getString("DISPLAY_NAME"),
            rs.getString("DESCRIPTION"),
            rs.getString("DATATYPE"),
            rs.getBoolean("PATIENT_ATTRIBUTE"),
            rs.getString("PRIORITY"),
            rs.getInt("CANCER_STUDY_ID"));
    }
    
    public static ClinicalAttribute getDatum(String attrId, Integer cancerStudyId) throws SQLException {
        List<ClinicalAttribute> attrs = getDatum(Arrays.asList(attrId), cancerStudyId);
        if (attrs.isEmpty()) {
            return null;
        }
        
        return attrs.get(0);
    }

    public static List<ClinicalAttribute> getDatum(Collection<String> attrIds, Integer cancerStudyId) throws SQLException {
        if(attrIds == null || attrIds.isEmpty() ) {
            return Collections.emptyList();
        }
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = JdbcUtil.getDbConnection(MyDaoClinicalAttributeMeta.class);

            pstmt = con.prepareStatement("SELECT * FROM clinical_attribute_meta WHERE ATTR_ID IN ('"
                    + StringUtils.join(attrIds,"','")+"')  AND CANCER_STUDY_ID=" + String.valueOf(cancerStudyId));

            rs = pstmt.executeQuery();

            List<ClinicalAttribute> list = new ArrayList<ClinicalAttribute>();
            while (rs.next()) {
                list.add(unpack(rs));
            }
            
            return list;
        } catch (SQLException e) {
            throw new SQLException(e);
        } finally {
            JdbcUtil.closeAll(MyDaoClinicalAttributeMeta.class, con, pstmt, rs);
        }
    }
    
    public static List<ClinicalAttribute> getDatum(Collection<String> attrIds) throws SQLException {
        if(attrIds == null || attrIds.isEmpty() ) {
            return Collections.emptyList();
        }
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = JdbcUtil.getDbConnection(MyDaoClinicalAttributeMeta.class);

            pstmt = con.prepareStatement("SELECT * FROM clinical_attribute_meta WHERE ATTR_ID IN ('"
                    + StringUtils.join(attrIds,"','")+"')");

            rs = pstmt.executeQuery();

            List<ClinicalAttribute> list = new ArrayList<ClinicalAttribute>();
            while (rs.next()) {
                list.add(unpack(rs));
            }
            
            return list;
        } catch (SQLException e) {
            throw new SQLException(e);
        } finally {
            JdbcUtil.closeAll(MyDaoClinicalAttributeMeta.class, con, pstmt, rs);
        }        
    }

    public static List<ClinicalAttribute> getDataByStudy(int cancerStudyId) throws SQLException
    {
        List<ClinicalAttribute> attrs = new ArrayList<ClinicalAttribute>();
        attrs.addAll(getDataByCancerStudyId(cancerStudyId));
        
        return attrs;
    }

    /**
     * Gets all the clinical attributes for a particular set of samples
     * Looks in the clinical table for all records associated with any of the samples, extracts and uniques
     * the attribute ids, then finally uses the attribute ids to fetch the clinical attributes from the db.
     *
     * @param sampleIdSet
     * @return
     * @throws SQLException
     */
    private static List<ClinicalAttribute> getDataByCancerStudyId(int cancerStudyId) throws SQLException {
        
        Connection con = null;
        ResultSet rs = null;
		PreparedStatement pstmt = null;

        String sql = ("SELECT DISTINCT ATTR_ID FROM clinical_attribute_meta"
                + " WHERE CANCER_STUDY_ID = " + String.valueOf(cancerStudyId));

        Set<String> attrIds = new HashSet<String>();
        try {
            con = JdbcUtil.getDbConnection(MyDaoClinicalAttributeMeta.class);
            pstmt = con.prepareStatement(sql);
            rs = pstmt.executeQuery();
            
             while(rs.next()) {
                attrIds.add(rs.getString("ATTR_ID"));
            }

        } catch (SQLException e) {
            throw new SQLException(e);
        } finally {
            JdbcUtil.closeAll(MyDaoClinicalAttributeMeta.class, con, pstmt, rs);
        }

        return getDatum(attrIds, cancerStudyId);
    }

    private static Collection<ClinicalAttribute> getAll() throws SQLException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Collection<ClinicalAttribute> all = new ArrayList<ClinicalAttribute>();

        try {
            con = JdbcUtil.getDbConnection(MyDaoClinicalAttributeMeta.class);
            pstmt = con.prepareStatement("SELECT * FROM clinical_attribute_meta");
            rs = pstmt.executeQuery();

            while (rs.next()) {
                all.add(unpack(rs));
            }

        } catch (SQLException e) {
            throw new SQLException(e);
        } finally {
            JdbcUtil.closeAll(MyDaoClinicalAttributeMeta.class, con, pstmt, rs);
        }
        return all;
    }

	public static Map<String, String> getAllMap() throws SQLException {

		HashMap<String, String> toReturn = new HashMap<String, String>();
		for (ClinicalAttribute clinicalAttribute : MyDaoClinicalAttributeMeta.getAll()) {
			toReturn.put(clinicalAttribute.getAttrId(), clinicalAttribute.getDisplayName());
		}
		return toReturn;
	}

    /**
     * Deletes all Records.
     * @throws SQLException DAO Error.
     */
    public static void deleteAllRecords() throws SQLException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = JdbcUtil.getDbConnection(MyDaoClinicalAttributeMeta.class);
            pstmt = con.prepareStatement("TRUNCATE TABLE clinical_attribute_meta");
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException(e);
        } finally {
            JdbcUtil.closeAll(MyDaoClinicalAttributeMeta.class, con, pstmt, rs);
        }
    }
}
