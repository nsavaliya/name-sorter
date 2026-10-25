package com.namrata.namesorter;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

/** Wires concrete dependencies; all testable application behavior lives outside main. */
public final class NameSorterCli {
    private NameSorterCli() {
    }

    public static void main(String[] arguments) {
        NameStore store = new TextFileNameStore(new NameParser());
        NameSorterApplication application = new NameSorterApplication(store, new NameSorter());
        int exitCode = application.run(arguments, Path.of("").toAbsolutePath(),
                new PrintStream(System.out, true, StandardCharsets.UTF_8),
                new PrintStream(System.err, true, StandardCharsets.UTF_8));
        System.exit(exitCode);
    }
}
