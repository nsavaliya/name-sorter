# Name Sorter

Namrata Savaliya - Dye & Durham coding assessment

A Java command-line application that reads a list of names, sorts by surname and
then all given names, prints the result, and creates or replaces
`sorted-names-list.txt` in the current working directory.

## Build and run

Use a JDK 17 or newer. The Maven Wrapper downloads Maven 3.9.9 and the build
dependencies on the first run, so an initial build needs internet access.

**Windows PowerShell**

```powershell
.\mvnw.cmd clean verify
.\name-sorter.cmd .\examples\unsorted-names-list.txt
```

**macOS / Linux**

```sh
chmod +x mvnw name-sorter
./mvnw clean verify
./name-sorter ./examples/unsorted-names-list.txt
```

The packaged application can also be run directly:

```sh
java -jar target/name-sorter.jar examples/unsorted-names-list.txt
```

To use `name-sorter <input-file>` on macOS or Linux, add this directory to `PATH`:

```sh
export PATH="$PWD:$PATH"
name-sorter ./examples/unsorted-names-list.txt
```

Quote paths containing spaces. The output belongs to the directory where the
command is run, even when the input file is elsewhere. Build outputs are generated
locally under `target/` and are excluded from this source submission.

## IntelliJ IDEA

Open `pom.xml` as a Maven project and select JDK 17 as the Project SDK. The shared
**Name Sorter** run configuration in `.run/` supplies the sample input and uses the
project directory as the working directory. Run tests from the Maven `verify`
lifecycle task or with the wrapper commands above. The command-line build is the
same one used by CI.

## Input and ordering

- Each nonblank UTF-8 line contains 1 to 3 given names followed by one surname.
- The last token is the surname. Compound surnames are not inferred.
- Both keys use `String.CASE_INSENSITIVE_ORDER`; displayed spelling and case are
  preserved. This is locale-independent comparison, not linguistic collation.
- All given names are joined with single spaces for the secondary key. A shorter
  matching prefix comes first. Duplicates are retained and equal keys stay in
  input order.
- Blank lines are ignored. Repeated spaces, tabs, and Unicode whitespace are
  normalized. A leading UTF-8 BOM and CRLF input are accepted.
- An invalid nonblank line fails the run and reports its physical line number.
  Validation completes before output replacement. Empty input produces an empty
  output file.

The assessment input contains `Vaugh Lewis`, while its illustrated output contains
`Vaughn Lewis`. The application keeps the input spelling, `Vaugh`. The sample's
expected result is in `examples/expected-sorted-names-list.txt`.

For a surname tie, `examples/tied-surnames.txt` produces `Bob Brown`, `Ana Smith`,
`Ana Maria Smith`, `Ana Maria Jane Smith`, `Ana Marie Smith`, then `Zoe Smith`.

## Design

| Type | Responsibility |
| --- | --- |
| `PersonName` | Immutable name value, valid token counts, and formatting |
| `NameParser` | Whitespace normalization and parsing of one line |
| `NameSorter` | Surname and given-name ordering without I/O |
| `NameStore` | Read and write contract used by the application |
| `TextFileNameStore` | UTF-8 file access, line errors, and file replacement |
| `NameSorterApplication` | Arguments, workflow, console output, and exit codes |
| `NameSorterCli` | Creates dependencies and returns the process exit code |

The application depends on the storage interface through constructor injection.
Sorting copies the input list and uses the standard library's stable sort. It
needs O(n log n) comparisons, with comparison cost depending on name length, and
O(n) records in memory plus their text. There are no runtime library dependencies.

## Output and failures

File output is UTF-8 with LF line endings. Console output is UTF-8 with the
platform line separator. Successful sorting prints only names to stdout; errors
go to stderr. `--help` and `-h` show usage.

| Exit code | Meaning |
| --- | --- |
| 0 | Success, empty input, or help |
| 1 | Invalid name, file I/O failure, or failed console output |
| 2 | Wrong argument count or invalid path syntax |

Output is written to a temporary file in the destination directory and then
replaced, using an atomic move where supported. The fallback uses a regular
replacing move and has weaker crash guarantees. Names are printed after the file
is saved; a later stdout failure leaves the completed file available.

## Verification

`clean verify` compiles the project, runs 51 JUnit test invocations, creates the
executable JAR, and generates a JaCoCo report at `target/site/jacoco/index.html`.
Tests include the supplied sample, surname ties, three given names, duplicates,
Unicode, blank input, invalid rows, file overwrite, working-directory behavior,
I/O errors, and a deterministically shuffled 1,000-name input.

The GitHub Actions workflow builds and tests on Linux and Windows for pushes and
pull requests. Its Linux job also compares the launcher's screen and file output
against the sample. Local verification is recorded in [docs/VERIFICATION.md](docs/VERIFICATION.md).
Hosted workflow results become available after publishing the repository.

## AI assistance

I used ChatGPT to help draft the implementation, tests, build configuration, and
documentation. [AI_USAGE.md](AI_USAGE.md) describes the assistance and verification.

The assessment requests a GitHub repository URL. Publish all source files,
including `.github`, `.mvn`, and `.run`, and give the reviewer access before sharing
the URL.

## References

- [Java 17 Comparator](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/Comparator.html)
- [Java 17 List sort](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/List.html#sort(java.util.Comparator))
- [JUnit 5 User Guide](https://docs.junit.org/5.11.4/user-guide/index.html)
- [Maven Wrapper](https://maven.apache.org/tools/mavenwrapper.html)
