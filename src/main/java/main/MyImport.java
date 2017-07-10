/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import folder.ExcelAdaptor;
import java.io.File;
import java.nio.file.Path;
import java.sql.Connection;
import persistence.MyAddCaseList;
import static persistence.MyConnection.closeConnection;
import static persistence.MyConnection.getConnection;
import persistence.MyImportClinicalData;
import persistence.MyImportProfileData;

/**
 *
 * @author mcaikovs
 */
public class MyImport {

    static String STUDY_NAME = "acc_tcga_chuv3";
    String sourceFilePath = "C:\\Projects\\cBioPortal\\data sample\\Mix_cell-line13.hg19_coding01.Tab.xlsx";

    String getSampleName(Path path) {
        String fileName = path.getFileName().toString();
        String sampleName = fileName.substring(0, fileName.indexOf("."));
        System.out.println("sampleName: " + sampleName);
        if (sampleName == null || sampleName.isEmpty()) {
            throw new RuntimeException("Cannot extract a sample name from path " + path);
        }
        return sampleName;
    }

    void run(Connection con) throws Exception {
        Path dataFilePath = new ExcelAdaptor(sourceFilePath).run();

        File dataFile = dataFilePath.toFile();
        System.out.println("dataFile: " + dataFile);

        String sampleName = getSampleName(dataFilePath);

// commit switch off autocommit, and then commit
        MyImportClinicalData cd = new MyImportClinicalData(STUDY_NAME);
        MyImportProfileData pd = new MyImportProfileData();
        MyAddCaseList cl = new MyAddCaseList();

        con.setAutoCommit(false);
        int cancerStudyId = cd.getCancerStudyId(con, STUDY_NAME);
        int sampleId = cd.addSample(con, cancerStudyId, sampleName, sampleName);
        int geneticProfileId = cd.getGeneticProfileId(con, cancerStudyId);
        pd.run(con, geneticProfileId, dataFile, sampleId);
        cl.addSampleToList(con, cancerStudyId, sampleId);
        //  throw new RuntimeException("not readY!!!!");
        con.commit();

    }

    public static void main(String... args) throws Exception {
        Connection con = getConnection();
        try {
            new MyImport().run(con);
        } catch (Exception ex) {
            System.out.println("Exception:" + ex);
            ex.printStackTrace();
        } finally {
            closeConnection();
        }
    }
}
