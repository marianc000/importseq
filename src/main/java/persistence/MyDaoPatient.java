 
package persistence;

import org.mskcc.cbio.portal.model.*;

import org.apache.commons.collections.map.MultiKeyMap;

import java.sql.*;
import java.util.*;

/**
 * DAO to `patient`.
 *
 * @author Benjamin Gross
 */
public class MyDaoPatient {

    private static final Map<Integer, Patient> byInternalId = new HashMap<Integer, Patient>();
    private static final Map<Integer, Set<Patient>> byInternalCancerStudyId = new HashMap<Integer, Set<Patient>>();
    private static final MultiKeyMap byCancerIdAndStablePatientId = new MultiKeyMap();

    private static void clearCache() {
        byInternalId.clear();
        byInternalCancerStudyId.clear();
        byCancerIdAndStablePatientId.clear();
    }

    public static synchronized void reCache() {
        clearCache();

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            con = JdbcUtil.getDbConnection(MyDaoPatient.class);
            pstmt = con.prepareStatement("SELECT * FROM patient");
            rs = pstmt.executeQuery();
            ArrayList<Patient> list = new ArrayList<Patient>();
            while (rs.next()) {
                Patient p = extractPatient(rs);
                if (p != null) {
                    cachePatient(p, p.getCancerStudy().getInternalId());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JdbcUtil.closeAll(MyDaoPatient.class, con, pstmt, rs);
        }
    }

    public static void cachePatient(Patient patient, int cancerStudyId) {
        if (!byInternalId.containsKey(patient.getInternalId())) {
            byInternalId.put(patient.getInternalId(), patient);
        }
        if (byInternalCancerStudyId.containsKey(patient.getCancerStudy().getInternalId())) {
            byInternalCancerStudyId.get(patient.getCancerStudy().getInternalId()).add(patient);
        } else {
            Set<Patient> patientList = new HashSet<Patient>();
            patientList.add(patient);
            byInternalCancerStudyId.put(patient.getCancerStudy().getInternalId(), patientList);
        }

        if (!byCancerIdAndStablePatientId.containsKey(cancerStudyId, patient.getStableId())) {
            byCancerIdAndStablePatientId.put(cancerStudyId, patient.getStableId(), patient);
        }
    }

    public static int addPatient(Patient patient) throws SQLException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = JdbcUtil.getDbConnection(MyDaoPatient.class);
            pstmt = con.prepareStatement("INSERT INTO patient (`STABLE_ID`, `CANCER_STUDY_ID`) VALUES (?,?)",
                    Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, patient.getStableId());
            pstmt.setInt(2, patient.getCancerStudy().getInternalId());
            pstmt.executeUpdate();
            rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                cachePatient(new Patient(patient.getCancerStudy(), patient.getStableId(), rs.getInt(1)), patient.getCancerStudy().getInternalId());
                return rs.getInt(1);
            }
            return -1;
        } catch (SQLException e) {
            throw new SQLException(e);
        } finally {
            JdbcUtil.closeAll(MyDaoPatient.class, con, pstmt, rs);
        }
    }

    public static Patient getPatientById(int internalId) {
        return byInternalId.get(internalId);
    }

    public static Patient getPatientById(Connection con, int internalId) throws SQLException {
        System.out.println(">getPatientById: ");
        String sql = "SELECT * FROM  patient where INTERNAL_ID=?";
        try (PreparedStatement st = con.prepareStatement(sql)) {
            st.setInt(1, internalId);

            ResultSet rs = st.executeQuery();
            rs.next();
            Patient p = extractPatient(rs);
            System.out.println("<getPatientById: " + p);
            return p;
        }
    }

    public static Patient getPatientByCancerStudyAndPatientId(int cancerStudyId, String stablePatientId) {
        return (Patient) byCancerIdAndStablePatientId.get(cancerStudyId, stablePatientId);
    }

    public static Patient getPatientByCancerStudyAndPatientId(Connection con, int cancerStudyId, String stablePatientId) throws SQLException {
        String sql = "SELECT * FROM  patient where STABLE_ID=? and CANCER_STUDY_ID=?";
        try (PreparedStatement st = con.prepareStatement(sql)) {
            st.setInt(2, cancerStudyId);
            st.setString(1, stablePatientId);
            ResultSet rs = st.executeQuery();
            rs.next();
            Patient p = extractPatient(rs);
            System.out.println("getPatientByCancerStudyAndPatientId: " + p);
            return p;
        }
    }

    public static Set<Patient> getPatientsByCancerStudyId(int cancerStudyId) {
        return byInternalCancerStudyId.get(cancerStudyId);
    }

    public static List<Patient> getAllPatients() {
        return (byInternalId.isEmpty()) ? Collections.<Patient>emptyList()
                : new ArrayList<Patient>(byInternalId.values());
    }

    public static void deleteAllRecords() throws SQLException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = JdbcUtil.getDbConnection(MyDaoPatient.class);
            JdbcUtil.disableForeignKeyCheck(con);
            pstmt = con.prepareStatement("TRUNCATE TABLE patient");
            pstmt.executeUpdate();
            JdbcUtil.enableForeignKeyCheck(con);
        } catch (SQLException e) {
            throw new SQLException(e);
        } finally {
            JdbcUtil.closeAll(MyDaoPatient.class, con, pstmt, rs);
        }

        clearCache();
    }

    private static Patient extractPatient(ResultSet rs) throws SQLException {
//		try {
        CancerStudy cancerStudy = DaoCancerStudy.getCancerStudyByInternalIdFromCache(rs.getInt("CANCER_STUDY_ID"));
        if (cancerStudy == null) {
            return null;
        }
        return new Patient(cancerStudy,
                rs.getString("STABLE_ID"),
                rs.getInt("INTERNAL_ID"));
        //}
//		catch (DaoException e) {
//			throw new SQLException(e);
//		}
    }
}
