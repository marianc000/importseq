/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import folder.ExcelAdaptor;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.mskcc.cbio.portal.dao.DaoException;
import persistence.MyAddCaseList;
import static persistence.MyConnection.getConnection;
import persistence.MyImportClinicalData;
import persistence.MyImportProfileData;

/**
 *
 * @author mcaikovs
 */
public class MyImport {

    static String STUDY_NAME = "acc_tcga_chuv3";
    String sourceFilePath = "C:\\Projects\\cBioPortal\\data sample\\Mix_cell-line2.hg19_coding01.Tab.xlsx";

    String getSampleName(Path path) {
        String fileName = path.getFileName().toString();
        String sampleName = fileName.substring(0, fileName.indexOf("."));
        System.out.println("sampleName: " + sampleName);
        if (sampleName == null || sampleName.isEmpty()) {
            throw new RuntimeException("Cannot extract a sample name from path " + path);
        }
        return sampleName;
    }

    void run() throws Exception {
        Path dataFilePath = new ExcelAdaptor(sourceFilePath).run();

        File dataFile = dataFilePath.toFile();
        System.out.println("dataFile: " + dataFile);

        String sampleName = getSampleName(dataFilePath);

// commit switch off autocommit, and then commit
        MyImportClinicalData cd = new MyImportClinicalData(STUDY_NAME);
        MyImportProfileData pd = new MyImportProfileData();
        MyAddCaseList cl = new MyAddCaseList();
        try (Connection con = getConnection()) {
            con.setAutoCommit(false);
            int cancerStudyId = cd.getCancerStudyId(con, STUDY_NAME);
            int sampleId = cd.addSample(con, cancerStudyId, sampleName, sampleName);
            pd.run(cd.getGeneticProfileId(con, cancerStudyId), dataFile);
            cl.addSampleToList(con, cancerStudyId, sampleId);
            con.commit();
        }
    }

    public static void main(String... args) throws Exception {
        try {
            new MyImport().run();
        } catch (Exception ex) {
            System.out.println("Exception:" + ex);
            ex.printStackTrace();
        }
    }
}
