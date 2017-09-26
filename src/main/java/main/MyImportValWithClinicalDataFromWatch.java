/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import clean.CleanSample;
import excel.LoadRROTable;
import files.ClinicalDataFile;
import files.ValFile;
import folder.ExcelAdaptorForValImportFromWatch;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.util.List;
import java.util.Map;

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
public class MyImportValWithClinicalDataFromWatch {

    MyProperties p;

    public MyImportValWithClinicalDataFromWatch() {

    }

    // static String STUDY_NAME = "acc_tcga_chuv3";
    MyImportClinicalData cd;
// importEverything(studyName,valFilePath, clinicalRow, rro.getHeaders());

    void importEverything(String studyName, Path valFilePath, List<String> headers) throws Exception {
        cd = new MyImportClinicalData(studyName);
        try (Connection con = getConnection()) {
            con.setAutoCommit(false);
            int cancerStudyId = MyCancerStudy.getCancerStudyId(con, studyName);

            List<MyClinicalAttribute> columnAttrs = cd.grabAttrs(con, headers, cancerStudyId);
            importMutationFile(con, valFilePath, columnAttrs, cancerStudyId);

            con.commit();
            //   con.rollback();
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
        int sampleId;
        try {
            sampleId = cd.addSample(con, cancerStudyId, patientNameInFile, sampleName);
        } catch (Exception ex) {
            System.out.println(">EX: " + ex);
            new CleanSample().cleanSample(con, cancerStudyId, patientNameInFile, sampleName);
            sampleId = cd.addSample(con, cancerStudyId, patientNameInFile, sampleName);
        }
        importClinicalDataValues(con, row, columnAttrs, sampleId);
        Path dataFilePath = new ExcelAdaptorForValImportFromWatch().run(sourceFilePath.toString());

        File dataFile = dataFilePath.toFile();
        //   System.out.println("dataFile: " + dataFile);

        MyImportProfileData pd = new MyImportProfileData();
        MyAddCaseList cl = new MyAddCaseList();
        int geneticProfileId = cd.getGeneticProfileId(con, cancerStudyId);
        pd.run(con, geneticProfileId, dataFile, sampleId);
        cl.addSampleToList(con, cancerStudyId, sampleId);
        //  throw new RuntimeException("not readY!!!!");
        dataFile.delete();
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

    LoadRROTable rro;

    public void runImport(String clinicalDataFilePath, Path valFilePath, String studyName) throws Exception {
        rro = new LoadRROTable();
        rro.init(clinicalDataFilePath);
        importEverything(studyName, valFilePath, rro.getHeaders());
    }

    public void runImport(String clinicalDataFilePath, String valFilePath) throws Exception {
        runImport(new ClinicalDataFile(clinicalDataFilePath), new ValFile(valFilePath));
    }

    public void runImport(ClinicalDataFile clinicalDataFile, ValFile valFile) throws Exception {
        rro = new LoadRROTable();
        rro.init(clinicalDataFile.getFilePathString());
        importEverything(clinicalDataFile.getStudyName(), valFile.getFilePath(), rro.getHeaders());
    }
    static String RRO_FILE_PATH = "C:\\Projects\\cBioPortal\\data sample\\SECOND SAMPLES\\demo28092017\\H1700899-1A.aca_chuv.xlsx";
    static String VAL_FILE_PATH = "C:\\Projects\\cBioPortal\\data sample\\SECOND SAMPLES\\demo28092017\\H1700899-1A.hg19_coding01.Val.xlsx";

    // static String SOURCE_FILE_DIR = "C:\\Projects\\cBioPortal\\data sample\\test\\";
    public static void main(String... args) throws Exception {
        // new CleanStudy().clean(RRO_STUDY_NAME);
        // new MyImportValWithClinicalDataFromWatch().runImport(RRO_FILE_PATH, Paths.get(VAL_FILE_PATH), new ClinicalDataFile(RRO_FILE_PATH).getStudyName());
      new MyImportValWithClinicalDataFromWatch().runImport(new ClinicalDataFile(RRO_FILE_PATH), new ValFile(VAL_FILE_PATH));
      //  new MyImportValWithClinicalDataFromWatch().runImport(RRO_FILE_PATH, VAL_FILE_PATH);
    }
}
