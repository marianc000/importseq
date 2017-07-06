/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package persistence;

import java.io.File;
import java.io.IOException;
import org.mskcc.cbio.portal.dao.DaoException;
import org.mskcc.cbio.portal.scripts.ImportExtendedMutationData;
import org.mskcc.cbio.portal.util.SpringUtil;

/**
 *
 * @author mcaikovs
 */
public class MyImportProfileData {

    public void run(int geneticProfileId, File dataFile) throws IOException, DaoException {

        System.out.println("Reading data from:  " + dataFile.getAbsolutePath());
        SpringUtil.initDataSource();
        ImportExtendedMutationData importer = new ImportExtendedMutationData(dataFile, geneticProfileId, null);

        importer.importData();

    }

}
