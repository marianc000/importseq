package persistence;

import org.mskcc.cbio.portal.dao.*;
import org.mskcc.cbio.portal.model.*;
import org.mskcc.cbio.portal.model.ExtendedMutation.MutationEvent;
import org.mskcc.cbio.portal.util.*;
import org.mskcc.cbio.maf.*;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import org.mskcc.cbio.portal.scripts.MutationFilter;

/**
 * Import an extended mutation file. Columns may be in any order.
 * <p>
 * @author Ethan Cerami
 * <br>
 * @author Arthur Goldberg goldberg@cbio.mskcc.org
 * <br>
 * @author Selcuk Onur Sumer
 */
public class MyImportExtendedMutationData {

    private File mutationFile;
    private int geneticProfileId;
    private boolean swissprotIsAccession;
    private MutationFilter myMutationFilter;
    private int entriesSkipped = 0;
    private int samplesSkipped = 0;
    private Set<String> sampleSet = new HashSet<>();
    private Set<String> geneSet = new HashSet<>();
    private String genePanel;

    /**
     * construct an ImportExtendedMutationData. Filter mutations according to
     * the no argument MutationFilter().
     */
    public MyImportExtendedMutationData(File mutationFile, int geneticProfileId, String genePanel) {
        this.mutationFile = mutationFile;
        this.geneticProfileId = geneticProfileId;
        this.swissprotIsAccession = false;
        this.genePanel = genePanel;

        // create default MutationFilter
        myMutationFilter = new MutationFilter();
    }

    /**
     * Turns parsing the SWISSPROT column as an accession on or off again.
     *
     * If off, the column will be parsed as the name (formerly ID).
     *
     * @param swissprotIsAccession whether to parse the column as an accession
     */
    public void setSwissprotIsAccession(boolean swissprotIsAccession) {
        this.swissprotIsAccession = swissprotIsAccession;
    }

    public void importData(Connection con, int sampleId) throws IOException, DaoException, SQLException {
        System.out.println(">importData");
        //   MySQLbulkLoader.bulkLoadOn();
        Sample sample = MySampleDao.getSampleById(con, sampleId);

        MySampleDao.addSampleProfile(con, sample.getInternalId(), geneticProfileId);

        Set<MutationEvent> newEvents = new HashSet<>();

        Map<ExtendedMutation, ExtendedMutation> mutations = new HashMap<>();

        long mutationEventId = MyDaoMutation.getLargestMutationEventId(con);

        try (BufferedReader buf = new BufferedReader(new FileReader(mutationFile))) {

            //  DaoGeneOptimized daoGene = DaoGeneOptimized.getInstance();
            //  The MAF File Changes fairly frequently, and we cannot use column index constants.
            String line = buf.readLine();
            while (line.startsWith("#")) {
                line = buf.readLine(); // skip comments/meta info
            }

            line = line.trim();

            MafUtil mafUtil = new MafUtil(line);

            boolean fileHasOMAData = false;

            if (mafUtil.getMaFImpactIndex() >= 0) {
                // fail gracefully if a non-essential column is missing
                // e.g. if there is no MA_link.var column, we assume that the value is NA and insert it as such
                fileHasOMAData = true;
                ProgressMonitor.setCurrentMessage(" --> OMA Scores Column Number:  "
                        + mafUtil.getMaFImpactIndex());
            } else {
                fileHasOMAData = false;
            }

            //  GeneticProfile geneticProfile = DaoGeneticProfile.getGeneticProfileById(geneticProfileId);
            while ((line = buf.readLine()) != null) {

                if (!line.startsWith("#") && line.trim().length() > 0) {
                    String[] parts = line.split("\t", -1); // the -1 keeps trailing empty strings; see JavaDoc for String
                    MafRecord record = mafUtil.parseRecord(line);

                    // process case id
                    String barCode = record.getTumorSampleID();
                    // backwards compatible part (i.e. in the new process, the sample should already be there. TODO - replace this workaround later with an exception:

                    String validationStatus = record.getValidationStatus();

                    if (validationStatus == null
                            || validationStatus.equalsIgnoreCase("Wildtype")) {
                        ProgressMonitor.logWarning("Skipping entry with Validation_Status: Wildtype");
                        entriesSkipped++;
                        continue;
                    }

                    String chr = MyDaoGeneOptimized.normalizeChr(record.getChr().toUpperCase());

                    if (chr == null) {
                        ProgressMonitor.logWarning("Skipping entry with chromosome value: " + record.getChr());
                        entriesSkipped++;
                        continue;
                    }

                    record.setChr(chr);

                    if (record.getStartPosition() < 0) {
                        record.setStartPosition(0);
                    }

                    if (record.getEndPosition() < 0) {
                        record.setEndPosition(0);
                    }

                    String functionalImpactScore = "";
                    // using -1 is not safe, FIS can be a negative value
                    Float fisValue = Float.MIN_VALUE;
                    String linkXVar = "";
                    String linkMsa = "";
                    String linkPdb = "";

                    if (fileHasOMAData) {
                        functionalImpactScore = record.getMaFuncImpact();
                        fisValue = record.getMaFIS();
                        linkXVar = record.getMaLinkVar();
                        linkMsa = record.getMaLinkMsa();
                        linkPdb = record.getMaLinkPdb();

                        functionalImpactScore = transformOMAScore(functionalImpactScore);
                        linkXVar = linkXVar.replace("\"", "");
                    }

                    String mutationType,
                            proteinChange,
                            aaChange,
                            codonChange,
                            refseqMrnaId,
                            uniprotAccession;

                    int proteinPosStart,
                            proteinPosEnd;

                    // determine whether to use canonical or best effect transcript
                    // try canonical first
                    if (ExtendedMutationUtil.isAcceptableMutation(record.getVariantClassification())) {
                        mutationType = record.getVariantClassification();
                    } // if not acceptable either, use the default value
                    else {
                        mutationType = ExtendedMutationUtil.getMutationType(record);
                    }

                    // skip RNA mutations
                    if (mutationType != null && mutationType.equalsIgnoreCase("rna")) {
                        ProgressMonitor.logWarning("Skipping entry with mutation type: RNA");
                        entriesSkipped++;
                        continue;
                    }

                    proteinChange = ExtendedMutationUtil.getProteinChange(parts, record);
                    //proteinChange = record.getProteinChange();
                    aaChange = record.getAminoAcidChange();
                    codonChange = record.getCodons();
                    refseqMrnaId = record.getRefSeq();
                    if (this.swissprotIsAccession) {
                        uniprotAccession = record.getSwissprot();
                    } else {
                        String uniprotName = record.getSwissprot();

                        uniprotAccession = MyDaoUniProtIdMapping.mapFromUniprotIdToAccession(con, uniprotName);
                    }

                    proteinPosStart = ExtendedMutationUtil.getProteinPosStart(
                            record.getProteinPosition(), proteinChange);
                    proteinPosEnd = ExtendedMutationUtil.getProteinPosEnd(
                            record.getProteinPosition(), proteinChange);

                    //  Assume we are dealing with Entrez Gene Ids (this is the best / most stable option)
                    String geneSymbol = record.getHugoGeneSymbol();

                    CanonicalGene gene = MyDaoGeneOptimized.getInstance(con).getNonAmbiguousGene(geneSymbol, chr);
                  //  System.out.println(">gene=" + gene);
                    if (gene == null) {
                        throw new RuntimeException("gene is null");
                    }
                    ExtendedMutation mutation = new ExtendedMutation();

                    mutation.setGeneticProfileId(geneticProfileId);
                    mutation.setSampleId(sample.getInternalId());
                    mutation.setGene(gene);
                    mutation.setSequencingCenter(record.getCenter());
                    mutation.setSequencer(record.getSequencer());
                    mutation.setProteinChange(proteinChange);
                    mutation.setAminoAcidChange(aaChange);
                    mutation.setMutationType(mutationType);
                    mutation.setChr(record.getChr());
                    mutation.setStartPosition(record.getStartPosition());
                    mutation.setEndPosition(record.getEndPosition());
                    mutation.setValidationStatus(record.getValidationStatus());
                    mutation.setMutationStatus(record.getMutationStatus());
                    mutation.setFunctionalImpactScore(functionalImpactScore);
                    mutation.setFisValue(fisValue);
                    mutation.setLinkXVar(linkXVar);
                    mutation.setLinkPdb(linkPdb);
                    mutation.setLinkMsa(linkMsa);
                    mutation.setNcbiBuild(record.getNcbiBuild());
                    mutation.setStrand(record.getStrand());
                    mutation.setVariantType(record.getVariantType());
                    mutation.setAllele(record.getTumorSeqAllele1(), record.getTumorSeqAllele2(), record.getReferenceAllele());
                    mutation.setDbSnpRs(record.getDbSNP_RS());
                    mutation.setDbSnpValStatus(record.getDbSnpValStatus());
                    mutation.setMatchedNormSampleBarcode(record.getMatchedNormSampleBarcode());
                    mutation.setMatchNormSeqAllele1(record.getMatchNormSeqAllele1());
                    mutation.setMatchNormSeqAllele2(record.getMatchNormSeqAllele2());
                    mutation.setTumorValidationAllele1(record.getTumorValidationAllele1());
                    mutation.setTumorValidationAllele2(record.getTumorValidationAllele2());
                    mutation.setMatchNormValidationAllele1(record.getMatchNormValidationAllele1());
                    mutation.setMatchNormValidationAllele2(record.getMatchNormValidationAllele2());
                    mutation.setVerificationStatus(record.getVerificationStatus());
                    mutation.setSequencingPhase(record.getSequencingPhase());
                    mutation.setSequenceSource(record.getSequenceSource());
                    mutation.setValidationMethod(record.getValidationMethod());
                    mutation.setScore(record.getScore());
                    mutation.setBamFile(record.getBamFile());

                    // here we set      Variant Reads	Ref Reads              
                    mutation.setTumorAltCount(ExtendedMutationUtil.getTumorAltCount(record));
                    mutation.setTumorRefCount(ExtendedMutationUtil.getTumorRefCount(record));
                    mutation.setNormalAltCount(ExtendedMutationUtil.getNormalAltCount(record));
                    mutation.setNormalRefCount(ExtendedMutationUtil.getNormalRefCount(record));

                    // TODO rename the oncotator column names (remove "oncotator")
                    mutation.setOncotatorCodonChange(codonChange);
                    mutation.setOncotatorRefseqMrnaId(refseqMrnaId);
                    mutation.setOncotatorUniprotAccession(uniprotAccession);
                    mutation.setOncotatorProteinPosStart(proteinPosStart);
                    mutation.setOncotatorProteinPosEnd(proteinPosEnd);

                    // TODO we don't use this info right now...
                    mutation.setCanonicalTranscript(true);

                    //  Filter out Mutations
                    if (myMutationFilter.acceptMutation(mutation)) {

                        MutationEvent e = mutation.getEvent();

                        MutationEvent event = MyMutationEventDao.getMutationEvent(con,
                                e.getGene().getEntrezGeneId(),
                                e.getChr(),
                                e.getStartPosition(),
                                e.getEndPosition(),
                                e.getProteinChange(), e.getTumorSeqAllele(), e.getMutationType());

                        if (event != null) {
                            mutation.setEvent(event);
                        } else {
                            mutation.setMutationEventId(++mutationEventId);
                            // existingEvents.put(mutation.getEvent(), mutation.getEvent());
                            newEvents.add(mutation.getEvent());
                        }

                        if (mutations.get(mutation) != null) {
                            throw new RuntimeException("Mutation exists already");
                        } else {
                            mutations.put(mutation, mutation);
                        }
                        //keep track:
                        sampleSet.add(barCode);
                        geneSet.add(mutation.getEntrezGeneId() + "");
                    } else {
                        entriesSkipped++;
                    }

                }
            }
        }
        for (MutationEvent event : newEvents) {

            MyDaoMutation.addMutationEvent(con, event);

        }

        for (ExtendedMutation mutation : mutations.values()) {
            MyDaoMutation.addMutation(con, mutation);
        }

        // calculate mutation count for every sample
        MyDaoMutation.calculateMutationCount(con, geneticProfileId);
        System.out.println(">importData9");
        System.out.println(" --> total number of data entries skipped (see table below):  " + entriesSkipped);
        System.out.println(" --> total number of samples: " + sampleSet.size());
        System.out.println(" --> total number of samples skipped (normal samples): " + samplesSkipped);
        System.out.println(" --> total number of genes for which one or more mutation events were stored:  " + geneSet.size());

        System.out.println("Filtering table:\n-----------------");
        System.out.println(myMutationFilter.getStatistics());
    }

    private String transformOMAScore(String omaScore) {
        if (omaScore == null || omaScore.length() == 0) {
            return omaScore;
        }
        if (omaScore.equalsIgnoreCase("H") || omaScore.equalsIgnoreCase("high")) {
            return "H";
        } else if (omaScore.equalsIgnoreCase("M") || omaScore.equalsIgnoreCase("medium")) {
            return "M";
        } else if (omaScore.equalsIgnoreCase("L") || omaScore.equalsIgnoreCase("low")) {
            return "L";
        } else if (omaScore.equalsIgnoreCase("N") || omaScore.equalsIgnoreCase("neutral")) {
            return "N";
        } else if (omaScore.equalsIgnoreCase("[sent]")) {
            return ExtendedMutationUtil.NOT_AVAILABLE; // TODO temp workaround for invalid sent values
        } else {
            return omaScore;
        }
    }
}
