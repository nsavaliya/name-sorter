package com.namrata.namesorter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/** Pure sorting logic, independent of files and console output. */
public final class NameSorter {
    private static final Comparator<PersonName> BY_LAST_THEN_GIVEN_NAMES =
            Comparator.comparing(PersonName::lastName, String.CASE_INSENSITIVE_ORDER)
                    .thenComparing(PersonName::givenNamesText, String.CASE_INSENSITIVE_ORDER);

    public List<PersonName> sort(List<PersonName> names) {
        List<PersonName> sorted = new ArrayList<>(names);
        // List.sort is stable: equal name keys keep their input order, including case variants.
        sorted.sort(BY_LAST_THEN_GIVEN_NAMES);
        return List.copyOf(sorted);
    }
}
