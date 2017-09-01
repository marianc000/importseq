/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import folder.ExcelAdaptor;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;

import persistence.MyAddCaseList;
import persistence.MyCancerStudy;
import static persistence.MyConnection.getConnection;
import persistence.MyImportClinicalData;
import persistence.MyImportProfileData;
import utils.FileUtils;

/**
 *
 * @author mcaikovs
 */
public class MyImport {

    MyProperties p;

    public MyImport(MyProperties p) throws IOException {
        this.p = p;

    }

    // static String STUDY_NAME = "acc_tcga_chuv3";
    String getSampleName(Path path) {
        String fileName = path.getFileName().toString();
        String sampleName = fileName.substring(0, fileName.indexOf("."));
        System.out.println("sampleName: " + sampleName);
        if (sampleName == null || sampleName.isEmpty()) {
            throw new RuntimeException("Cannot extract a sample name from path " + path);
        }
        return sampleName;
    }

    void importFile(Connection con, String sourceFilePath) throws Exception {
        String sampleName = getSampleName(Paths.get(sourceFilePath));
        MyImportClinicalData cd = new MyImportClinicalData(p.getTargetStudyName());
        int cancerStudyId = MyCancerStudy.getCancerStudyId(con, p.getTargetStudyName());
        if (cd.doesSampleIdExistInCancerStudy(con, cancerStudyId, sampleName)) {
            throw new RuntimeException("sample " + sampleName + " for patient" + sampleName + " already exist, skipping");
        }

        Path dataFilePath = new ExcelAdaptor(sourceFilePath).run();

        File dataFile = dataFilePath.toFile();
        System.out.println("dataFile: " + dataFile);

// commit switch off autocommit, and then commit
        MyImportProfileData pd = new MyImportProfileData();
        MyAddCaseList cl = new MyAddCaseList();
        try {
            con.setAutoCommit(false);

            int sampleId = cd.addSample(con, cancerStudyId, sampleName, sampleName);
            int geneticProfileId = cd.getGeneticProfileId(con, cancerStudyId);

            pd.run(con, geneticProfileId, dataFile, sampleId);

            cl.addSampleToList(con, cancerStudyId, sampleId);
            //  throw new RuntimeException("not readY!!!!");
            con.commit();
        } finally {
            System.out.println("importFile:deleting " + dataFilePath);
            Files.delete(dataFilePath);
            System.out.println("importFile:deleted " + dataFilePath);
        }
    }

    public void runImportWithFilePath(Path sourceFilePath) throws Exception {
        FileUtils fu = new FileUtils(p.getImportedDirPath(), p.getRejectedDirPath());
        try (Connection con = getConnection()) {
            importFile(con, sourceFilePath.toString());
        } catch (Exception ex) {
            System.out.println("Exception:" + ex.getMessage());
            fu.moveFileToRejected(sourceFilePath);
            throw ex;
        }
// everything is fine
        fu.moveFileToImported(sourceFilePath);
    }

    public void runImport(String sourceFilePathString) throws Exception {
        Path sourceFilePath = p.getSourceFilePath(sourceFilePathString);
        runImportWithFilePath(sourceFilePath);
    }

    public static void main(String... args) throws Exception {
        String SOURCE_FILE_NAME = "Mix_cell-line44.hg19_coding01.Tab.xlsx";
        MyImport i = new MyImport(new MyProperties());
        i.runImport(SOURCE_FILE_NAME);
    }
}
