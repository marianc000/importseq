 

package persistence;
 
import java.sql.SQLException;
 
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Class to wrap Entrez Gene ID, HUGO Gene Symbols,etc.
 */
public class MyCanonicalGene extends MyGene {
    public static final String MIRNA_TYPE = "miRNA";
    public static final String PHOSPHOPROTEIN_TYPE = "phosphoprotein";
    private int geneticEntityId;
    private long entrezGeneId;
    private String hugoGeneSymbol;
    private Set<String> aliases;
    private double somaticMutationFrequency;
    private String cytoband;
    private int length;
    private String type;

    /**
     * @deprecated: hardly used (2 places that are not relevant, 
     * potentially dead code), should be deprecated 
     * 
     * @param hugoGeneSymbol
     */
    public MyCanonicalGene(String hugoGeneSymbol) {
        this(hugoGeneSymbol, null);
    }

    /**
     * This constructor can be used to get a rather empty object 
     * representing this gene symbol. 
     * 
     * Note: Its most important use is for data loading of "phosphogenes"
     * (ImportTabDelimData.importPhosphoGene) where a dummy gene record
     * is generated on the fly for this entity. TODO this constructor needs
     * to be deprecated once "phosphogenes" become a genetic entity on their own. 
     * 
     * @param hugoGeneSymbol
     */
    public MyCanonicalGene(String hugoGeneSymbol, Set<String> aliases) {
        this(-1, -1, hugoGeneSymbol, aliases);
    }
    
    /** 
     * This constructor can be used when geneticEntityId is not yet known, 
     * e.g. in case of a new gene (like when adding new genes in ImportGeneData), or is
     * not needed (like when retrieving mutation data)
     * 
     * @param entrezGeneId
     * @param hugoGeneSymbol
     */
    public MyCanonicalGene(long entrezGeneId, String hugoGeneSymbol) {
        this(-1, entrezGeneId, hugoGeneSymbol, null);
    }

    /**
     * This constructor can be used when geneticEntityId is not yet known, 
     * e.g. in case of a new gene (like when adding new genes in ImportGeneData), or is
     * not needed (like when retrieving mutation data)
     * 
     * @param entrezGeneId
     * @param hugoGeneSymbol
     * @param aliases
     */
    public MyCanonicalGene(long entrezGeneId, String hugoGeneSymbol, Set<String> aliases) {
    	this(-1, entrezGeneId, hugoGeneSymbol, aliases);
    }
    
    public MyCanonicalGene(int geneticEntityId, long entrezGeneId, String hugoGeneSymbol, Set<String> aliases) {
   		this.geneticEntityId = geneticEntityId;
    	this.entrezGeneId = entrezGeneId;
        this.hugoGeneSymbol = hugoGeneSymbol;
        setAliases(aliases);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    /**
     * 
     * @return gene length; 0 if no available
     */
    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public String getCytoband() {
        return cytoband;
    }

    public void setCytoband(String cytoband) {
        this.cytoband = cytoband;
    }

    public Set<String> getAliases() {
        if (aliases==null) {
            return Collections.emptySet();
        }
        return Collections.unmodifiableSet(aliases);
    }

    public void setAliases(Set<String> aliases) {
        if (aliases==null) {
            this.aliases = null;
            return;
        }
        
        Map<String,String> map = new HashMap<String,String>(aliases.size());
        for (String alias : aliases) {
            map.put(alias.toUpperCase(), alias);
        }
        
        this.aliases = new HashSet<String>(map.values());
    }
    
    public int getGeneticEntityId() {
    	return geneticEntityId;
    }
    
    public void setGeneticEntityId(int geneticEntityId) {
        this.geneticEntityId = geneticEntityId;
    }

    public long getEntrezGeneId() {
        return entrezGeneId;
    }

    public void setEntrezGeneId(long entrezGeneId) {
        this.entrezGeneId = entrezGeneId;
    }

    public String getHugoGeneSymbolAllCaps() {
        return hugoGeneSymbol.toUpperCase();
    }

    public String getStandardSymbol() {
        return getHugoGeneSymbolAllCaps();
    }

    public void setHugoGeneSymbol(String hugoGeneSymbol) {
        this.hugoGeneSymbol = hugoGeneSymbol;
    }
    
    public boolean isMicroRNA() {
        return MIRNA_TYPE.equals(type);
    }
    
    public boolean isPhosphoProtein() {
        return PHOSPHOPROTEIN_TYPE.equals(type);
    }

    @Override
    public String toString() {
        return this.getHugoGeneSymbolAllCaps();
    }

    @Override
    public boolean equals(Object obj0) {
        if (!(obj0 instanceof MyCanonicalGene)) {
            return false;
        }
        
        MyCanonicalGene gene0 = (MyCanonicalGene) obj0;
        if (gene0.getEntrezGeneId() == entrezGeneId) {
            return true;
        }
        return false;
    }

    public double getSomaticMutationFrequency() {
        return somaticMutationFrequency;
    }

    public void setSomaticMutationFrequency(double somaticMutationFrequency) {
        this.somaticMutationFrequency = somaticMutationFrequency;
    }

//    public boolean isSangerGene() throws SQLException {
//        MyDaoSangerCensus daoSangerCensus = MyDaoSangerCensus.getInstance();
//
//        String hugo = this.getHugoGeneSymbolAllCaps();
//
//        return daoSangerCensus.getCancerGeneSet().containsKey(hugo);
//    }

    @Override
    public int hashCode() {
        return (int) entrezGeneId;
    }
}