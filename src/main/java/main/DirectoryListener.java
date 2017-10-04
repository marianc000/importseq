package main;

import files.MatchingGeneticClinicalDataFiles;
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

    //   MyProperties p;
    MatchingGeneticClinicalDataFiles fileMatcher;

    boolean isComplete(Path filePath) {
        long s1 = 0, s2 = 0;
        try {
            s1 = Files.size(filePath);
            Thread.sleep(100);
            s2 = Files.size(filePath);
            System.out.println(">isComplete: " + s1 + "; " + s2);
        } catch (Exception ex) {
            return false;
        }
        return (s1 == s2);
    }

    void waitUntilComplete(Path filePath) {
        System.out.println(">waitUntilComplete");
        while (!isComplete(filePath) || !Files.isReadable(filePath) || !Files.isWritable(filePath)) {
            System.out.println("waitUntilComplete:waiting");
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {

            }
        }
    }

    void processFile(Path filePath) {
        System.out.println(">processFile: " + filePath);

        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        waitUntilComplete(filePath);
        try {
            fileMatcher.addFile(filePath);
        } catch (Exception e) {
            System.out.println("processFile exception: ");
            e.printStackTrace();
        }

        System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<processFile: " + filePath);
        System.out.println();
    }

    public DirectoryListener(MyProperties p) throws IOException {
        this(p.getSourceDirPath());
    }

    Path dir;

    public DirectoryListener(Path dir) {
        System.out.println(">WatchDirForVal: watching: " + dir + "; exists=" + Files.exists(dir) + "; isWritable=" + Files.isWritable(dir) + "; isReadable=" + Files.isReadable(dir));

        this.dir = dir;
        fileMatcher = new MatchingGeneticClinicalDataFiles();
    }

    public void stop() throws IOException {
        System.out.println(">WatchDir:stop");
        listening = false;

    }

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

    public void listen() throws IOException {
        if (listening) {
            System.out.println("Already listening");
            return;
        }
        System.out.println("Starting to listen");
        listening = true;
        Set<Path> previousSnapShot = readDirectory(dir);
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
             //   System.out.println("no change");
            } else {
                processNewFiles(newFiles);
            }
        }
        System.out.println("Stopped listening");
    }

    void processNewFiles(Set<Path> files) {
        System.out.println(">processNewFiles");
        printCollection(files);
        for (Path p : files) {
            processFile(p);
        }
        System.out.println("<processNewFiles");
    }

    public static void main(String... args) throws Exception {
        // new DirectoryListener().readDirectory(Paths.get("L:\\SHARE\\RRO_IPA_FILES"));
        new DirectoryListener(Paths.get("L:\\SHARE\\RRO_IPA_FILES")).listen();
    }
}
