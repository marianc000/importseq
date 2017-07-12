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
import org.mskcc.cbio.portal.dao.DaoGeneOptimized;
import org.mskcc.cbio.portal.model.CanonicalGene;
import org.mskcc.cbio.portal.model.ExtendedMutation;

/**
 *
 * @author mcaikovs
 */
public class MyMutationEventDao {

    public static ExtendedMutation.MutationEvent getMutationEvent(Connection con,
            long entrezId, String chr, long startPosition, long endPosition,
            String proteinChange, String tumorSeqAllele, String mutationType) throws SQLException {
        // System.out.println(">getMutationEvent");
        String sql = "SELECT * FROM  mutation_event where ENTREZ_GENE_ID=?"
                + " and CHR=? and START_POSITION=? and END_POSITION=? and PROTEIN_CHANGE=? and TUMOR_SEQ_ALLELE=? and MUTATION_TYPE=?";
        try (PreparedStatement st = con.prepareStatement(sql)) {
            st.setLong(1, entrezId);
            st.setString(2, chr);
            st.setLong(3, startPosition);
            st.setLong(4, endPosition);
            st.setString(5, proteinChange);
            st.setString(6, tumorSeqAllele);
            st.setString(7, mutationType);
            ResultSet rs = st.executeQuery();

            if (rs.next()) {
                ExtendedMutation.MutationEvent event = extractMutationEvent(con, rs);
                // System.out.println("found mutation event: " + event);
                return event;
            }

            // System.out.println("mutation event not found");
            return null;
        }
    }

    static ExtendedMutation.MutationEvent extractMutationEvent(Connection con, ResultSet rs) throws SQLException {
        ExtendedMutation.MutationEvent event = new ExtendedMutation.MutationEvent();
        event.setMutationEventId(rs.getLong("MUTATION_EVENT_ID"));
        long entrezId = rs.getLong("ENTREZ_GENE_ID");
        CanonicalGene gene = MyDaoGeneOptimized.getInstance(con).getGene(entrezId);
        // System.out.println(">extractMutationEvent: gene=" + gene);
        
        event.setGene(gene);
        event.setChr(rs.getString("CHR"));
        event.setStartPosition(rs.getLong("START_POSITION"));
        event.setEndPosition(rs.getLong("END_POSITION"));
        event.setProteinChange(rs.getString("PROTEIN_CHANGE"));
        event.setMutationType(rs.getString("MUTATION_TYPE"));
        event.setFunctionalImpactScore(rs.getString("FUNCTIONAL_IMPACT_SCORE"));
        event.setFisValue(rs.getFloat("FIS_VALUE"));
        event.setLinkXVar(rs.getString("LINK_XVAR"));
        event.setLinkPdb(rs.getString("LINK_PDB"));
        event.setLinkMsa(rs.getString("LINK_MSA"));
        event.setNcbiBuild(rs.getString("NCBI_BUILD"));
        event.setStrand(rs.getString("STRAND"));
        event.setVariantType(rs.getString("VARIANT_TYPE"));
        event.setDbSnpRs(rs.getString("DB_SNP_RS"));
        event.setDbSnpValStatus(rs.getString("DB_SNP_VAL_STATUS"));
        event.setReferenceAllele(rs.getString("REFERENCE_ALLELE"));
        event.setOncotatorDbSnpRs(rs.getString("ONCOTATOR_DBSNP_RS"));
        event.setOncotatorRefseqMrnaId(rs.getString("ONCOTATOR_REFSEQ_MRNA_ID"));
        event.setOncotatorCodonChange(rs.getString("ONCOTATOR_CODON_CHANGE"));
        event.setOncotatorUniprotName(rs.getString("ONCOTATOR_UNIPROT_ENTRY_NAME"));
        event.setOncotatorUniprotAccession(rs.getString("ONCOTATOR_UNIPROT_ACCESSION"));
        event.setOncotatorProteinPosStart(rs.getInt("ONCOTATOR_PROTEIN_POS_START"));
        event.setOncotatorProteinPosEnd(rs.getInt("ONCOTATOR_PROTEIN_POS_END"));
        event.setCanonicalTranscript(rs.getBoolean("CANONICAL_TRANSCRIPT"));
        event.setTumorSeqAllele(rs.getString("TUMOR_SEQ_ALLELE"));
        event.setKeyword(rs.getString("KEYWORD"));
        return event;
    }
}
