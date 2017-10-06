/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package files;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.nio.file.Files;

import utils.FileUtils;

/**
 *
 * @author mcaikovs
 */
public class MySourceFile {

    public MySourceFile(Path filePath) {
        this.filePath = filePath;
        fu = new FileUtils(getFileDir());
    }

    public MySourceFile(String filePath) {
        this(Paths.get(filePath));
    }
    FileUtils fu;
    Path filePath;

    public String getSampleName() {
        return FileUtils.getSampleName(filePath);
    }

    public Path getFilePath() {
        return filePath;
    }

    public String getFilePathString() {
        return filePath.toString();
    }

    public Path getFileDir() {
        return filePath.getParent();
    }

    void moveToImported() throws IOException {
        fu.moveFileToImported(filePath);
    }

    void moveToFailed() throws IOException {
        fu.moveFileToRejected(filePath);
    }

    boolean isGeneticDataFile() {
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 89 * hash + Objects.hashCode(this.filePath);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final MySourceFile other = (MySourceFile) obj;
        if (!Objects.equals(this.filePath, other.filePath)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return filePath.toString();
    }

}
