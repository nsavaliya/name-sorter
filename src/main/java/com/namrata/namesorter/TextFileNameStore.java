package com.namrata.namesorter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

/** Reads UTF-8 names and replaces output only after a complete temporary file is written. */
public final class TextFileNameStore implements NameStore {
    private final NameParser parser;

    public TextFileNameStore(NameParser parser) {
        this.parser = parser;
    }

    @Override
    public List<PersonName> read(Path input) throws IOException {
        List<PersonName> names = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(input, StandardCharsets.UTF_8)) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                // Some editors add a UTF-8 byte order mark at the start of a file.
                if (lineNumber == 1 && line.startsWith("\uFEFF")) {
                    line = line.substring(1);
                }
                try {
                    parser.parse(line).ifPresent(names::add);
                } catch (IllegalArgumentException invalidName) {
                    throw new IllegalArgumentException(
                            input + ":" + lineNumber + ": " + invalidName.getMessage(), invalidName);
                }
            }
        }
        return List.copyOf(names);
    }

    @Override
    public void write(Path output, List<PersonName> names) throws IOException {
        Path temporary = Files.createTempFile(output.toAbsolutePath().getParent(), ".name-sorter-", ".tmp");
        try {
            try (BufferedWriter writer = Files.newBufferedWriter(temporary, StandardCharsets.UTF_8)) {
                for (PersonName name : names) {
                    writer.write(name.fullName());
                    writer.write('\n');
                }
            }
            try {
                Files.move(temporary, output, StandardCopyOption.REPLACE_EXISTING,
                        StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException unsupported) {
                // The replacement still works on filesystems that do not support atomic moves.
                Files.move(temporary, output, StandardCopyOption.REPLACE_EXISTING);
            }
        } finally {
            Files.deleteIfExists(temporary);
        }
    }
}
