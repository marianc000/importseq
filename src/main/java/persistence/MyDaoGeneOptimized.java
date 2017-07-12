package persistence;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.mskcc.cbio.portal.dao.DaoException;
import org.mskcc.cbio.portal.model.CanonicalGene;
import org.mskcc.cbio.portal.util.ProgressMonitor;

/**
 * A Utility Class that speeds access to Gene Info.
 *
 * @author Ethan Cerami
 */
public class MyDaoGeneOptimized {

    private static final String CBIO_CANCER_GENES_FILE = "/cbio_cancer_genes.txt";
    private static final String GENE_SYMBOL_DISAMBIGUATION_FILE = "/gene_symbol_disambiguation.txt";

    private static MyDaoGeneOptimized daoGeneOptimized;
    //nb: make sure any map is also cleared in clearCache() method below:
    private final HashMap<String, CanonicalGene> geneSymbolMap = new HashMap<>();
    private final HashMap<Long, CanonicalGene> entrezIdMap = new HashMap<>();
    private final HashMap<Integer, CanonicalGene> geneticEntityMap = new HashMap<>();
    private final HashMap<String, List<CanonicalGene>> geneAliasMap = new HashMap<>();
    private final Set<CanonicalGene> cbioCancerGenes = new HashSet<>();
    private final Map<String, CanonicalGene> disambiguousGenes = new HashMap<>();

    /**
     * Private Constructor, to enforce singleton pattern.
     *
     * @throws DaoException Database Error.
     */
    private MyDaoGeneOptimized(Connection con) throws SQLException {
        fillCache(con);
    }

    private synchronized void fillCache(Connection con) throws SQLException {

        //  Automatically populate hashmap upon init
        ArrayList<CanonicalGene> globalGeneList = MyDaoGene.getAllGenes(con);
        for (CanonicalGene currentGene : globalGeneList) {
            cacheGene(currentGene);
        }

        try {
            if (geneSymbolMap.size() > 10000) {
                // only for deployed version; not for unit test and importing
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(getClass().getResourceAsStream(CBIO_CANCER_GENES_FILE)));
                for (String line = in.readLine(); line != null; line = in.readLine()) {
                    String[] parts = line.trim().split("\t", -1);
                    CanonicalGene gene = null;
                    if (parts.length > 1) {
                        gene = getGene(Long.parseLong(parts[1]));
                    } else {
                        gene = getGene(parts[0]);
                    }
                    if (gene != null) {
                        cbioCancerGenes.add(gene);
                    } else {
                        System.out.println(line + " in the cbio cancer gene list config file [resources" + CBIO_CANCER_GENES_FILE
                             + "] is not a HUGO gene symbol. You should either update this file or update the `gene` and `gene_alias` tables to fix this.");
                    }
                }
                in.close();
            }

            {
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(getClass().getResourceAsStream(GENE_SYMBOL_DISAMBIGUATION_FILE)));
                for (String line = in.readLine(); line != null; line = in.readLine()) {
                    if (line.startsWith("#")) {
                        continue;
                    }
                    String[] parts = line.trim().split("\t", -1);
                    CanonicalGene gene = getGene(Long.parseLong(parts[1]));
                    if (gene == null) {
                        ProgressMonitor.logWarning(line + " in config file [resources" + GENE_SYMBOL_DISAMBIGUATION_FILE
                                + "]is not valid. You should either update this file or update the `gene` and `gene_alias` tables to fix this.");
                    }
                    disambiguousGenes.put(parts[0], gene);
                }
                in.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void cacheGene(CanonicalGene gene) {
        geneSymbolMap.put(gene.getHugoGeneSymbolAllCaps(), gene);
        entrezIdMap.put(gene.getEntrezGeneId(), gene);
        geneticEntityMap.put(gene.getGeneticEntityId(), gene);

        for (String alias : gene.getAliases()) {
            String aliasUp = alias.toUpperCase();
            List<CanonicalGene> genes = geneAliasMap.get(aliasUp);
            if (genes == null) {
                genes = new ArrayList<CanonicalGene>();
                geneAliasMap.put(aliasUp, genes);
            }
            genes.add(gene);
        }
    }

    public static MyDaoGeneOptimized getInstance(Connection con) throws SQLException {
        if (daoGeneOptimized == null) {
            daoGeneOptimized = new MyDaoGeneOptimized(con);
        }

        return daoGeneOptimized;
    }

    /**
     * Return geneticEntityId from cache for given entrezGeneId
     *
     * @param entrezGeneId
     * @return
     */
    public static int getGeneticEntityId(long entrezGeneId) {
        //get entity id from cache:
        CanonicalGene gene = daoGeneOptimized.getGene(entrezGeneId);
        if (gene != null) {
            return gene.getGeneticEntityId();
        } else {
            throw new RuntimeException("Invalid entrezGeneId symbol. Not found in cache: " + entrezGeneId);
        }
    }

    /**
     * Return entrezGeneId from cache for given geneticEntityId
     *
     * @param geneticEntityId
     * @return
     */
    public static long getEntrezGeneId(int geneticEntityId) {
        //get entity id from cache:
        CanonicalGene gene = daoGeneOptimized.getGeneByEntityId(geneticEntityId);
        //since not every genetic entity will be a gene, this could be null (but would
        //be a programming error elsewhere, so throw exception):
        if (gene == null) {
            throw new RuntimeException("Genetic entity was not found in gene cache: " + geneticEntityId);
        }

        return daoGeneOptimized.getGeneByEntityId(geneticEntityId).getEntrezGeneId();
    }

    /**
     * Gets Gene by HUGO Gene Symbol.
     *
     * @param hugoGeneSymbol HUGO Gene Symbol.
     * @return Canonical Gene Object.
     */
    public CanonicalGene getGene(String hugoGeneSymbol) {
        // System.out.println(">getGene: " + hugoGeneSymbol);
        return geneSymbolMap.get(hugoGeneSymbol.toUpperCase());
    }

    /**
     * Looks for a Gene where HUGO Gene Symbol or an alias matches the given
     * symbol.
     *
     * @param geneSymbol: HUGO Gene Symbol or an alias
     * @param searchInAliases: set to true if this method should search for a
     * match in this.geneAliasMap in case a matching hugo symbol cannot be found
     * in this.geneSymbolMap
     *
     * @return
     */
    public List<CanonicalGene> getGene(String geneSymbol, boolean searchInAliases) {
        CanonicalGene gene = getGene(geneSymbol);
        if (gene != null) {
            return Collections.singletonList(gene);
        }

        if (searchInAliases) {
            List<CanonicalGene> genes = geneAliasMap.get(geneSymbol.toUpperCase());
            if (genes != null) {
                return Collections.unmodifiableList(genes);
            }
        }

        return Collections.emptyList();
    }

    /**
     * Gets Gene By Entrez Gene ID.
     *
     * @param entrezId Entrez Gene ID.
     * @return Canonical Gene Object.
     */
    public CanonicalGene getGene(long entrezId) {
        // System.out.println(">getGene long: " + entrezId);
        CanonicalGene gene = entrezIdMap.get(entrezId);
        if (gene == null) {
            throw new RuntimeException("gene is null");
        }
         // System.out.println("<getGene gene: " + gene);
        return gene;
    }

    /**
     * Gets Gene By Entrez Gene ID.
     *
     * @param entrezId Entrez Gene ID.
     * @return Canonical Gene Object.
     */
    public CanonicalGene getGeneByEntityId(int geneticEntityId) {
        return geneticEntityMap.get(geneticEntityId);
    }

    /**
     * Look for genes with a specific ID. First look for genes with the specific
     * Entrez Gene ID, if found return this gene; then for HUGO symbol, if
     * found, return this gene; and lastly for aliases, if found, return a list
     * of matched genes (could be more than one). If nothing matches, return an
     * empty list.
     *
     * @param geneId an Entrez Gene ID or HUGO symbol or gene alias
     * @return A list of genes that match, an empty list if no match.
     */
    public List<CanonicalGene> guessGene(String geneId) {
        return guessGene(geneId, null);
    }

    /**
     * Look for genes with a specific ID on a chr. First look for genes with the
     * specific Entrez Gene ID, if found return this gene; then for HUGO symbol,
     * if found, return this gene; and lastly for aliases, if found, return a
     * list of matched genes (could be more than one). If chr is not null, use
     * that to match too. If nothing matches, return an empty list.
     *
     * @param geneId an Entrez Gene ID or HUGO symbol or gene alias
     * @param chr chromosome
     * @return A list of genes that match, an empty list if no match.
     */
    public List<CanonicalGene> guessGene(String geneId, String chr) {

        CanonicalGene gene = getGene(geneId); // HUGO gene symbol
        if (gene != null) {
            return Collections.singletonList(gene);
        }

        List<CanonicalGene> genes = geneAliasMap.get(geneId.toUpperCase());
        if (genes != null) {
            if (chr == null) {
                return Collections.unmodifiableList(genes);
            }

            String nchr = normalizeChr(chr);

            List<CanonicalGene> ret = new ArrayList<CanonicalGene>();
            for (CanonicalGene cg : genes) {
                String gchr = getChrFromCytoband(cg.getCytoband());
                if (gchr == null // TODO: should we exlude this?
                        || gchr.equals(nchr)) {
                    ret.add(cg);
                }
            }
            return ret;
        }

        return Collections.emptyList();
    }

    private static Map<String, String> validChrValues = null;

    public static String normalizeChr(String strChr) {
        if (strChr == null) {
            return null;
        }

        if (validChrValues == null) {
            validChrValues = new HashMap<String, String>();
            for (int lc = 1; lc <= 24; lc++) {
                validChrValues.put(Integer.toString(lc), Integer.toString(lc));
                validChrValues.put("CHR" + Integer.toString(lc), Integer.toString(lc));
            }
            validChrValues.put("X", "23");
            validChrValues.put("CHRX", "23");
            validChrValues.put("Y", "24");
            validChrValues.put("CHRY", "24");
            validChrValues.put("NA", "NA");
            validChrValues.put("MT", "MT"); // mitochondria
        }

        return validChrValues.get(strChr);
    }

    private static String getChrFromCytoband(String cytoband) {
        if (cytoband == null) {
            return null;
        }

        if (cytoband.startsWith("X")) {
            return "23";
        }

        if (cytoband.startsWith("Y")) {
            return "24";
        }

        Pattern p = Pattern.compile("([0-9]+).*");
        Matcher m = p.matcher(cytoband);
        if (m.find()) {
            return m.group(1);
        }

        return null;
    }

    public CanonicalGene getNonAmbiguousGene(String geneId, String chr) {
        // System.out.println(">getNonAmbiguousGene: geneId=" + geneId + "; chr=" + chr);
        List<CanonicalGene> genes = guessGene(geneId, chr);
        if (genes.isEmpty()) {
            return null;
        }

        if (genes.size() == 1) {
            return genes.get(0);
        }

        if (disambiguousGenes.containsKey(geneId)) {
            return disambiguousGenes.get(geneId);
        }

        StringBuilder sb = new StringBuilder("Ambiguous alias ");
        sb.append(geneId);
        sb.append(": corresponding entrez ids of ");
        for (CanonicalGene gene : genes) {
            sb.append(gene.getEntrezGeneId());
            sb.append(",");
        }
        sb.deleteCharAt(sb.length() - 1);

        return null;

    }

    public Set<Long> getEntrezGeneIds(Collection<CanonicalGene> genes) {
        Set<Long> entrezGeneIds = new HashSet<Long>();
        for (CanonicalGene gene : genes) {
            entrezGeneIds.add(gene.getEntrezGeneId());
        }
        return entrezGeneIds;
    }

    public Set<CanonicalGene> getCbioCancerGenes() {
        return Collections.unmodifiableSet(cbioCancerGenes);
    }

    public boolean isCbioCancerGene(CanonicalGene gene) {
        return cbioCancerGenes.contains(gene);
    }

    /**
     * Gets an ArrayList of All Genes.
     *
     * @return Array List of All Genes.
     */
    public ArrayList<CanonicalGene> getAllGenes() {
        return new ArrayList<>(entrezIdMap.values());
    }

}
