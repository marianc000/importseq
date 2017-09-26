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
public class MyPropertiesForVal extends Properties {

    static String PROPERTIES_FILE_NAME = "/my.properties";
    static String SOURCE_DIR_KEY = "SOURCE_DIR";
    static String IMPORTED_DIR_KEY = "IMPORTED_DIR";
    static String REJECTED_DIR_KEY = "REJECTED_DIR";
    static String STUDY_NAME_KEY = "STUDY_NAME";

    public MyPropertiesForVal() throws IOException {
        loadMyProperties();
    }

    public MyPropertiesForVal(String resourceName) throws IOException {
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

    String getSourceDir() {
        String s = getProperty(SOURCE_DIR_KEY).trim();
        System.out.println(">getSourceDir: " + s);
        return s;
    }

    public Path getSourceDirPath() {
        return Paths.get(getSourceDir());
    }

    public static void main(String... args) throws Exception {
        MyPropertiesForVal i = new MyPropertiesForVal();
        for (String s : i.stringPropertyNames()) {
            System.out.println(s + "\t" + i.getProperty(s));
        }
    }
}
