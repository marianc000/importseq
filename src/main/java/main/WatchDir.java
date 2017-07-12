package main;

import java.nio.file.*;
import static java.nio.file.StandardWatchEventKinds.*;
import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Example to watch a directory (or tree) for changes to files.
 */
public class WatchDir {

    private final WatchService watcher;
    private final Map<WatchKey, Path> keys;

    @SuppressWarnings("unchecked")
    static <T> WatchEvent<T> cast(WatchEvent<?> event) {
        return (WatchEvent<T>) event;
    }

    /**
     * Register the given directory with the WatchService
     */
    private void register(Path dir) throws IOException {
        WatchKey key = dir.register(watcher, ENTRY_CREATE);

        Path prev = keys.get(key);
        if (prev == null) {
            System.out.format("register: %s\n", dir);
        } else {
            if (!dir.equals(prev)) {
                System.out.format("update: %s -> %s\n", prev, dir);
            }
        }

        keys.put(key, dir);
    }

    MyProperties p;
    MyImport i;

    WatchDir() throws IOException {
        p = new MyProperties();
        i = new MyImport(p);

        this.watcher = FileSystems.getDefault().newWatchService();
        this.keys = new HashMap<>();

        register(p.getSourceDirPath());
    }

    /**
     * Process all events for keys queued to the watcher
     */
    void processEvents() {
        for (;;) {

            // wait for key to be signalled
            WatchKey key;
            try {
                key = watcher.take();
            } catch (InterruptedException x) {
                return;
            }

            Path dir = keys.get(key);
            if (dir == null) {
                System.err.println("WatchKey not recognized!!");
                continue;
            }

            for (WatchEvent<?> event : key.pollEvents()) {
                WatchEvent.Kind kind = event.kind();

                // TBD - provide example of how OVERFLOW event is handled
                if (kind == OVERFLOW) {
                    continue;
                }

                // Context for directory entry event is the file name of entry
                WatchEvent<Path> ev = cast(event);
                Path name = ev.context();
                Path child = dir.resolve(name);

                // print out event
                System.out.println(">processEvents:detected: " + event.kind().name() + "; " + child + "; " + name);
                processFile(child);
            }

            // reset key and remove from set if directory no longer accessible
            boolean valid = key.reset();
            if (!valid) {
                keys.remove(key);

                // all directories are inaccessible
                if (keys.isEmpty()) {
                    break;
                }
            }
        }
    }

    static String ACCEPTABLE_SOURCE_FILE_EXTENSION = ".xlsx";

    static boolean isAcceptable(Path filePath) {
        return filePath.getFileName().toString().endsWith(ACCEPTABLE_SOURCE_FILE_EXTENSION);
    }

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
        while (!isComplete(filePath)) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {

            }
        }
    }

    void processFile(Path filePath) {
        System.out.println(">processFile: " + filePath);
        if (!isAcceptable(filePath)) {
            System.out.println(">processFile: unacceptable " + filePath);
            return;
        }
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        waitUntilComplete(filePath);
        try {
            i.runImportWithFilePath(filePath);
        } catch (Exception e) {
            System.out.println("processFile exception: ");
            e.printStackTrace();
        }

        System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<processFile: " + filePath);
        System.out.println();
    }

    public static void main(String[] args) throws IOException {
        new WatchDir().processEvents();
    }
}
