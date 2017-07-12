package persistence;

import org.mskcc.cbio.portal.model.CanonicalGene;
import org.mskcc.cbio.portal.util.ProgressMonitor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.mskcc.cbio.portal.dao.DaoException;

/**
 * Data Access Object to Gene Table. For faster access, consider using
 * DaoGeneOptimized.
 *
 * @author Ethan Cerami.
 */
final class MyDaoGene {

    private static Map<Long, Set<String>> getAllAliases(Connection con) throws SQLException {
        // System.out.println(">getAllAliases");
        Map<Long, Set<String>> map = new HashMap<>();

        String sql = "SELECT * FROM gene_alias";
        try (PreparedStatement st = con.prepareStatement(sql)) {
            ResultSet rs = st.executeQuery();

            while (rs.next()) {
                Long entrez = rs.getLong("ENTREZ_GENE_ID");
                Set<String> aliases = map.get(entrez);
                if (aliases == null) {
                    aliases = new HashSet<String>();
                    map.put(entrez, aliases);
                }
                aliases.add(rs.getString("GENE_ALIAS"));

            }
        }
        // System.out.println("<getAllAliases");
        return map;
    }

    /**
     * Gets all Genes in the Database.
     *
     * @return ArrayList of Canonical Genes.
     * @throws DaoException Database Error.
     */
    public static ArrayList<CanonicalGene> getAllGenes(Connection con) throws SQLException {
        // System.out.println(">getAllGenes");
        Map<Long, Set<String>> mapAliases = getAllAliases(con);
        ArrayList<CanonicalGene> geneList = new ArrayList<>();
        String sql = "SELECT * FROM gene";
        try (PreparedStatement st = con.prepareStatement(sql)) {

            ResultSet rs = st.executeQuery();

            while (rs.next()) {
                int geneticEntityId = rs.getInt("GENETIC_ENTITY_ID");
                long entrezGeneId = rs.getInt("ENTREZ_GENE_ID");
                Set<String> aliases = mapAliases.get(entrezGeneId);
                CanonicalGene gene = new CanonicalGene(geneticEntityId, entrezGeneId,
                        rs.getString("HUGO_GENE_SYMBOL"), aliases);
                gene.setCytoband(rs.getString("CYTOBAND"));
                gene.setLength(rs.getInt("LENGTH"));
                gene.setType(rs.getString("TYPE"));
                geneList.add(gene);
            }
            // System.out.println("<getAllGenes");
            return geneList;
        }
    }

//    /**
//     * Gets the Gene with the Specified HUGO Gene Symbol. For faster access,
//     * consider using DaoGeneOptimized.
//     *
//     * @param hugoGeneSymbol HUGO Gene Symbol.
//     * @return Canonical Gene Object.
//     * @throws DaoException Database Error.
//     */
//    private static CanonicalGene getGene(String hugoGeneSymbol) throws DaoException {
//        Connection con = null;
//        PreparedStatement pstmt = null;
//        ResultSet rs = null;
//        try {
//            con = JdbcUtil.getDbConnection(MyDaoGene.class);
//            pstmt = con.prepareStatement("SELECT * FROM gene WHERE HUGO_GENE_SYMBOL = ?");
//            pstmt.setString(1, hugoGeneSymbol);
//            rs = pstmt.executeQuery();
//            if (rs.next()) {
//                return extractGene(rs);
//            } else {
//                return null;
//            }
//        } catch (SQLException e) {
//            throw new DaoException(e);
//        } finally {
//            JdbcUtil.closeAll(MyDaoGene.class, con, pstmt, rs);
//        }
//    }
//
//    private static CanonicalGene extractGene(ResultSet rs) throws SQLException, DaoException {
//        int geneticEntityId = rs.getInt("GENETIC_ENTITY_ID");
//        long entrezGeneId = rs.getInt("ENTREZ_GENE_ID");
//        Set<String> aliases = getAliases(entrezGeneId);
//        CanonicalGene gene = new CanonicalGene(geneticEntityId, entrezGeneId,
//                rs.getString("HUGO_GENE_SYMBOL"), aliases);
//        gene.setCytoband(rs.getString("CYTOBAND"));
//        gene.setLength(rs.getInt("LENGTH"));
//        gene.setType(rs.getString("TYPE"));
//
//        return gene;
//    }
//
//    /**
//     * Gets the Number of Gene Records in the Database.
//     *
//     * @return number of gene records.
//     * @throws DaoException Database Error.
//     */
//    public static int getCount() throws DaoException {
//        Connection con = null;
//        PreparedStatement pstmt = null;
//        ResultSet rs = null;
//        try {
//            con = JdbcUtil.getDbConnection(MyDaoGene.class);
//            pstmt = con.prepareStatement("SELECT COUNT(*) FROM gene");
//            rs = pstmt.executeQuery();
//            if (rs.next()) {
//                return rs.getInt(1);
//            }
//            return 0;
//        } catch (SQLException e) {
//            throw new DaoException(e);
//        } finally {
//            JdbcUtil.closeAll(MyDaoGene.class, con, pstmt, rs);
//        }
//    }
//
//    /**
//     * Deletes the Gene Record that has the Entrez Gene ID in the Database.
//     *
//     * @param entrezGeneId
//     */
//    public static void deleteGene(long entrezGeneId) throws DaoException {
//        deleteGeneAlias(entrezGeneId);
//
//        Connection con = null;
//        PreparedStatement pstmt = null;
//        ResultSet rs = null;
//        try {
//            con = JdbcUtil.getDbConnection(MyDaoGene.class);
//            pstmt = con.prepareStatement("DELETE FROM gene WHERE ENTREZ_GENE_ID=?");
//            pstmt.setLong(1, entrezGeneId);
//            pstmt.executeUpdate();
//        } catch (SQLException e) {
//            throw new DaoException(e);
//        } finally {
//            JdbcUtil.closeAll(MyDaoGene.class, con, pstmt, rs);
//        }
//    }
//
//    /**
//     * Deletes the Gene Alias Record(s) that has/have the Entrez Gene ID in the
//     * Database.
//     *
//     * @param entrezGeneId
//     */
//    public static void deleteGeneAlias(long entrezGeneId) throws DaoException {
//        Connection con = null;
//        PreparedStatement pstmt = null;
//        ResultSet rs = null;
//        try {
//            con = JdbcUtil.getDbConnection(MyDaoGene.class);
//            pstmt = con.prepareStatement("DELETE FROM gene_alias WHERE ENTREZ_GENE_ID=?");
//            pstmt.setLong(1, entrezGeneId);
//            pstmt.executeUpdate();
//        } catch (SQLException e) {
//            throw new DaoException(e);
//        } finally {
//            JdbcUtil.closeAll(MyDaoGene.class, con, pstmt, rs);
//        }
//    }
//
//    /**
//     * Deletes all Gene Records in the Database.
//     *
//     * @throws DaoException Database Error.
//     *
//     * @deprecated only used by deprecated code, so deprecating this as well.
//     */
//    public static void deleteAllRecords() throws DaoException {
//        Connection con = null;
//        PreparedStatement pstmt = null;
//        ResultSet rs = null;
//        try {
//            con = JdbcUtil.getDbConnection(MyDaoGene.class);
//            JdbcUtil.disableForeignKeyCheck(con);
//            pstmt = con.prepareStatement("TRUNCATE TABLE gene");
//            pstmt.executeUpdate();
//            JdbcUtil.enableForeignKeyCheck(con);
//        } catch (SQLException e) {
//            throw new DaoException(e);
//        } finally {
//            JdbcUtil.closeAll(MyDaoGene.class, con, pstmt, rs);
//        }
//        deleteAllAliasRecords();
//    }
//
//    /**
//     *
//     * @throws DaoException
//     *
//     * @deprecated only used by deprecated code, so deprecating this as well.
//     */
//    private static void deleteAllAliasRecords() throws DaoException {
//        Connection con = null;
//        PreparedStatement pstmt = null;
//        ResultSet rs = null;
//        try {
//            con = JdbcUtil.getDbConnection(MyDaoGene.class);
//            pstmt = con.prepareStatement("TRUNCATE TABLE gene_alias");
//            pstmt.executeUpdate();
//        } catch (SQLException e) {
//            throw new DaoException(e);
//        } finally {
//            JdbcUtil.closeAll(MyDaoGene.class, con, pstmt, rs);
//        }
//    }
}
