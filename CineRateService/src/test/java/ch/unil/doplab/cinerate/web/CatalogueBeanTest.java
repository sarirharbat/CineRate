package ch.unil.doplab.cinerate.web;

import ch.unil.doplab.cinerate.domain.ApplicationState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static ch.unil.doplab.cinerate.domain.Utils.printMethodName;
import static org.junit.jupiter.api.Assertions.*;

// Klisman Kodra
class CatalogueBeanTest {

    private CatalogueBean bean;

    @BeforeEach
    void setUp() {
        var state = new ApplicationState();
        state.init();
        bean = new CatalogueBean();
        bean.setState(state);
        bean.init();
    }

    private Set<String> titles() {
        var titles = new TreeSet<String>();
        for (MovieRow row : bean.getMovies()) {
            titles.add(row.getTitle());
        }
        return titles;
    }

    @Test
    void testShowsWholeCatalogueAtStart() {
        printMethodName();
        assertEquals(3, bean.getMovies().size());
        assertEquals(3, bean.getCatalogueSize());
        assertFalse(bean.isFiltered());
    }

    @Test
    void testActorAndTheme() {
        printMethodName();
        bean.setActor("leonardo dicaprio");
        bean.setKeyword("DREAM");
        bean.search();
        assertTrue(bean.isFiltered());
        assertEquals(1, bean.getMovies().size());
        var inception = bean.getMovies().get(0);
        assertEquals(ApplicationState.inceptionID, inception.getId());
        assertEquals("Inception", inception.getTitle());
        assertEquals("Action, Science Fiction", inception.getGenres());
        assertEquals("Christopher Nolan", inception.getDirector());
        assertEquals("Leonardo DiCaprio, Joseph Gordon-Levitt", inception.getCast());
        assertEquals("dream, heist", inception.getKeywords());
        assertEquals("not rated yet", inception.getFormattedRating());
    }

    @Test
    void testGenreFromDropDown() {
        printMethodName();
        bean.setGenre("Thriller");
        bean.search();
        assertEquals(Set.of("Shutter Island"), titles());
        bean.setGenre("");
        bean.search();
        assertEquals(3, bean.getMovies().size());
    }

    @Test
    void testYearFilter() {
        printMethodName();
        bean.setYear(2010);
        bean.search();
        assertEquals(Set.of("Inception", "Shutter Island"), titles());
        bean.setYear(1984);
        bean.search();
        assertEquals(Set.of("A Nightmare on Elm Street"), titles());
    }

    @Test
    void testReset() {
        printMethodName();
        bean.setActor("Robert Englund");
        bean.setYear(2010);
        bean.search();
        assertEquals(List.of(), bean.getMovies());
        bean.reset();
        assertEquals(3, bean.getMovies().size());
        assertFalse(bean.isFiltered());
        assertNull(bean.getActor());
    }

    @Test
    void testUnknownGenreGivesNoResult() {
        printMethodName();
        bean.setGenre("Cartoon");
        bean.search();
        assertEquals(List.of(), bean.getMovies());
        assertTrue(bean.getWarning().contains("Unknown genre"));
        bean.reset();
        assertNull(bean.getWarning());
    }

    @Test
    void testDropDownChoices() {
        printMethodName();
        assertEquals(List.of(2010, 1984), bean.getYears());
        assertEquals(19, bean.getGenres().size());
        assertTrue(bean.getGenres().contains("Science Fiction"));
    }
}
