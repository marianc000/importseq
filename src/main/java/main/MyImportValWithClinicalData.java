/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import clean.CleanStudy;
import excel.LoadRROTable;
import files.FileFinder;
import folder.ExcelAdaptorForValImport;
import java.io.File;
import java.nio.file.Path;
import java.sql.Connection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import persistence.MyAddCaseList;
import persistence.MyCancerStudy;
import persistence.MyClinicalAttribute;
import static persistence.MyConnection.getConnection;
import persistence.MyDaoClinicalData;

import persistence.MyImportClinicalData;
import persistence.MyImportProfileData;
import utils.FileUtils;

/**
 *
 * @author mcaikovs
 */
public class MyImportValWithClinicalData {

    MyProperties p;

    public MyImportValWithClinicalData() {

    }

    // static String STUDY_NAME = "acc_tcga_chuv3";
    MyImportClinicalData cd = new MyImportClinicalData(RRO_STUDY_NAME);

    void importEverything(Set<Path> mutationFilePaths, Map<String, List<String>> refextNipMap, List<String> headers) throws Exception {

        try (Connection con = getConnection()) {
            con.setAutoCommit(false);
            int cancerStudyId = MyCancerStudy.getCancerStudyId(con, RRO_STUDY_NAME);

            List<MyClinicalAttribute> columnAttrs = cd.grabAttrs(con, headers, cancerStudyId);

            for (Path sourceFilePath : mutationFilePaths) {
                importMutationFile(con, sourceFilePath, columnAttrs, cancerStudyId);
            }
            con.commit();
            // con.rollback();
        }
    }

    String removeLeadingZeros(String str) {
        try {
            int id = Integer.parseUnsignedInt(str);
            return String.valueOf(id);
        } catch (Exception ex) {
            return str;
        }
    }

    void importMutationFile(Connection con, Path sourceFilePath, List<MyClinicalAttribute> columnAttrs, int cancerStudyId) throws Exception {
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>importMutationFile: sourceFilePath=" + sourceFilePath.getFileName());

        String sampleName = FileUtils.getSampleName(sourceFilePath);
        Map<String, List<String>> refextNipMap = rro.getRefextNipMap();
        List<String> row = refextNipMap.get(sampleName);
        // System.out.println(">importMutationFile: row=" + row);
        String sampleNameInFile = row.get(rro.getRefextHeaderIndex());
        String patientNameInFile = removeLeadingZeros(row.get(rro.getNipHeaderIndex()));
        // System.out.println(">importMutationFile: " + sourceFilePath.getFileName() + "; sampleName=" + sampleNameInFile + "; patientName=" + patientNameInFile);
        if (!sampleName.equals(sampleNameInFile)) {
            throw new RuntimeException("sample names differ");
        }

        int sampleId = cd.addSample(con, cancerStudyId, patientNameInFile, sampleName);
        importClinicalDataValues(con, row, columnAttrs, sampleId);
        Path dataFilePath = new ExcelAdaptorForValImport().run(sourceFilePath.toString());

        File dataFile = dataFilePath.toFile();
        //   System.out.println("dataFile: " + dataFile);

        MyImportProfileData pd = new MyImportProfileData();
        MyAddCaseList cl = new MyAddCaseList();
        int geneticProfileId = cd.getGeneticProfileId(con, cancerStudyId);
        pd.run(con, geneticProfileId, dataFile, sampleId);
        cl.addSampleToList(con, cancerStudyId, sampleId);
        //  throw new RuntimeException("not readY!!!!");
        System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<importMutationFile: sourceFilePath=" + sourceFilePath.getFileName());
    }

    void importClinicalDataValues(Connection con, List<String> row, List<MyClinicalAttribute> columnAttrs, int internalSampleId) throws Exception {
        System.out.println(">importClinicalDataValues");
        int sampleIdIndex = rro.getRefextHeaderIndex();
        int patientIdIndex = rro.getNipHeaderIndex();

        for (int lc = 0; lc < row.size(); lc++) {
            //if lc is sampleIdIndex or patientIdIndex, skip as well since these are the relational fields:
            if (lc == sampleIdIndex || lc == patientIdIndex) {
                // continue; // skip for tests
            }
            //if the value matches one of the missing values, skip this attribute:
            String val = row.get(lc);
            if (val == null || val.isEmpty()) {
                continue;
            }

            MyDaoClinicalData.addSampleDatum(con, internalSampleId, columnAttrs.get(lc).getAttrId(), val);
        }

    }

    void printSet(Set<String> set) {
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        for (String s : set) {
            System.out.println("'" + s + "'");
        }
        System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
    }
    LoadRROTable rro;

    public void runImport(String clinicalDataFilePath) throws Exception {
        rro = new LoadRROTable();
        rro.init(clinicalDataFilePath);
        Map<String, List<String>> refextNipMap = rro.getRefextNipMap();
        FileFinder ff = new FileFinder();
        Set<Path> mutationFilePaths = ff.run(SOURCE_FILE_DIR);

        Set<String> samplesInFiles = ff.getSampleNames();
        System.out.println("Samples in files: " + samplesInFiles.size());

        Set<String> samplesInRro = new HashSet<>(refextNipMap.keySet());

        samplesInRro.removeAll(samplesInFiles);
        System.out.println("Samples without files: " + samplesInRro);

        samplesInFiles = ff.getSampleNames();
        samplesInFiles.removeAll(refextNipMap.keySet());
        System.out.println("Files without rro samples: " + samplesInFiles);
        System.out.println("Files: " + mutationFilePaths.size());
        System.out.println("Sample names: " + refextNipMap.size());
        importEverything(mutationFilePaths, refextNipMap, rro.getHeaders());
    }

    static String RRO_FILE_PATH = "C:\\Projects\\cBioPortal\\data sample\\NEW CLINICAL DATA\\20170824 Cbioportal export2.xlsx";
    // static String SOURCE_FILE_DIR = "C:\\Projects\\cBioPortal\\data sample\\SECOND SAMPLES\\corrected\\H1702318-1A.hg19_coding01.Tab.xlsx";
    static String SOURCE_FILE_DIR = "C:\\Projects\\cBioPortal\\data sample\\SECOND SAMPLES\\corrected\\";
    public static String RRO_STUDY_NAME = "chuv_val2";

    // static String SOURCE_FILE_DIR = "C:\\Projects\\cBioPortal\\data sample\\test\\";
    public static void main(String... args) throws Exception {
        new CleanStudy().clean(RRO_STUDY_NAME);
        new MyImportValWithClinicalData().runImport(RRO_FILE_PATH);
    }
}
