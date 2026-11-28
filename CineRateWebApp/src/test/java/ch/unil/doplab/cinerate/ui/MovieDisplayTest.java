package ch.unil.doplab.cinerate.ui;

import ch.unil.doplab.cinerate.domain.Genre;
import ch.unil.doplab.cinerate.domain.Movie;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

// Klisman Kodra
class MovieDisplayTest {
    private final MovieDisplay display = new MovieDisplay();

    @Test
    void movieWithoutRatingIsShownAsNew() {
        var movie = new Movie("Inception", 2010);
        assertFalse(display.isRated(movie));
        assertEquals("New", display.rating(movie));
        assertEquals("No ratings yet", display.ratingCount(movie));
        assertEquals(0, display.ratingPercent(movie));
    }

    @Test
    void ratingIsRoundedToOneDecimal() {
        var movie = new Movie("Inception", 2010);
        movie.rate(9);
        movie.rate(8);
        assertEquals("8.5", display.rating(movie));
        assertEquals("2 ratings", display.ratingCount(movie));
        assertEquals(85, display.ratingPercent(movie));
    }

    @Test
    void durationIsReadable() {
        assertEquals("2h 28m", display.duration(148));
        assertEquals("2h", display.duration(120));
        assertEquals("45 min", display.duration(45));
        assertEquals("", display.duration(0));
    }

    @Test
    void genresAreSortedByLabel() {
        var movie = new Movie("Inception", 2010);
        movie.setGenres(List.of(Genre.ScienceFiction, Genre.Action));
        assertEquals(List.of("Action", "Science Fiction"), display.genreLabels(movie));
        assertEquals("Action · Science Fiction", display.genres(movie));
    }

    @Test
    void sameTitleAlwaysGetsSameTone() {
        var first = new Movie("Shutter Island", 2010);
        var second = new Movie("Shutter Island", 2010);
        assertEquals(display.tone(first), display.tone(second));
        assertTrue(display.tone(first).matches("tone-[0-5]"));
    }

    @Test
    void initialsAndDate() {
        assertEquals("LD", display.initials("Leonardo DiCaprio"));
        assertEquals("A", display.initials("alice"));
        assertEquals("?", display.initials(" "));
        assertEquals("12 Nov 2026", display.date(LocalDateTime.of(2026, 11, 12, 10, 0)));
        assertEquals("", display.date(null));
    }
}
