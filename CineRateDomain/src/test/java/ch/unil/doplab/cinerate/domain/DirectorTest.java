package ch.unil.doplab.cinerate.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static ch.unil.doplab.cinerate.domain.Utils.printMethodName;

// Elias Mounerat
class DirectorTest {

    private Director nolan;
    private UUID inception;

    @BeforeEach
    void setUp() {
        nolan = new Director(UUID.randomUUID(), "Christopher", "Nolan", null, "British");
        inception = UUID.randomUUID();
    }

    @Test
    void testAddMovie() {
        printMethodName();
        nolan.addMovie(inception);
        assertTrue(nolan.hasDirected(inception));
        assertEquals(1, nolan.getMovies().size());
    }

    @Test
    void testRemoveMovie() {
        printMethodName();
        nolan.addMovie(inception);
        nolan.removeMovie(inception);
        assertFalse(nolan.hasDirected(inception));
    }

    @Test
    void testReplaceWith() {
        printMethodName();
        nolan.addMovie(inception);
        var other = new Director("Denis", "Villeneuve");
        nolan.replaceWith(other);
        assertEquals("Denis", nolan.getFirstName());
        assertTrue(nolan.getMovies().isEmpty());
    }

    @Test
    void testToString() {
        printMethodName();
        assertTrue(nolan.toString().startsWith("Director{"));
    }
}
