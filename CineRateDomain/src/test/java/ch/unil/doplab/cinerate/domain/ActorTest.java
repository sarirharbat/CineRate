package ch.unil.doplab.cinerate.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static ch.unil.doplab.cinerate.domain.Utils.printMethodName;

// Elias Mounerat
class ActorTest {

    private Actor dicaprio;
    private UUID inception;

    @BeforeEach
    void setUp() {
        dicaprio = new Actor(UUID.randomUUID(), "Leonardo", "DiCaprio", null, "American");
        inception = UUID.randomUUID();
    }

    @Test
    void testAddMovie() {
        printMethodName();
        dicaprio.addMovie(inception);
        assertTrue(dicaprio.playsIn(inception));
        assertEquals(1, dicaprio.getMovies().size());
    }

    @Test
    void testRemoveMovie() {
        printMethodName();
        dicaprio.addMovie(inception);
        dicaprio.removeMovie(inception);
        assertFalse(dicaprio.playsIn(inception));
        assertTrue(dicaprio.getMovies().isEmpty());
    }

    @Test
    void testAddSameMovieTwice() {
        printMethodName();
        dicaprio.addMovie(inception);
        dicaprio.addMovie(inception);
        assertEquals(1, dicaprio.getMovies().size());
    }

    @Test
    void testMergeWith() {
        printMethodName();
        var patch = new Actor();
        patch.addMovie(inception);
        dicaprio.mergeWith(patch);
        assertTrue(dicaprio.playsIn(inception));
        assertEquals("Leonardo", dicaprio.getFirstName());
    }

    @Test
    void testToString() {
        printMethodName();
        assertTrue(dicaprio.toString().startsWith("Actor{"));
    }
}
