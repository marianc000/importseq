package persistence;


import java.sql.*;
 

/**
 * Data access object for Mutation table
 */
public final class MyDaoMutation {

    public static final String NAN = "NaN";

    static String mutationSql = " insert into mutation(MUTATION_EVENT_ID,GENETIC_PROFILE_ID,SAMPLE_ID,ENTREZ_GENE_ID,CENTER,SEQUENCER,MUTATION_STATUS,VALIDATION_STATUS,TUMOR_SEQ_ALLELE1,TUMOR_SEQ_ALLELE2,MATCHED_NORM_SAMPLE_BARCODE,MATCH_NORM_SEQ_ALLELE1,MATCH_NORM_SEQ_ALLELE2,TUMOR_VALIDATION_ALLELE1,TUMOR_VALIDATION_ALLELE2,MATCH_NORM_VALIDATION_ALLELE1,MATCH_NORM_VALIDATION_ALLELE2,VERIFICATION_STATUS,SEQUENCING_PHASE,SEQUENCE_SOURCE,VALIDATION_METHOD,SCORE,BAM_FILE,TUMOR_ALT_COUNT,TUMOR_REF_COUNT,NORMAL_ALT_COUNT,NORMAL_REF_COUNT ) values(";

    static String getMutationInsertSql(MyExtendedMutation mutation) {
        String sql = mutationSql
                + Long.toString(mutation.getMutationEventId()) + ","
                + Integer.toString(mutation.getGeneticProfileId()) + ","
                + Integer.toString(mutation.getSampleId()) + ","
                + Long.toString(mutation.getGene().getEntrezGeneId()) + ","
                + wrapString(mutation.getSequencingCenter()) + ","
                + wrapString(mutation.getSequencer()) + ","
                + wrapString(mutation.getMutationStatus()) + ","
                + wrapString(mutation.getValidationStatus()) + ","
                + wrapString(mutation.getTumorSeqAllele1()) + ","
                + wrapString(mutation.getTumorSeqAllele2()) + ","
                + wrapString(mutation.getMatchedNormSampleBarcode()) + ","
                + wrapString(mutation.getMatchNormSeqAllele1()) + ","
                + wrapString(mutation.getMatchNormSeqAllele2()) + ","
                + wrapString(mutation.getTumorValidationAllele1()) + ","
                + wrapString(mutation.getTumorValidationAllele2()) + ","
                + wrapString(mutation.getMatchNormValidationAllele1()) + ","
                + wrapString(mutation.getMatchNormValidationAllele2()) + ","
                + wrapString(mutation.getVerificationStatus()) + ","
                + wrapString(mutation.getSequencingPhase()) + ","
                + wrapString(mutation.getSequenceSource()) + ","
                + wrapString(mutation.getValidationMethod()) + ","
                + wrapString(mutation.getScore()) + ","
                + wrapString(mutation.getBamFile()) + ","
                + Integer.toString(mutation.getTumorAltCount()) + ","
                + Integer.toString(mutation.getTumorRefCount()) + ","
                + Integer.toString(mutation.getNormalAltCount()) + ","
                + Integer.toString(mutation.getNormalRefCount()) + ")";
      //  // System.out.println(">getMutationInsertSql: " + sql);
        return sql;
    }

    public static int addMutation(Connection con, MyExtendedMutation mutation) throws SQLException {
        // System.out.println(">addMutation");
        String sql = getMutationInsertSql(mutation);
        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            return pstmt.executeUpdate();
        }
    }

    static String sql = "insert into mutation_event (MUTATION_EVENT_ID,ENTREZ_GENE_ID,CHR,START_POSITION,END_POSITION,"
            + "REFERENCE_ALLELE,TUMOR_SEQ_ALLELE,PROTEIN_CHANGE,MUTATION_TYPE,FUNCTIONAL_IMPACT_SCORE,FIS_VALUE,"
            + "LINK_XVAR,LINK_PDB,LINK_MSA,NCBI_BUILD,STRAND,VARIANT_TYPE,DB_SNP_RS,DB_SNP_VAL_STATUS,ONCOTATOR_DBSNP_RS,"
            + "ONCOTATOR_REFSEQ_MRNA_ID,ONCOTATOR_CODON_CHANGE,ONCOTATOR_UNIPROT_ENTRY_NAME,ONCOTATOR_UNIPROT_ACCESSION,"
            + "ONCOTATOR_PROTEIN_POS_START,ONCOTATOR_PROTEIN_POS_END,CANONICAL_TRANSCRIPT,KEYWORD) values(";

    static String wrapString(String str) {
        if (str == null) {
            return null;
        }
        return "'" + str + "'";
    }

    static String getInsertMutationEventSql(MyExtendedMutation.MutationEvent event) {
        // System.out.println(">addMutationEvent");
        String keyword = MyMutationKeywordUtils.guessOncotatorMutationKeyword(event.getProteinChange(), event.getMutationType());
        // System.out.println(">addMutationEvent: keyword=" + keyword);
        String mySql = sql
                + event.getMutationEventId() + ","
                + event.getGene().getEntrezGeneId() + ","
                + wrapString(event.getChr()) + ","
                + event.getStartPosition() + ","
                + event.getEndPosition() + ","
                + wrapString(event.getReferenceAllele()) + ","
                + wrapString(event.getTumorSeqAllele()) + ","
                + wrapString(event.getProteinChange()) + ","
                + wrapString(event.getMutationType()) + ","
                + wrapString(event.getFunctionalImpactScore()) + ","
                + Float.toString(event.getFisValue()) + ","
                + wrapString(event.getLinkXVar()) + ","
                + wrapString(event.getLinkPdb()) + ","
                + wrapString(event.getLinkMsa()) + ","
                + wrapString(event.getNcbiBuild()) + ","
                + wrapString(event.getStrand()) + ","
                + wrapString(event.getVariantType()) + ","
                + wrapString(event.getDbSnpRs()) + ","
                + wrapString(event.getDbSnpValStatus()) + ","
                + wrapString(event.getOncotatorDbSnpRs()) + ","
                + wrapString(event.getOncotatorRefseqMrnaId()) + ","
                + wrapString(event.getOncotatorCodonChange()) + ","
                + wrapString(event.getOncotatorUniprotName()) + ","
                + wrapString(event.getOncotatorUniprotAccession()) + ","
                + event.getOncotatorProteinPosStart() + ","
                + event.getOncotatorProteinPosEnd() + ","
                + boolToStr(event.isCanonicalTranscript()) + ","
                + wrapString(keyword == null ? "\\N" : (event.getGene().getHugoGeneSymbolAllCaps() + " " + keyword))
                + ")";
        // System.out.println("<addMutationEvent: sql=" + mySql);
        return mySql;
    }

    public static int addMutationEvent(Connection con, MyExtendedMutation.MutationEvent event) throws  SQLException {
        // System.out.println(">addMutationEvent");

        String sql = getInsertMutationEventSql(event);
        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.executeUpdate();
        }
        // System.out.println("<addMutationEvent");
        return 1;
    }

    public static int calculateMutationCount(Connection con, int profileId) throws SQLException {
        // System.out.println(">addMutationEvent: profileId=" + profileId);
        String sql = "INSERT INTO mutation_count "
                + "SELECT genetic_profile.`GENETIC_PROFILE_ID`, `SAMPLE_ID`, COUNT(*) AS MUTATION_COUNT "
                + "FROM `mutation` , `genetic_profile` "
                + "WHERE mutation.`GENETIC_PROFILE_ID` = genetic_profile.`GENETIC_PROFILE_ID` "
                + "AND genetic_profile.`GENETIC_PROFILE_ID`=? "
                + "GROUP BY genetic_profile.`GENETIC_PROFILE_ID` , `SAMPLE_ID` ";

        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setInt(1, profileId);
            return pstmt.executeUpdate();
        }
    }

    protected static String boolToStr(boolean value) {
        return value ? "1" : "0";
    }

    public static long getLargestMutationEventId(Connection con) throws SQLException {

        String sql = "SELECT MAX(`MUTATION_EVENT_ID`) FROM `mutation_event`";
        try (PreparedStatement st = con.prepareStatement(sql)) {
            ResultSet rs = st.executeQuery();
            rs.next();
            long id = rs.getLong(1);
            // System.out.println("getLargestMutationEventId: id" + id);
            return id;
        }
    }
}
