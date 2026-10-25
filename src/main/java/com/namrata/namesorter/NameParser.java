package com.namrata.namesorter;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;

/** Normalizes whitespace without changing the spelling or case of a name. */
public final class NameParser {
    private static final Pattern WHITESPACE =
            Pattern.compile("\\s+", Pattern.UNICODE_CHARACTER_CLASS);

    public Optional<PersonName> parse(String line) {
        String normalized = WHITESPACE.matcher(Objects.requireNonNull(line, "line"))
                .replaceAll(" ").strip();
        if (normalized.isEmpty()) {
            return Optional.empty();
        }

        String[] tokens = normalized.split(" ");
        return Optional.of(new PersonName(
                Arrays.asList(tokens).subList(0, tokens.length - 1),
                tokens[tokens.length - 1]));
    }
}
