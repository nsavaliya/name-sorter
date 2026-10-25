package com.namrata.namesorter;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/** The file operations needed by the application, without filesystem details. */
public interface NameStore {
    List<PersonName> read(Path input) throws IOException;

    void write(Path output, List<PersonName> names) throws IOException;
}
