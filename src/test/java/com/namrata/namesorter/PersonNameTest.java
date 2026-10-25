package com.namrata.namesorter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PersonNameTest {
    @Test
    void formatsAllGivenNamesBeforeTheSurname() {
        PersonName name = new PersonName(List.of("Hunter", "Uriah", "Mathew"), "Clarke");
        assertEquals("Hunter Uriah Mathew", name.givenNamesText());
        assertEquals("Hunter Uriah Mathew Clarke", name.fullName());
    }

    @Test
    void makesADefensiveCopyOfGivenNames() {
        List<String> givenNames = new ArrayList<>(List.of("Ana"));
        PersonName name = new PersonName(givenNames, "Smith");
        givenNames.add("Maria");
        assertEquals(List.of("Ana"), name.givenNames());
        assertThrows(UnsupportedOperationException.class, () -> name.givenNames().add("Jane"));
    }

    @Test
    void rejectsMissingGivenNames() {
        assertThrows(IllegalArgumentException.class, () -> new PersonName(List.of(), "Smith"));
    }

    @Test
    void rejectsMoreThanThreeGivenNames() {
        assertThrows(IllegalArgumentException.class,
                () -> new PersonName(List.of("A", "B", "C", "D"), "Smith"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "Van Dyke", "A\tB", "A\u00A0B"})
    void rejectsInvalidSurnameTokens(String surname) {
        assertThrows(IllegalArgumentException.class,
                () -> new PersonName(List.of("Ana"), surname));
    }

    @Test
    void rejectsWhitespaceInsideAGivenNameToken() {
        assertThrows(IllegalArgumentException.class,
                () -> new PersonName(List.of("Ana Maria"), "Smith"));
    }
}
