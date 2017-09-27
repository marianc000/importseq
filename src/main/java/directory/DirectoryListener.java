/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package directory;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import static utils.CollectionUtils.printCollection;

/**
 *
 * @author mcaikovs
 */
public class DirectoryListener {

    Set<Path> previousSnapShot = new HashSet<>();

    Set<Path> readDirectory(Path dir) throws IOException {
        Set<Path> files = new HashSet<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, "*.xlsx")) {
            for (Path file : stream) {
                files.add(file);
            }
        }
        return files;
    }

    boolean listening = false;
    static int PAUSE = 3000;

    void listen(Path dir) throws IOException, InterruptedException {
        if (listening) {
            System.out.println("Already listening");
            return;
        }
        listening = true;
        previousSnapShot = readDirectory(dir);
        while (listening) {
            try {
                Thread.sleep(PAUSE);
            } catch (InterruptedException ex) {
                System.out.println("Interrupted exception");
                break;
            }
            Set<Path> newSnapshot = readDirectory(dir);
            Set<Path> newFiles = new HashSet<>(newSnapshot);

            newFiles.removeAll(previousSnapShot);
            previousSnapShot = newSnapshot;
            if (newFiles.isEmpty()) {
                System.out.println("no change");
            } else {
                processNewFiles(newFiles);
            }
        }
    }

    void processNewFiles(Set<Path> files) {
        System.out.println(">processNewFiles");
        printCollection(files);
        System.out.println("<processNewFiles");
    }

    public static void main(String... args) throws Exception {
        // new DirectoryListener().readDirectory(Paths.get("L:\\SHARE\\RRO_IPA_FILES"));
        new DirectoryListener().listen(Paths.get("L:\\SHARE\\RRO_IPA_FILES"));
    }
}
