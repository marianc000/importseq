/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 *
 * @author mcaikovs
 */
public class MyProperties extends Properties {

    static String PROPERTIES_FILE_NAME = "/my.properties";
    static String SOURCE_DIR_KEY = "SOURCE_DIR";
    static String IMPORTED_DIR_KEY = "IMPORTED_DIR";
    static String REJECTED_DIR_KEY = "REJECTED_DIR";
    static String STUDY_NAME_KEY = "STUDY_NAME";

    public MyProperties() throws IOException {
        loadMyProperties();
    }

    public MyProperties(String resourceName) throws IOException {
        loadMyProperties(resourceName);
    }

    final void loadMyProperties() throws IOException {
        loadMyProperties(PROPERTIES_FILE_NAME);
    }

    final void loadMyProperties(String resourceName) throws IOException {
        try (InputStream is = getClass().getResourceAsStream(resourceName)) {
            load(is);
        }
    }

    public String getTargetStudyName() {
        String s = getProperty(STUDY_NAME_KEY).trim();
        System.out.println(">getTargetStudyName: " + s);
        return s;
    }

    public String getSourceDir() {
        String s = getProperty(SOURCE_DIR_KEY).trim();
        System.out.println(">getSourceDir: " + s);
        return s;
    }

    public String getImportedDir() {
        String s = getProperty(IMPORTED_DIR_KEY).trim();
        System.out.println(">getImportedDir: " + s);
        return s;
    }

    public String getRejectedDir() {
        String s = getProperty(REJECTED_DIR_KEY).trim();
        System.out.println(">getRejectedDir: " + s);
        return s;
    }

    public Path getSourceDirPath() {
        return Paths.get(getSourceDir());
    }

    public Path getSourceFilePath(String fileName) {
        return Paths.get(getSourceDir()).resolve(fileName);
    }

    public Path getImportedDirPath() {
        return Paths.get(getImportedDir());
    }

    public Path getRejectedDirPath() {

        return Paths.get(getRejectedDir());
    }

    public static void main(String... args) throws Exception {
        MyProperties i = new MyProperties();
        for (String s : i.stringPropertyNames()) {
            System.out.println(s + "\t" + i.getProperty(s));
        }
    }
}
