package com.namrata.namesorter;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NameSorterTest {
    private final NameParser parser = new NameParser();
    private final NameSorter sorter = new NameSorter();

    @Test
    void sortsBySurnameBeforeGivenNames() {
        assertEquals(List.of("Zoe Archer", "Ana Brown", "Aaron Smith"),
                sort("Aaron Smith", "Ana Brown", "Zoe Archer"));
    }

    @Test
    void comparesAllGivenNamesWhenSurnamesAreEqual() {
        assertEquals(List.of("Ana Smith", "Ana Maria Smith", "Ana Maria Jane Smith",
                        "Ana Maria Zoe Smith", "Ana Marie Smith", "Zoe Smith"),
                sort("Zoe Smith", "Ana Marie Smith", "Ana Maria Zoe Smith", "Ana Smith",
                        "Ana Maria Jane Smith", "Ana Maria Smith"));
    }

    @Test
    void sortsCaseInsensitivelyButPreservesOriginalCase() {
        assertEquals(List.of("zoe archer", "ana Brown", "Aaron smith"),
                sort("Aaron smith", "ana Brown", "zoe archer"));
    }

    @Test
    void preservesInputOrderForEqualKeys() {
        assertEquals(List.of("ana SMITH", "Ana Smith", "ANA smith"),
                sort("ana SMITH", "Ana Smith", "ANA smith"));
    }

    @Test
    void keepsDuplicates() {
        assertEquals(List.of("Ana Brown", "Ana Smith", "Ana Smith"),
                sort("Ana Smith", "Ana Brown", "Ana Smith"));
    }

    @Test
    void doesNotMutateTheInputAndReturnsAnImmutableResult() {
        List<PersonName> input = new ArrayList<>(List.of(name("Zoe Smith"), name("Ana Brown")));
        List<PersonName> original = List.copyOf(input);
        List<PersonName> result = sorter.sort(input);
        assertEquals(original, input);
        assertThrows(UnsupportedOperationException.class, () -> result.add(name("Ben Jones")));
    }

    @Test
    void handlesEmptyAndSingleNameInputs() {
        assertEquals(List.of(), sorter.sort(List.of()));
        assertEquals(List.of("Ana Smith"), sort("Ana Smith"));
    }

    private List<String> sort(String... names) {
        return sorter.sort(List.of(names).stream().map(this::name).toList())
                .stream().map(PersonName::fullName).toList();
    }

    private PersonName name(String value) {
        return parser.parse(value).orElseThrow();
    }
}
