package ch.unil.doplab.cinerate.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static ch.unil.doplab.cinerate.domain.Utils.printMethodName;

class GenreTest {

    @Test
    void testGetLabel() {
        printMethodName();
        assertEquals("Science Fiction", Genre.ScienceFiction.getLabel());
        assertEquals("Drama", Genre.Drama.getLabel());
    }

    @Test
    void testFromLabel() {
        printMethodName();
        assertEquals(Genre.ScienceFiction, Genre.fromLabel("Science Fiction"));
        assertEquals(Genre.Comedy, Genre.fromLabel("comedy"));
    }

    @Test
    void testFromLabelFailure_Unknown() {
        printMethodName();
        Exception e = assertThrows(IllegalArgumentException.class, () -> Genre.fromLabel("Cartoon"));
        assertTrue(e.getMessage().contains("Unknown genre"));
    }
}
