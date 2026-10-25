# AI assistance

## Request and output

I supplied the assessment PDF and my Java developer resume to ChatGPT and asked
for a complete solution and an explanation I could use in a meeting. The output
included the Java 17 implementation, five JUnit test classes, Maven configuration,
Maven Wrapper scripts, command launchers, sample files, a GitHub Actions workflow,
and documentation. ChatGPT helped draft both the implementation and the tests.

I then requested a source submission and a first-person walkthrough of the file
responsibilities, design decisions, execution flow, AI assistance, and verification.
This revision adds concise submission documentation and a shared IntelliJ IDEA
run configuration while keeping the Java behavior and test suite unchanged.

## Verification

The assisted workflow built the project with Java 17 and Maven 3.9.9 on Linux.
All 51 included JUnit invocations passed. Checks of the packaged command also
covered the sample, surname ties, output replacement, invalid input, Unicode,
working-directory behavior, and a separate 1,000-name dataset. The recorded checks
and reproducible commands are in [docs/VERIFICATION.md](docs/VERIFICATION.md).

The expected 1,000-name JUnit result is constructed independently with fixed-width
keys and generation loops. It does not call the production comparator. Separate
assertions check stdout, output-file contents, row counts, and duplicate retention.

The shared IntelliJ configuration is provided for local use. The recorded
executions used Java and Maven through the terminal. Hosted GitHub Actions results
become available after repository publication.
