/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package folder;

import excel.LoadTabForVal;
import excel.LoadValForVal;
import excel.OutputMafRow;
import static excel.OutputMafRow.printCollection;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import static utils.FileUtils.getOutputFilePath;

/**
 *
 * @author mcaikovs
 */
public class ExcelAdaptorForValImport {

    void printMessage(OutputMafRow rowInVal, Map<String, OutputMafRow> mutationsInTab, String msg) {
        System.out.println(msg + ":\n" + rowInVal.toMafRow());
        printCollection(mutationsInTab.values(), "MUTATIONS FOR THE SAME GENE IN TAB: ");
        System.out.println();
    }

    public Path run(String sourceTabFilePath) throws IOException, InvalidFormatException {
        System.out.println("sourceTabFilePath="+sourceTabFilePath);
        Map<String, Map<String, OutputMafRow>> tabMap = new LoadTabForVal().run(sourceTabFilePath);
        String sourceValFilePath = sourceTabFilePath.replace("coding01.Tab", "coding01.Val");
        List<OutputMafRow> mafRows = new LoadValForVal().run(sourceValFilePath);

        for (OutputMafRow rowInVal : mafRows) {
            Map<String, OutputMafRow> mutationsInTab = tabMap.get(rowInVal.getGeneName());
            if (mutationsInTab == null) {
                throw new RuntimeException("this is impossible");
            }
            OutputMafRow rowInTab = mutationsInTab.get(rowInVal.getAaMutation());
            String msg = "";
            if (rowInTab == null) {
                msg += "NOT FOUND";
            } else {
                // mutationsInTab.remove(rowInVal.getAaMutation());
                if (!rowInTab.getChromosome().equals(rowInVal.getChromosome())) {
                    if (rowInVal.getChromosome().equals("0")) {
                        String refSeq = rowInVal.getRefSeqWithNucleotideMutation();
                        if (!(refSeq.startsWith("NM_033360.3") || refSeq.startsWith("NM_005896.3")|| refSeq.startsWith("NM_004448.3"))) {
                            msg += "CHROMOSOME; ";
                        }
                    }
                }
                if (!rowInTab.getVariantClassification().equals(rowInVal.getVariantClassification())) {
                    msg += "VARIANT_CLASSIFICATION; ";
                }
            }
            if (!msg.isEmpty()) {
                printMessage(rowInVal, mutationsInTab, msg);
            }
        }

//        for (int c = 0; c < excel.getColumn(GENE_COLUMN).size(); c++) {
//            String gene = excel.getColumn(GENE_COLUMN).get(c);
//            String mutation = excel.getColumn(AA_CHANGE_COLUMN).get(c);
//            String mutationType = excel.getColumn(EXONIC_FUNC_REFGENE).get(c);
//            String chromosome = excel.getColumn(CHROMOSOME_COLUMN).get(c);
//            Set<String> validMutations = geneValidMutationsMap.get(gene);
//            System.out.println("gene: " + gene + "; mutation: " + mutation + "; validMutations: " + validMutations);
//            if (validMutations != null) {
//
//                boolean validated = validMutations.remove(mutation);
//
//                if (!validated) {
//                    // try to replace * by X
//                    String mutation2 = transformStopMutationName(mutation);
//                    validated = validMutations.remove(transformStopMutationName(mutation2));
//                    System.out.println("mutation2: " + mutation2 + "; validMutations: " + validMutations);
//                }
//                if (!validated) { // splicing, no mutation in protein
//                    if (mutation.isEmpty() && mutationType.equals("Splice_Site")) {
//                        validated = validMutations.remove("Splicing");
//                    }
//                }
//                if (!validated) {
//                    for (String validMutation : validMutations) { //p.L252_I254del
//                        String transformedValidMutation = transformDeletionMutationName(validMutation); //p.252_254del
//                        if (mutation.equals(transformedValidMutation)) {
//                            validated = validMutations.remove(validMutation);
//                            break;
//                        }
//                    }
//                }
//
//                if (validated) {
//                    validationCol.set(c, "Validated in Val file");
//                    if (validMutations.isEmpty()) {
//                        geneValidMutationsMap.remove(gene);
//                    }
//                }
//            }
//        }
//        //   LoadRROTable.printMap(geneValidMutationsMap);
//        if (!geneValidMutationsMap.isEmpty()) {
//            // throw new RuntimeException("map is not empty");
//            System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" + sourceFilePath);
//            System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!map is not empty");
//            LoadRROTable.printMap(geneValidMutationsMap);
//            System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
//        }
//
//        addColumn(SAMPLE_NAME_COLUMN, getSampleName(sourceFilePath));
//        insertColumn(IGNORED_COLUMN, "");
//        insertHeaders();
//        //  printDocument();
//        saveDocument(getOutputFilePath(), doc);
        return getOutputFilePath(sourceValFilePath);
    }
    Pattern delMutationPattern = Pattern.compile("p\\.[A-Z]\\d+_[A-Z]\\d+del");

    String transformDeletionMutationName(String nameInVal) {
        Matcher m = delMutationPattern.matcher(nameInVal);
        if (m.matches()) {
            return nameInVal.replaceAll("[A-Z]", "");
        }
        return nameInVal;
    }
    Pattern stopMutationPattern = Pattern.compile("p\\.[A-Z]\\d+X");

    String transformStopMutationName(String nameInTab) {
        Matcher m = stopMutationPattern.matcher(nameInTab);
        if (m.matches()) {
            return nameInTab.replace("X", "*");
        }

        return nameInTab;
    }

    public static void main(String... args) throws IOException, InvalidFormatException {
        new ExcelAdaptorForValImport().run("C:\\Projects\\cBioPortal\\data sample\\SECOND SAMPLES\\corrected\\H1706047-1A.hg19_coding01.Tab.xlsx");
    }
}
