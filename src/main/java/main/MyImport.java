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
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.mskcc.cbio.portal.dao.DaoException;
import static persistence.MyConnection.getConnection;
import persistence.MyImportClinicalData;
import persistence.MyImportProfileData;

/**
 *
 * @author mcaikovs
 */
public class MyImport {

    static String STUDY_NAME = "acc_tcga_chuv3";
    static String sourceFilePath = "C:\\Projects\\cBioPortal\\data sample\\Mix_cell-line.hg19_coding01.Tab.xlsx";

    String getSampleName(Path path) {
        String fileName = path.getFileName().toString();
        return fileName.substring(0, fileName.indexOf("."));
    }

    void run() throws IOException, InvalidFormatException, SQLException, DaoException {
        Path dataFilePath = new ExcelAdaptor("C:\\Projects\\cBioPortal\\data sample\\Mix_cell-line.hg19_coding01.Tab.xlsx").run();

        File dataFile = dataFilePath.toFile();
        String sampleName = getSampleName(dataFilePath);
// commit switch off autocommit, and then commit
        MyImportClinicalData cd = new MyImportClinicalData(STUDY_NAME);
        MyImportProfileData pd=new MyImportProfileData();
        try (Connection con = getConnection()) {
            con.setAutoCommit(false);
            cd.addSample(con, STUDY_NAME, sampleName, sampleName);
            pd.run(0, dataFile);
            con.commit();
        }
    }

    public static void main(String... args) throws IOException, InvalidFormatException, SQLException {
        new MyImport().run();

    }
}
