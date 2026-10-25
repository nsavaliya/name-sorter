package com.namrata.namesorter;

import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

/** A name with one surname token and one to three given-name tokens. */
public record PersonName(List<String> givenNames, String lastName) {
    private static final Pattern WHITESPACE =
            Pattern.compile("\\s", Pattern.UNICODE_CHARACTER_CLASS);

    public PersonName {
        givenNames = List.copyOf(Objects.requireNonNull(givenNames, "givenNames"));
        if (givenNames.isEmpty() || givenNames.size() > 3) {
            throw new IllegalArgumentException(
                    "A name must have 1 to 3 given names followed by a last name.");
        }
        givenNames.forEach(PersonName::validateToken);
        validateToken(lastName);
    }

    public String givenNamesText() {
        return String.join(" ", givenNames);
    }

    public String fullName() {
        return givenNamesText() + " " + lastName;
    }

    private static void validateToken(String token) {
        Objects.requireNonNull(token, "Name tokens must not be null");
        if (token.isEmpty() || WHITESPACE.matcher(token).find()) {
            throw new IllegalArgumentException("Name tokens must be nonempty and contain no whitespace.");
        }
    }
}
