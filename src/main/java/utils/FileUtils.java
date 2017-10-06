/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import java.util.LinkedList;
import java.util.List;

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

    public static String REJECTED_FOLDER_NAME = "rejected", IMPORTED_FOLDER_NAME = "imported", ERROR_FILE_EXTENSION = ".err";

    public FileUtils(Path baseDir) {
        this.imported = baseDir.resolve(IMPORTED_FOLDER_NAME);
        this.rejected = baseDir.resolve(REJECTED_FOLDER_NAME);
    }

    void createParentDirIfDoesNotExist(Path filePath) throws IOException {
        Path dirPath = filePath.getParent();
        if (!Files.isDirectory(dirPath)) {
            System.out.println(">Creating dir: " + dirPath);
            Files.createDirectory(dirPath);
        }
    }

    public Path getImported() {
        return imported;
    }

    public Path getRejected() {
        return rejected;
    }

    void moveFile(Path filePath, Path destination) throws IOException {
        System.out.println(">moveFile: " + filePath + " -> " + destination);
        createParentDirIfDoesNotExist(destination);
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

  public  void saveStackTrace(Path filePath, Exception ex) throws IOException {

        List<String> st = new LinkedList<>();
        for (StackTraceElement e : ex.getStackTrace()) {
            st.add(e.toString());
        }
        Path targetFilePath = getErrorFilePath(filePath);
        createParentDirIfDoesNotExist(targetFilePath);
        Files.write(targetFilePath, st);
    }

  public  Path getErrorFilePath(Path filePath) {
        return rejected.resolve(filePath.getFileName() + ERROR_FILE_EXTENSION);
    }

    public static String getSampleName(Path path) {
        String fileName = path.getFileName().toString();
        String sampleName = fileName.substring(0, fileName.indexOf("."));
        //   System.out.println("sampleName: " + sampleName);
        if (sampleName == null || sampleName.isEmpty()) {
            throw new RuntimeException("Cannot extract a sample name from path " + path);
        }
        return sampleName;
    }

    public static String getSampleName(String path) {
        return getSampleName(Paths.get(path));
    }

    public static String convertFilePathToSampleName(String filePath) {
        return getSampleName(Paths.get(filePath));
    }

    public static void saveDocument(Path path, List<List<String>> doc) throws IOException {
        List<String> content = new LinkedList<>();
        for (int r = 0; r < doc.get(0).size(); r++) {
            StringBuilder sb = new StringBuilder();
            for (int c = 0; c < doc.size(); c++) {
                sb.append(doc.get(c).get(r));
                if (c < doc.size() - 1) {
                    sb.append("\t");
                }
            }
            content.add(sb.toString());
        }
        Files.write(path, content);
    }

    public static Path getOutputFilePath(String sourceFilePath) {
        return Paths.get(sourceFilePath + ".out");
    }

}
