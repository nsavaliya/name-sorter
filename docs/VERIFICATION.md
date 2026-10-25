# Verification record

Verified on 24 October 2026 using Java 17 and Maven 3.9.9 on Linux.

## Build and tests

The latest build completed successfully with **51 test invocations, 0 failures, 0 errors, and 0 skipped tests**. Parameterized inputs count separately.

Actual build command in the cached verification environment:

```sh
./mvnw --offline --batch-mode --no-transfer-progress clean verify
```

For a first build on a new machine, omit `--offline` so Maven can download dependencies. Windows users can run `.\mvnw.cmd clean verify`.

| Test class | Passed |
| --- | ---: |
| `NameParserTest` | 12 |
| `NameSorterApplicationTest` | 15 |
| `NameSorterTest` | 7 |
| `PersonNameTest` | 10 |
| `TextFileNameStoreTest` | 7 |

```text
Tests run: 51, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

## Packaged command checks

- Packaged launcher matches the supplied sample on stdout and disk.
- Output uses the caller working directory.
- Existing output is overwritten without stale rows.
- Packaged JAR compares all given names for matching surnames.
- Invalid data returns exit 1, reports line 2, and preserves prior output.
- Missing input returns exit 1 and preserves prior output.
- Missing arguments return process exit 2.
- Extra arguments return process exit 2.
- Help returns process exit 0.
- Blank input overwrites output with an empty file.
- Exact PATH command handles UTF-8 BOM, Unicode whitespace, CRLF, and a path containing spaces.
- Input and output can be the same file.
- A separate packaged process sorts 1,000 names and retains duplicate rows.
- Malformed UTF-8 returns exit 1 and preserves prior output.
- An unwritable output destination returns exit 1 without console results.

The IntelliJ configuration XML was checked against the main class, sample input, and project working directory. The equivalent main-class command was executed successfully through Java. The recorded checks were performed through the terminal.

## Reproduce the sample check

```sh
./mvnw clean verify
./name-sorter examples/unsorted-names-list.txt > actual-names.txt
diff -u examples/expected-sorted-names-list.txt actual-names.txt
diff -u examples/expected-sorted-names-list.txt sorted-names-list.txt
```

## Hosted builds

`.github/workflows/build.yml` is configured for Linux and Windows. Hosted GitHub Actions results must be checked after repository publication.

## IDE references

- [IntelliJ Maven projects](https://www.jetbrains.com/help/idea/maven-support.html)
- [IntelliJ Application run configurations](https://www.jetbrains.com/help/idea/run-debug-configuration-java-application.html)
