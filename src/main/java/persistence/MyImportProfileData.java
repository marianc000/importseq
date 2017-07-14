/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package persistence;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import org.mskcc.cbio.portal.dao.DaoException;

/**
 *
 * @author mcaikovs
 */
public class MyImportProfileData {

    public void run(Connection con ,int geneticProfileId, File dataFile, int sampleId  ) throws IOException, DaoException, SQLException {

        System.out.println("MyImportProfileData: Reading data from:  " + dataFile.getAbsolutePath());
       // SpringUtil.initDataSource();
        MyImportExtendedMutationData importer = new MyImportExtendedMutationData(dataFile, geneticProfileId, null);

        importer.importData(con,sampleId);
        System.out.println("<MyImportProfileData");

    }

}
