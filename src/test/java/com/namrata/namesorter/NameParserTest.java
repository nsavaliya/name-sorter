package com.namrata.namesorter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NameParserTest {
    private final NameParser parser = new NameParser();

    @Test
    void parsesOneGivenName() {
        assertEquals(new PersonName(List.of("Marin"), "Alvarez"), parser.parse("Marin Alvarez").orElseThrow());
    }

    @Test
    void parsesTwoGivenNames() {
        assertEquals(List.of("Adonis", "Julius"),
                parser.parse("Adonis Julius Archer").orElseThrow().givenNames());
    }

    @Test
    void parsesThreeGivenNames() {
        assertEquals(List.of("Hunter", "Uriah", "Mathew"),
                parser.parse("Hunter Uriah Mathew Clarke").orElseThrow().givenNames());
    }

    @Test
    void normalizesSpacesTabsAndUnicodeWhitespace() {
        assertEquals("Ana Maria Smith",
                parser.parse(" \tAna\u00A0 Maria\u2003Smith \t").orElseThrow().fullName());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "\t", "\u00A0\u2003"})
    void ignoresBlankLines(String line) {
        assertTrue(parser.parse(line).isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {"Madonna", "A B C D Smith"})
    void rejectsNamesOutsideTheRequiredTokenCount(String line) {
        assertThrows(IllegalArgumentException.class, () -> parser.parse(line));
    }

    @Test
    void preservesSpellingCaseAccentsAndPunctuation() {
        assertEquals("Élodie Anne-Marie O'Neill",
                parser.parse("Élodie Anne-Marie O'Neill").orElseThrow().fullName());
    }

    @Test
    void preservesTheInputSpellingInTheAssessmentExample() {
        assertEquals("Vaugh Lewis", parser.parse("Vaugh Lewis").orElseThrow().fullName());
    }
}
