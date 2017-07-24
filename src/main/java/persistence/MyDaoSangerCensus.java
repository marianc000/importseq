 

package persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import org.mskcc.cbio.portal.model.CanonicalGene;
import org.mskcc.cbio.portal.model.SangerCancerGene;

/**
 * Data access object for Sanger Cancer Gene Census Table.
 */
public class MyDaoSangerCensus {
    private static MyDaoSangerCensus daoSangerCensus;
    private HashMap<String, SangerCancerGene> geneCensus;

    public static MyDaoSangerCensus getInstance() throws SQLException {
        if (daoSangerCensus == null || daoSangerCensus.getCancerGeneSet().size() == 0) {
            daoSangerCensus = new MyDaoSangerCensus();
        }
        return daoSangerCensus;
    }

    private MyDaoSangerCensus() {
    }

    public int addGene(CanonicalGene gene, boolean cancerSomaticMutation, boolean cancerGermlineMutation,
            String tumorTypesSomaticMutation, String tumorTypesGermlineMutation,
            String cancerSyndrome, String tissueType,
            String mutationType, String translocationPartner,
            boolean otherGermlineMut, String otherDisease) throws SQLException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = JdbcUtil.getDbConnection(MyDaoSangerCensus.class);
            pstmt = con.prepareStatement ("INSERT INTO sanger_cancer_census ("
                 + "`ENTREZ_GENE_ID`, `CANCER_SOMATIC_MUT`, `CANCER_GERMLINE_MUT`,"
                 + "`TUMOR_TYPES_SOMATIC_MUT`, `TUMOR_TYPES_GERMLINE_MUT`, `CANCER_SYNDROME`,"
                 + "`TISSUE_TYPE`, `MUTATION_TYPE`, `TRANSLOCATION_PARTNER`, `OTHER_GERMLINE_MUT`,"
                 + "`OTHER_DISEASE`) VALUES (?,?,?,?,?,?,?,?,?,?,?)");
            pstmt.setLong(1, gene.getEntrezGeneId());
            pstmt.setBoolean(2, cancerSomaticMutation);
            pstmt.setBoolean(3, cancerGermlineMutation);
            pstmt.setString(4, tumorTypesSomaticMutation);
            pstmt.setString(5, tumorTypesGermlineMutation);
            pstmt.setString(6, cancerSyndrome);
            pstmt.setString(7, tissueType);
            pstmt.setString(8, mutationType);
            pstmt.setString(9, translocationPartner);
            pstmt.setBoolean(10, otherGermlineMut);
            pstmt.setString(11, otherDisease);
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException(e);
        } finally {
            JdbcUtil.closeAll(MyDaoSangerCensus.class, con, pstmt, rs);
        }
    }

    public HashMap<String, SangerCancerGene> getCancerGeneSet() throws SQLException {
        if (geneCensus == null || geneCensus.size() == 0) {
            geneCensus = lookUpCancerGeneSet();
        }
        return geneCensus;
    }

    private HashMap<String, SangerCancerGene> lookUpCancerGeneSet() throws SQLException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        HashMap<String, SangerCancerGene> geneCenusus = new HashMap<String, SangerCancerGene>();
        try {
            con = JdbcUtil.getDbConnection(MyDaoSangerCensus.class);
            pstmt = con.prepareStatement ("SELECT * FROM sanger_cancer_census");
            rs = pstmt.executeQuery();
            while (rs.next()) {
                SangerCancerGene gene = extractCancerGene(rs);
                geneCenusus.put(gene.getGene().getHugoGeneSymbolAllCaps(), gene);
            }
            return geneCenusus;
        } catch (SQLException e) {
            throw new SQLException(e);
        } finally {
            JdbcUtil.closeAll(MyDaoSangerCensus.class, con, pstmt, rs);
        }
    }

    private SangerCancerGene extractCancerGene(ResultSet rs) throws SQLException, SQLException {
        SangerCancerGene cancerGene = new SangerCancerGene();
        long entrezGene = rs.getLong("ENTREZ_GENE_ID");

        DaoGeneOptimized daoGene = DaoGeneOptimized.getInstance();
        cancerGene.setGene(daoGene.getGene(entrezGene));

        cancerGene.setCancerSomaticMutation(rs.getBoolean("CANCER_SOMATIC_MUT"));
        cancerGene.setCancerGermlineMutation(rs.getBoolean("CANCER_GERMLINE_MUT"));
        cancerGene.setTumorTypesSomaticMutation(rs.getString("TUMOR_TYPES_SOMATIC_MUT"));
        cancerGene.setTumorTypesGermlineMutation(rs.getString("TUMOR_TYPES_GERMLINE_MUT"));
        cancerGene.setCancerSyndrome(rs.getString("CANCER_SYNDROME"));
        cancerGene.setTissueType(rs.getString("TISSUE_TYPE"));
        cancerGene.setMutationType(rs.getString("MUTATION_TYPE"));
        cancerGene.setTranslocationPartner(rs.getString("TRANSLOCATION_PARTNER"));
        cancerGene.setOtherGermlineMut(rs.getBoolean("OTHER_GERMLINE_MUT"));
        cancerGene.setOtherDisease(rs.getString("OTHER_DISEASE"));
        return cancerGene;
    }

    public void deleteAllRecords() throws SQLException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = JdbcUtil.getDbConnection(MyDaoSangerCensus.class);
            pstmt = con.prepareStatement("TRUNCATE TABLE sanger_cancer_census");
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException(e);
        } finally {
            JdbcUtil.closeAll(MyDaoSangerCensus.class, con, pstmt, rs);
        }
    }
}