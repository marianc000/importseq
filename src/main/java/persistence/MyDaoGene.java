package persistence;
 
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
 

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
    public static ArrayList<MyCanonicalGene> getAllGenes(Connection con) throws SQLException {
        // System.out.println(">getAllGenes");
        Map<Long, Set<String>> mapAliases = getAllAliases(con);
        ArrayList<MyCanonicalGene> geneList = new ArrayList<>();
        String sql = "SELECT * FROM gene";
        try (PreparedStatement st = con.prepareStatement(sql)) {

            ResultSet rs = st.executeQuery();

            while (rs.next()) {
                int geneticEntityId = rs.getInt("GENETIC_ENTITY_ID");
                long entrezGeneId = rs.getInt("ENTREZ_GENE_ID");
                Set<String> aliases = mapAliases.get(entrezGeneId);
                MyCanonicalGene gene = new MyCanonicalGene(geneticEntityId, entrezGeneId,
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
 
}
