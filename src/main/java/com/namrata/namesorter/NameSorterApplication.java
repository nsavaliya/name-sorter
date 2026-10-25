package com.namrata.namesorter;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.List;

/** Coordinates the use case and maps expected failures to CLI exit codes. */
public final class NameSorterApplication {
    public static final String OUTPUT_FILE = "sorted-names-list.txt";
    private static final String USAGE = "Usage: name-sorter <input-file>";

    private final NameStore store;
    private final NameSorter sorter;

    public NameSorterApplication(NameStore store, NameSorter sorter) {
        this.store = store;
        this.sorter = sorter;
    }

    public int run(String[] arguments, Path workingDirectory, PrintStream stdout, PrintStream stderr) {
        if (arguments.length == 1
                && (arguments[0].equals("--help") || arguments[0].equals("-h"))) {
            stdout.println(USAGE);
            return 0;
        }
        if (arguments.length != 1) {
            stderr.println(USAGE);
            return 2;
        }

        try {
            Path input = workingDirectory.resolve(arguments[0]).normalize();
            List<PersonName> sorted = sorter.sort(store.read(input));
            // Finish validation and file output before printing any sorted names.
            store.write(workingDirectory.resolve(OUTPUT_FILE), sorted);
            sorted.forEach(name -> stdout.println(name.fullName()));
            if (stdout.checkError()) {
                stderr.println("I/O error: Unable to write names to standard output.");
                return 1;
            }
            return 0;
        } catch (InvalidPathException invalidPath) {
            stderr.println("Invalid path: " + invalidPath.getMessage());
            return 2;
        } catch (IllegalArgumentException invalidName) {
            stderr.println("Invalid input: " + invalidName.getMessage());
            return 1;
        } catch (IOException ioFailure) {
            stderr.println("I/O error: " + ioFailure.getMessage());
            return 1;
        }
    }
}
