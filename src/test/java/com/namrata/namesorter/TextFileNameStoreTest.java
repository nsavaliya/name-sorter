package com.namrata.namesorter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TextFileNameStoreTest {
    @TempDir
    Path temporaryDirectory;

    private final TextFileNameStore store = new TextFileNameStore(new NameParser());

    @Test
    void readsUtf8BomCrLfBlankLinesAndNamesWithoutAFinalNewline() throws IOException {
        Path input = temporaryDirectory.resolve("names.txt");
        Files.writeString(input, "\uFEFFÉlodie O'Neill\r\n\r\n \t\r\nAna Smith", StandardCharsets.UTF_8);
        assertEquals(List.of("Élodie O'Neill", "Ana Smith"),
                store.read(input).stream().map(PersonName::fullName).toList());
    }

    @Test
    void reportsPhysicalLineNumbersForInvalidNames() throws IOException {
        Path input = temporaryDirectory.resolve("invalid.txt");
        Files.writeString(input, "Ana Smith\n\nMadonna\n", StandardCharsets.UTF_8);
        IllegalArgumentException failure = assertThrows(IllegalArgumentException.class, () -> store.read(input));
        assertTrue(failure.getMessage().contains("invalid.txt:3:"));
        assertTrue(failure.getMessage().contains("1 to 3 given names"));
    }

    @Test
    void reportsAMissingInputFile() {
        assertThrows(IOException.class, () -> store.read(temporaryDirectory.resolve("missing.txt")));
    }

    @Test
    void rejectsMalformedUtf8() throws IOException {
        Path input = temporaryDirectory.resolve("bad-encoding.txt");
        Files.write(input, new byte[]{(byte) 0xC3, (byte) 0x28});
        assertThrows(IOException.class, () -> store.read(input));
    }

    @Test
    void overwritesOldOutputUsingUtf8AndLfNewlines() throws IOException {
        Path output = temporaryDirectory.resolve("sorted-names-list.txt");
        Files.writeString(output, "Old Content\nExtra Stale Row\n", StandardCharsets.UTF_8);
        store.write(output, List.of(new PersonName(List.of("Élodie"), "O'Neill")));
        assertArrayEquals("Élodie O'Neill\n".getBytes(StandardCharsets.UTF_8), Files.readAllBytes(output));
        assertNoTemporaryFiles();
    }

    @Test
    void writesAnEmptyFileForAnEmptyList() throws IOException {
        Path output = temporaryDirectory.resolve("empty.txt");
        store.write(output, List.of());
        assertEquals(0, Files.size(output));
    }

    @Test
    void cleansUpTemporaryFilesWhenOutputReplacementFails() throws IOException {
        Path output = Files.createDirectory(temporaryDirectory.resolve("sorted-names-list.txt"));
        Files.writeString(output.resolve("keep.txt"), "keep", StandardCharsets.UTF_8);
        assertThrows(IOException.class,
                () -> store.write(output, List.of(new PersonName(List.of("Ana"), "Smith"))));
        assertEquals("keep", Files.readString(output.resolve("keep.txt")));
        assertNoTemporaryFiles();
    }

    private void assertNoTemporaryFiles() throws IOException {
        try (var files = Files.list(temporaryDirectory)) {
            assertTrue(files.noneMatch(path -> path.getFileName().toString().startsWith(".name-sorter-")));
        }
    }
}
