/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package folder;

import excel.LoadValForVal;
import java.io.IOException;
import java.nio.file.Path;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import static utils.FileUtils.getOutputFilePath;

/**
 *
 * @author mcaikovs
 */
public class ExcelAdaptorForValImportFromWatch {

    public Path run(String sourceValFilePath) throws IOException, InvalidFormatException {

        new LoadValForVal().run(sourceValFilePath);
        return getOutputFilePath(sourceValFilePath);
    }

    public static void main(String... args) throws IOException, InvalidFormatException {
        new ExcelAdaptorForValImportFromWatch().run("C:\\Projects\\cBioPortal\\data sample\\SECOND SAMPLES\\corrected\\H1706047-1A.hg19_coding01.Tab.xlsx");
    }
}
