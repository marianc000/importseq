/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

/**
 *
 * @author mcaikovs
 */
public class FileUtils {

    Path imported, rejected;

    public FileUtils(Path imported, Path rejected) {
        this.imported = imported;
        this.rejected = rejected;
    }

    void moveFile(Path filePath, Path destination) throws IOException {
        System.out.println(">moveFile: " + filePath + " -> " + destination);
        Files.move(filePath, destination, REPLACE_EXISTING);
    }

    public void moveFileToImported(Path filePath) throws IOException {
        System.out.println(">moveFileToImported");
        moveFile(filePath, imported.resolve(filePath.getFileName()));
    }

    public void moveFileToRejected(Path filePath) throws IOException {
        System.out.println(">moveFileToRejected");
        moveFile(filePath, rejected.resolve(filePath.getFileName()));
    }
}
