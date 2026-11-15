package ch.unil.doplab.cinerate.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static ch.unil.doplab.cinerate.domain.ApplicationState.*;
import static ch.unil.doplab.cinerate.domain.Utils.printMethodName;
import static org.junit.jupiter.api.Assertions.*;

// Klinsmann Kodra
class ApplicationStateMovieTest {

    private static final UUID aliceID = UUID.fromString("1ead1a6d-7d30-4e2d-96ad-37f620949a9b");
    private static final UUID bobID = UUID.fromString("f6a10b0a-c25f-4b6e-81b4-227a28fdda4e");
    private static final UUID adminID = UUID.fromString("834bd15a-f9d6-4650-a5bf-e9bc69c89ccf");

    private ApplicationState state;

    @BeforeEach
    void setUp() {
        state = new ApplicationState();
        state.init();
    }

    private static Set<String> titles(List<Movie> movies) {
        var titles = new TreeSet<String>();
        for (Movie movie : movies) {
            titles.add(movie.getTitle());
        }
        return titles;
    }

    private static MovieData data(String title, Integer year, UUID director, List<UUID> cast,
                                  List<String> genres, List<String> keywords) {
        var data = new MovieData(title, year);
        data.setDirectorID(director == null ? null : director.toString());
        var ids = new ArrayList<String>();
        for (UUID id : cast) {
            ids.add(id.toString());
        }
        data.setCast(ids);
        data.setGenres(genres);
        data.setKeywords(keywords);
        return data;
    }

    private static MovieData inceptionData() {
        var data = data("Inception", 2010, nolanID, List.of(dicaprioID, gordonLevittID),
                List.of("Science Fiction", "Action"), List.of("dream", "heist"));
        data.setDuration(148);
        return data;
    }

    private Set<UUID> filmography(Person person) {
        return new HashSet<>(person.getMovies());
    }

    @Test
    void testMovieLifecycle() {
        printMethodName();
        var data = data("Memento", 2000, nolanID, List.of(), List.of("Thriller"), List.of("memory"));
        data.setDuration(113);
        var created = state.addMovie(data);

        var id = created.getUUID();
        assertNotNull(id);
        assertEquals(4, state.getAllMovies().size());
        assertTrue(state.getAllMovies().containsKey(id));
        assertSame(created, state.getMovie(id));
        assertEquals("Christopher Nolan", created.getDirectorName());
        assertTrue(state.getDirector(nolanID).hasDirected(id));

        var update = data("Memento (restored)", 2000, null, List.of(), List.of("Mystery"), List.of("memory", "tattoo"));
        var updated = state.setMovie(id, update);
        assertSame(created, updated);
        assertEquals(id, updated.getUUID());
        assertEquals("Memento (restored)", updated.getTitle());
        assertEquals(0, updated.getDuration());
        assertNull(updated.getDirectorID());
        assertNull(updated.getDirectorName());
        assertFalse(state.getDirector(nolanID).hasDirected(id));
        assertEquals(List.of("memory", "tattoo"), updated.getKeywords());

        assertTrue(state.removeMovie(id));
        assertNull(state.getMovie(id));
        assertEquals(3, state.getAllMovies().size());
        assertFalse(state.removeMovie(id));
        assertNull(state.setMovie(id, update));
    }

    @Test
    void testGetMovieReturnsNullWhenAbsent() {
        printMethodName();
        assertNull(state.getMovie(UUID.randomUUID()));
        assertNull(state.getMovie(null));
        assertEquals("Inception", state.getMovie(inceptionID).getTitle());
    }

    @Test
    void testFieldsAreTrimmedAndKeywordsNormalized() {
        printMethodName();
        var data = data("  Memento  ", 2000, null, List.of(), List.of(" thriller "), List.of("  Memory  "));
        var movie = state.addMovie(data);
        assertEquals("Memento", movie.getTitle());
        assertTrue(movie.hasGenre(Genre.Thriller));
        assertEquals(List.of("memory"), movie.getKeywords());
        assertEquals(List.of(movie), state.searchMovies(null, null, null, null, " MEMORY "));
    }

    @Test
    void testAddMovieFailure_BlankTitle() {
        printMethodName();
        var e = assertThrows(IllegalArgumentException.class, () -> state.addMovie(new MovieData("   ", 2000)));
        assertTrue(e.getMessage().contains("title"));
        assertThrows(IllegalArgumentException.class, () -> state.addMovie(new MovieData(null, 2000)));
        assertThrows(IllegalArgumentException.class, () -> state.addMovie(null));
        assertEquals(3, state.getAllMovies().size());
    }

    @Test
    void testAddMovieFailure_YearAndDuration() {
        printMethodName();
        assertThrows(IllegalArgumentException.class, () -> state.addMovie(new MovieData("Memento", null)));
        assertThrows(IllegalArgumentException.class, () -> state.addMovie(new MovieData("Memento", 0)));
        var data = new MovieData("Memento", 2000);
        data.setDuration(-5);
        var e = assertThrows(IllegalArgumentException.class, () -> state.addMovie(data));
        assertTrue(e.getMessage().contains("duration"));
        assertEquals(3, state.getAllMovies().size());
    }

    @Test
    void testParseUUIDFailure_Malformed() {
        printMethodName();
        var e = assertThrows(IllegalArgumentException.class, () -> ApplicationState.parseUUID("not-a-uuid", "movie"));
        assertTrue(e.getMessage().contains("Invalid movie id"));
        assertThrows(IllegalArgumentException.class, () -> ApplicationState.parseUUID("1-1-1-1-1", "movie"));
        assertThrows(IllegalArgumentException.class, () -> ApplicationState.parseUUID(null, "movie"));
        assertEquals(inceptionID, ApplicationState.parseUUID(" " + inceptionID + " ", "movie"));
    }

    @Test
    void testAddMovieFailure_InvalidReferences() {
        printMethodName();
        var unknownActor = data("Memento", 2000, null, List.of(UUID.randomUUID()), List.of(), List.of());
        var e = assertThrows(IllegalArgumentException.class, () -> state.addMovie(unknownActor));
        assertTrue(e.getMessage().contains("Unknown actor"));

        var malformedActor = new MovieData("Memento", 2000);
        malformedActor.setCast(List.of("dicaprio"));
        assertThrows(IllegalArgumentException.class, () -> state.addMovie(malformedActor));

        var unknownDirector = data("Memento", 2000, UUID.randomUUID(), List.of(), List.of(), List.of());
        e = assertThrows(IllegalArgumentException.class, () -> state.addMovie(unknownDirector));
        assertTrue(e.getMessage().contains("Unknown director"));

        var duplicateActor = data("Memento", 2000, null, List.of(dicaprioID, dicaprioID), List.of(), List.of());
        assertThrows(IllegalArgumentException.class, () -> state.addMovie(duplicateActor));

        var unknownGenre = data("Memento", 2000, null, List.of(), List.of("Cartoon"), List.of());
        e = assertThrows(IllegalArgumentException.class, () -> state.addMovie(unknownGenre));
        assertTrue(e.getMessage().contains("Accepted values"));

        var emptyKeyword = data("Memento", 2000, null, List.of(), List.of(), Arrays.asList("ok", null));
        assertThrows(IllegalArgumentException.class, () -> state.addMovie(emptyKeyword));

        assertEquals(3, state.getAllMovies().size());
        assertEquals(Set.of(inceptionID, shutterIslandID), filmography(state.getActor(dicaprioID)));
    }

    @Test
    void testInvalidPutLeavesMovieAndRelationsUnchanged() {
        printMethodName();
        var inception = state.getMovie(inceptionID);
        var invalid = data("Changed", 2011, scorseseID, List.of(ruffaloID, UUID.randomUUID()),
                List.of("Drama"), List.of("changed"));
        assertThrows(IllegalArgumentException.class, () -> state.setMovie(inceptionID, invalid));

        assertEquals("Inception", inception.getTitle());
        assertEquals(2010, inception.getReleaseYear());
        assertTrue(inception.isDirectedBy(nolanID));
        assertEquals(List.of(dicaprioID, gordonLevittID), inception.getCast());
        assertEquals(List.of("dream", "heist"), inception.getKeywords());
        assertTrue(inception.hasGenre(Genre.ScienceFiction));
        assertTrue(state.getDirector(nolanID).hasDirected(inceptionID));
        assertFalse(state.getDirector(scorseseID).hasDirected(inceptionID));
        assertFalse(state.getActor(ruffaloID).playsIn(inceptionID));
        assertTrue(state.getActor(gordonLevittID).playsIn(inceptionID));
    }

    @Test
    void testSearchWithoutActiveCriteriaReturnsCatalogue() {
        printMethodName();
        assertEquals(3, state.searchMovies(null, null, null, null, null).size());
        assertEquals(3, state.searchMovies("", "  ", "", " ", "").size());
    }

    @Test
    void testSearchByTitle() {
        printMethodName();
        assertEquals(Set.of("Inception"), titles(state.searchMovies("incep", null, null, null, null)));
        assertEquals(Set.of("Shutter Island"), titles(state.searchMovies("  ISLAND ", null, null, null, null)));
    }

    @Test
    void testSearchByActor() {
        printMethodName();
        var expected = Set.of("Inception", "Shutter Island");
        assertEquals(expected, titles(state.searchMovies(null, "leonardo dicaprio", null, null, null)));
        assertEquals(expected, titles(state.searchMovies(null, "  LEONARDO   DiCaprio ", null, null, null)));
        assertEquals(List.of(), state.searchMovies(null, "DiCaprio", null, null, null));
        assertEquals(List.of(), state.searchMovies(null, "Unknown Person", null, null, null));
    }

    @Test
    void testSearchByActor_Homonyms() {
        printMethodName();
        var otherLeo = new Actor(UUID.randomUUID(), "Leonardo", "DiCaprio", null, "Italian");
        state.addActor(otherLeo);
        var movie = state.addMovie(data("Another Film", 2020, null, List.of(otherLeo.getUUID()), List.of(), List.of()));
        assertEquals(Set.of("Inception", "Shutter Island", "Another Film"),
                titles(state.searchMovies(null, "Leonardo DiCaprio", null, null, null)));
        assertTrue(otherLeo.playsIn(movie.getUUID()));
    }

    @Test
    void testSearchByDirector() {
        printMethodName();
        assertEquals(Set.of("A Nightmare on Elm Street"), titles(state.searchMovies(null, null, "WES CRAVEN", null, null)));
        assertEquals(List.of(), state.searchMovies(null, null, "Steven Spielberg", null, null));
    }

    @Test
    void testSearchByGenre() {
        printMethodName();
        assertEquals(Set.of("Inception"), titles(state.searchMovies(null, null, null, "science fiction", null)));
        assertEquals(Set.of("Inception"), titles(state.searchMovies(null, null, null, "ScienceFiction", null)));
        assertEquals(Set.of("Shutter Island"), titles(state.searchMovies(null, null, null, " Thriller ", null)));
        assertEquals(List.of(), state.searchMovies(null, null, null, "Western", null));
    }

    @Test
    void testSearchFailure_UnknownGenre() {
        printMethodName();
        var e = assertThrows(IllegalArgumentException.class, () -> state.searchMovies(null, null, null, "Cartoon", null));
        assertTrue(e.getMessage().contains("Science Fiction"));
    }

    @Test
    void testSearchByKeyword() {
        printMethodName();
        var expected = Set.of("Inception", "A Nightmare on Elm Street");
        assertEquals(expected, titles(state.searchMovies(null, null, null, null, "DREAM")));
        assertEquals(expected, titles(state.searchMovies(null, null, null, null, "  dream ")));
        assertEquals(Set.of("Shutter Island"), titles(state.searchMovies(null, null, null, null, "Investigation")));
    }

    @Test
    void testSearchActorAndTheme() {
        printMethodName();
        assertEquals(2, state.searchMovies(null, "leonardo dicaprio", null, null, null).size());
        assertEquals(2, state.searchMovies(null, null, null, null, "DREAM").size());
        var result = state.searchMovies(null, "leonardo dicaprio", null, null, "DREAM");
        assertEquals(1, result.size());
        assertEquals(inceptionID, result.get(0).getUUID());
    }

    @Test
    void testSearchCombinationWithoutResult() {
        printMethodName();
        assertEquals(List.of(), state.searchMovies(null, "Leonardo DiCaprio", null, "Horror", null));
        assertEquals(List.of(), state.searchMovies("Inception", null, "Martin Scorsese", null, null));
        assertEquals(Set.of("Inception"),
                titles(state.searchMovies("in", "Leonardo DiCaprio", "Christopher Nolan", "Action", "heist")));
    }

    @Test
    void testChangeDirector() {
        printMethodName();
        var data = inceptionData();
        data.setDirectorID(scorseseID.toString());
        var inception = state.setMovie(inceptionID, data);
        assertEquals("Martin Scorsese", inception.getDirectorName());
        assertFalse(state.getDirector(nolanID).hasDirected(inceptionID));
        assertTrue(state.getDirector(scorseseID).hasDirected(inceptionID));

        data.setDirectorID(null);
        state.setMovie(inceptionID, data);
        assertNull(inception.getDirectorID());
        assertNull(inception.getDirectorName());
        assertEquals(Set.of(shutterIslandID), filmography(state.getDirector(scorseseID)));
        assertEquals(Set.of(), filmography(state.getDirector(nolanID)));
    }

    @Test
    void testChangeCast() {
        printMethodName();
        var data = inceptionData();
        data.setCast(List.of(dicaprioID.toString(), englundID.toString()));
        var inception = state.setMovie(inceptionID, data);
        assertEquals(Set.of(dicaprioID, englundID), new HashSet<>(inception.getCast()));
        assertTrue(state.getActor(dicaprioID).playsIn(inceptionID));
        assertTrue(state.getActor(englundID).playsIn(inceptionID));
        assertFalse(state.getActor(gordonLevittID).playsIn(inceptionID));
        assertEquals(Set.of(nightmareID, inceptionID), filmography(state.getActor(englundID)));

        data.setCast(List.of());
        state.setMovie(inceptionID, data);
        assertEquals(List.of(), inception.getCast());
        assertEquals(Set.of(shutterIslandID), filmography(state.getActor(dicaprioID)));
        assertEquals(Set.of(nightmareID), filmography(state.getActor(englundID)));
    }

    @Test
    void testPutKeepsUUIDReviewsAndRatings() {
        printMethodName();
        var alice = state.getUser(aliceID);
        var inception = state.getMovie(inceptionID);
        new Review(Rating.EIGHT, "Great").post(alice, inception);

        var data = inceptionData();
        data.setTitle("Inception (2010)");
        data.setGenres(null);
        data.setKeywords(null);
        var updated = state.setMovie(inceptionID, data);

        assertSame(inception, updated);
        assertEquals(inceptionID, updated.getUUID());
        assertEquals("Inception (2010)", updated.getTitle());
        assertEquals(List.of(), updated.getGenres());
        assertEquals(List.of(), updated.getKeywords());
        assertEquals(1, updated.getRatingCount());
        assertEquals(8.0, updated.getRatingAverage());
        assertNotNull(updated.getReview(aliceID));
    }

    @Test
    void testRemoveMovieCleansReviewsWatchlistsAndPeople() {
        printMethodName();
        var alice = state.getUser(aliceID);
        var bob = state.getUser(bobID);
        var admin = state.getUser(adminID);
        var inception = state.getMovie(inceptionID);
        var shutter = state.getMovie(shutterIslandID);

        new Review(Rating.NINE, "Brilliant").post(alice, inception);
        new Review(Rating.SEVEN, "Tense").post(alice, shutter);
        bob.addToWatchlist(inceptionID);
        bob.addToWatchlist(shutterIslandID);
        admin.addToWatchlist(inceptionID);
        admin.markAsWatched(inceptionID);
        assertTrue(admin.hasWatched(inceptionID));

        assertTrue(state.removeMovie(inceptionID));

        assertNull(state.getMovie(inceptionID));
        assertFalse(alice.hasReviewed(inceptionID));
        assertEquals(1, alice.getReviews().size());
        assertTrue(alice.hasReviewed(shutterIslandID));
        assertEquals(1, shutter.getRatingCount());
        assertFalse(bob.isInWatchlist(inceptionID));
        assertTrue(bob.isInWatchlist(shutterIslandID));
        assertFalse(admin.hasWatched(inceptionID));
        assertFalse(admin.isInWatchlist(inceptionID));
        assertEquals(Set.of(shutterIslandID), filmography(state.getActor(dicaprioID)));
        assertEquals(Set.of(), filmography(state.getActor(gordonLevittID)));
        assertEquals(Set.of(), filmography(state.getDirector(nolanID)));
        assertEquals(Set.of("Shutter Island"), titles(state.searchMovies(null, "Leonardo DiCaprio", null, null, null)));
    }

    @Test
    void testRemoveMovieWhoseReviewerWasDeleted() {
        printMethodName();
        new Review(Rating.SIX, "Fine").post(state.getUser(aliceID), state.getMovie(inceptionID));
        assertTrue(state.removeUser(aliceID));
        assertTrue(state.removeMovie(inceptionID));
        assertNull(state.getMovie(inceptionID));
    }

    @Test
    void testResetRestoresDemoCatalogue() {
        printMethodName();
        state.removeMovie(inceptionID);
        state.addMovie(new MovieData("Memento", 2000));
        var data = data("Changed", 2010, null, List.of(), List.of(), List.of());
        state.setMovie(shutterIslandID, data);

        state.init();

        assertEquals(Set.of(inceptionID, shutterIslandID, nightmareID), state.getAllMovies().keySet());
        assertEquals("Shutter Island", state.getMovie(shutterIslandID).getTitle());
        assertEquals(Set.of(inceptionID, shutterIslandID), filmography(state.getActor(dicaprioID)));
        assertEquals(Set.of(shutterIslandID), filmography(state.getDirector(scorseseID)));
        assertEquals(3, state.getAllUsers().size());
        assertNotNull(state.login("alice", "alice123"));
        assertEquals(List.of(state.getMovie(inceptionID)),
                state.searchMovies(null, "leonardo dicaprio", null, null, "DREAM"));
    }
}
