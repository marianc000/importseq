/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package files;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.*;
import static java.nio.file.FileVisitResult.*;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 *
 * @author mcaikovs
 */
public class FileFinder {

    public class Finder extends SimpleFileVisitor<Path> {

        private final PathMatcher matcher;
        private int numMatches = 0;

        Finder(String pattern) {
            matcher = FileSystems.getDefault()
                    .getPathMatcher("glob:" + pattern);
        }

        // Compares the glob pattern against
        // the file or directory name.
        void find(Path file) {
            Path name = file.getFileName();

            if (name != null && matcher.matches(name)) {
                numMatches++;
                System.out.println(file);
                mutationFilePaths.add(file);
            }
        }

        // Prints the total number of
        // matches to standard out.
        void done() {
            System.out.println("Matched: " + numMatches);
        }

        // Invoke the pattern matching
        // method on each file.
        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
            find(file);
            return CONTINUE;
        }

        // Invoke the pattern matching
        // method on each directory.
        @Override
        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
            System.out.println("preVisitDirectory: " + dir);
            return CONTINUE;
        }

        @Override
        public FileVisitResult visitFileFailed(Path file, IOException exc) {
            System.err.println(exc);
            return CONTINUE;
        }
    }

    static String SOURCE_FILE_NAME_PATTERN = "*.Tab.xlsx";

    Set<Path> mutationFilePaths;

    public Set<Path> run(String sourceDir) throws IOException {
        mutationFilePaths = new HashSet<>();
        Finder finder = new Finder(SOURCE_FILE_NAME_PATTERN);
        Files.walkFileTree(Paths.get(sourceDir), finder);
        finder.done();
        return mutationFilePaths;
    }

    public Set<String> getFileNames() throws IOException {

        Set<String> fileNames = new HashSet<String>();
        for (Path p : mutationFilePaths) {
            fileNames.add(p.getFileName().toString());
        }
        return fileNames;
    }

    public Set<String> getSampleNames() throws IOException {

        Set<String> sampleNames = new HashSet<>();
        for (String s : getFileNames()) {
            String name = s.substring(0, s.indexOf('.'));
            sampleNames.add(name);
        }
        return sampleNames;
    }

    public static void main(String[] args) throws IOException {
        new FileFinder().run("C:\\Projects\\cBioPortal\\data sample\\SECOND SAMPLES\\");
    }
}
