package ch.unil.doplab.cinerate.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static ch.unil.doplab.cinerate.domain.Utils.printMethodName;

// Elias Mounerat
class MovieTest {

    private Movie inception;
    private Director nolan;
    private Actor dicaprio;

    @BeforeEach
    void setUp() {
        inception = new Movie(UUID.randomUUID(), "Inception", 2010, "A thief who steals secrets in dreams.", 148);
        nolan = new Director(UUID.randomUUID(), "Christopher", "Nolan", null, "British");
        dicaprio = new Actor(UUID.randomUUID(), "Leonardo", "DiCaprio", null, "American");
    }

    @Test
    void testRateAverage() {
        printMethodName();
        inception.rate(8);
        inception.rate(10);
        assertEquals(9.0, inception.getRatingAverage());
        assertEquals(2, inception.getRatingCount());
    }

    @Test
    void testUnrateAverage() {
        printMethodName();
        inception.rate(8);
        inception.rate(10);
        inception.unrate(10);
        assertEquals(8.0, inception.getRatingAverage());
        inception.unrate(8);
        assertEquals(Movie.noRating, inception.getRatingAverage());
        assertEquals(0, inception.getRatingCount());
    }

    @Test
    void testRateFailure_OutOfRange() {
        printMethodName();
        Exception e = assertThrows(IllegalArgumentException.class, () -> inception.rate(11));
        assertTrue(e.getMessage().contains("between 1 and 10"));
        assertThrows(IllegalArgumentException.class, () -> inception.rate(0));
    }

    @Test
    void testFormattedRating() {
        printMethodName();
        assertEquals("not rated yet", inception.getFormattedRating());
        inception.rate(7);
        assertTrue(inception.getFormattedRating().endsWith("/ 10"));
    }

    @Test
    void testSetDirector() {
        printMethodName();
        inception.setDirector(nolan);
        assertTrue(inception.isDirectedBy(nolan.getUUID()));
        assertEquals("Christopher Nolan", inception.getDirectorName());
        assertTrue(nolan.hasDirected(inception.getUUID()));
    }

    @Test
    void testAddActorSuccess() {
        printMethodName();
        inception.addActor(dicaprio);
        assertTrue(inception.hasActor(dicaprio.getUUID()));
        assertTrue(dicaprio.playsIn(inception.getUUID()));
    }

    @Test
    void testAddActorFailure_AlreadyInCast() {
        printMethodName();
        inception.addActor(dicaprio);
        Exception e = assertThrows(IllegalStateException.class, () -> inception.addActor(dicaprio));
        assertTrue(e.getMessage().contains("already in the cast"));
    }

    @Test
    void testGenresAndKeywords() {
        printMethodName();
        inception.addGenre(Genre.ScienceFiction);
        inception.addKeyword("Dream");
        assertTrue(inception.hasGenre(Genre.ScienceFiction));
        assertTrue(inception.hasKeyword("dream"));
        assertEquals(Genre.ScienceFiction, Genre.fromLabel("science fiction"));
    }

    @Test
    void testReplaceWithKeepsId() {
        printMethodName();
        var id = inception.getUUID();
        inception.replaceWith(new Movie("Interstellar", 2014));
        assertEquals("Interstellar", inception.getTitle());
        assertEquals(id, inception.getUUID());
    }

    @Test
    void testMergeWith() {
        printMethodName();
        var patch = new Movie();
        patch.setDuration(150);
        inception.mergeWith(patch);
        assertEquals(150, inception.getDuration());
        assertEquals("Inception", inception.getTitle());
    }
}

