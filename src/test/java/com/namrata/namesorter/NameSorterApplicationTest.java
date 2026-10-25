package com.namrata.namesorter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class NameSorterApplicationTest {
    @TempDir
    Path workingDirectory;

    private final ByteArrayOutputStream output = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errors = new ByteArrayOutputStream();
    private final NameSorterApplication application = new NameSorterApplication(
            new TextFileNameStore(new NameParser()), new NameSorter());

    @Test
    void reproducesTheAssessmentExampleOnScreenAndInTheOutputFile() throws IOException {
        Path example = Path.of("examples/unsorted-names-list.txt").toAbsolutePath();
        String expected = Files.readString(Path.of("examples/expected-sorted-names-list.txt"));
        assertEquals(0, run(example.toString()));
        assertEquals(expected.lines().toList(), stdout().lines().toList());
        assertEquals(expected, Files.readString(resultFile()));
        assertEquals("", stderr());
    }

    @Test
    void sortsOneThousandNamesAgainstAnIndependentExpectedOrder() throws IOException {
        List<String> expected = new ArrayList<>();
        // Fixed-width keys produce a known ordering independently of the production comparator.
        for (int surname = 0; surname < 10; surname++) {
            for (int given = 0; given < 100; given++) {
                expected.add("Given" + String.format("%03d", given) + " Surname" + surname);
            }
        }
        List<String> shuffled = new ArrayList<>(expected);
        Collections.shuffle(shuffled, new Random(42));
        Files.write(workingDirectory.resolve("thousand.txt"), shuffled, StandardCharsets.UTF_8);

        assertEquals(0, run("thousand.txt"));
        assertEquals(expected, stdout().lines().toList());
        assertEquals(expected, Files.readAllLines(resultFile(), StandardCharsets.UTF_8));
        assertEquals(1000, stdout().lines().count());
    }

    @Test
    void preservesDuplicatesInTheCompleteWorkflow() throws IOException {
        Files.writeString(workingDirectory.resolve("names.txt"), "Zoe Smith\nAna Brown\nZoe Smith\n");
        assertEquals(0, run("names.txt"));
        assertEquals(List.of("Ana Brown", "Zoe Smith", "Zoe Smith"), Files.readAllLines(resultFile()));
    }

    @Test
    void writesOutputInTheWorkingDirectoryRatherThanTheInputDirectory() throws IOException {
        Path inputDirectory = Files.createDirectory(workingDirectory.resolve("inputs"));
        Files.writeString(inputDirectory.resolve("names with spaces.txt"), "Zoe Smith\nAna Brown\n");
        assertEquals(0, run("inputs/names with spaces.txt"));
        assertTrue(Files.exists(resultFile()));
        assertFalse(Files.exists(inputDirectory.resolve(NameSorterApplication.OUTPUT_FILE)));
    }

    @Test
    void invalidInputKeepsExistingOutputAndPrintsNoPartialResults() throws IOException {
        Files.writeString(resultFile(), "Existing Result\n");
        Files.writeString(workingDirectory.resolve("invalid.txt"), "Ana Smith\nOnlyOneToken\n");
        assertEquals(1, run("invalid.txt"));
        assertEquals("Existing Result\n", Files.readString(resultFile()));
        assertEquals("", stdout());
        assertTrue(stderr().contains("Invalid input:"));
        assertTrue(stderr().contains("invalid.txt:2:"));
    }

    @Test
    void missingInputKeepsExistingOutputAndReturnsAnError() throws IOException {
        Files.writeString(resultFile(), "Existing Result\n");
        assertEquals(1, run("missing.txt"));
        assertEquals("Existing Result\n", Files.readString(resultFile()));
        assertEquals("", stdout());
        assertTrue(stderr().startsWith("I/O error:"));
    }

    @Test
    void outputFailurePrintsNoSuccessResults() throws IOException {
        Files.writeString(workingDirectory.resolve("names.txt"), "Ana Smith\n");
        Files.createDirectory(resultFile());
        Files.writeString(resultFile().resolve("keep.txt"), "keep");
        assertEquals(1, run("names.txt"));
        assertEquals("", stdout());
        assertTrue(stderr().startsWith("I/O error:"));
    }

    @Test
    void emptyInputOverwritesOldOutputWithAnEmptyFile() throws IOException {
        Files.writeString(workingDirectory.resolve("empty.txt"), " \n\t\n");
        Files.writeString(resultFile(), "Old Result\n");
        assertEquals(0, run("empty.txt"));
        assertEquals(0, Files.size(resultFile()));
        assertEquals("", stdout());
        assertEquals("", stderr());
    }

    @Test
    void canSafelyReadAndReplaceTheSameFile() throws IOException {
        Files.writeString(resultFile(), "Zoe Smith\nAna Brown\n");
        assertEquals(0, run(NameSorterApplication.OUTPUT_FILE));
        assertEquals(List.of("Ana Brown", "Zoe Smith"), Files.readAllLines(resultFile()));
    }

    @Test
    void noArgumentsReturnUsageErrorWithoutCreatingOutput() {
        assertEquals(2, run());
        assertEquals("", stdout());
        assertTrue(stderr().contains("Usage: name-sorter <input-file>"));
        assertFalse(Files.exists(resultFile()));
    }

    @Test
    void tooManyArgumentsReturnUsageError() {
        assertEquals(2, run("one.txt", "two.txt"));
        assertTrue(stderr().contains("Usage:"));
    }

    @Test
    void helpReturnsSuccessWithoutCreatingOutput() {
        assertEquals(0, run("--help"));
        assertTrue(stdout().contains("Usage:"));
        assertEquals("", stderr());
        assertFalse(Files.exists(resultFile()));
    }

    @Test
    void shortHelpOptionReturnsSuccess() {
        assertEquals(0, run("-h"));
        assertTrue(stdout().contains("Usage:"));
    }

    @Test
    void invalidPathReturnsUsageError() {
        assertEquals(2, run("bad\u0000path"));
        assertTrue(stderr().startsWith("Invalid path:"));
        assertEquals("", stdout());
    }

    @Test
    void brokenStandardOutputReturnsAnIoErrorAfterSavingTheFile() throws IOException {
        Files.writeString(workingDirectory.resolve("names.txt"), "Ana Smith\n");
        PrintStream brokenOutput = new PrintStream(new OutputStream() {
            @Override
            public void write(int value) throws IOException {
                throw new IOException("Output is closed");
            }
        });
        int code = application.run(new String[]{"names.txt"}, workingDirectory,
                brokenOutput, new PrintStream(errors, true, StandardCharsets.UTF_8));
        assertEquals(1, code);
        assertEquals("Ana Smith\n", Files.readString(resultFile()));
        assertTrue(stderr().contains("Unable to write names to standard output"));
    }

    private int run(String... arguments) {
        return application.run(arguments, workingDirectory,
                new PrintStream(output, true, StandardCharsets.UTF_8),
                new PrintStream(errors, true, StandardCharsets.UTF_8));
    }

    private Path resultFile() {
        return workingDirectory.resolve(NameSorterApplication.OUTPUT_FILE);
    }

    private String stdout() {
        return output.toString(StandardCharsets.UTF_8);
    }

    private String stderr() {
        return errors.toString(StandardCharsets.UTF_8);
    }
}
