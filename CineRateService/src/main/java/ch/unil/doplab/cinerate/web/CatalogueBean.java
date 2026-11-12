package ch.unil.doplab.cinerate.web;

import ch.unil.doplab.cinerate.domain.ApplicationState;
import ch.unil.doplab.cinerate.domain.Genre;
import ch.unil.doplab.cinerate.domain.Movie;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.*;

/**
 * Controller of the catalogue page (catalogue.xhtml).
 *
 * It reuses the search of the service (ApplicationState.searchMovies): the filled criteria are
 * combined with a logical AND and the empty ones are ignored. The release year, which the REST
 * search does not have, is filtered here.
 */
@Named
@ViewScoped
public class CatalogueBean implements Serializable {

    @Inject
    private ApplicationState state;

    private String title;
    private String actor;
    private String director;
    private String genre;
    private Integer year;
    private String keyword;

    private List<MovieRow> movies = new ArrayList<>();
    private MovieRow selectedMovie;
    private String warning;

    @PostConstruct
    public void init() {
        search();
    }

    /** Runs the search with the current criteria. */
    public void search() {
        warning = null;
        try {
            var rows = new ArrayList<MovieRow>();
            for (Movie movie : state.searchMovies(title, actor, director, genre, keyword)) {
                if (year == null || movie.getReleaseYear() == year) {
                    rows.add(toRow(movie));
                }
            }
            movies = rows;
        } catch (IllegalArgumentException e) {
            // for example an unknown genre: the page shows the message of the service
            movies = new ArrayList<>();
            warning = e.getMessage();
        }
    }

    /** Clears every criterion and shows the whole catalogue again. */
    public void reset() {
        title = null;
        actor = null;
        director = null;
        genre = null;
        year = null;
        keyword = null;
        search();
    }

    public boolean isFiltered() {
        return !isBlank(title) || !isBlank(actor) || !isBlank(director) || !isBlank(genre)
                || year != null || !isBlank(keyword);
    }

    /** Labels of the genres for the drop-down menu ("Science Fiction", ...). */
    public List<String> getGenres() {
        var labels = new ArrayList<String>();
        for (Genre g : Genre.values()) {
            labels.add(g.getLabel());
        }
        return labels;
    }

    /** Release years present in the catalogue, the most recent first. */
    public List<Integer> getYears() {
        var years = new TreeSet<Integer>(Comparator.reverseOrder());
        for (Movie movie : state.getAllMovies().values()) {
            if (movie.getReleaseYear() > 0) {
                years.add(movie.getReleaseYear());
            }
        }
        return new ArrayList<>(years);
    }

    public int getCatalogueSize() {
        return state.getAllMovies().size();
    }

    MovieRow toRow(Movie movie) {
        var genreLabels = new ArrayList<String>();
        for (Genre g : movie.getGenres()) {
            genreLabels.add(g.getLabel());
        }
        Collections.sort(genreLabels);
        var castNames = new ArrayList<String>();
        for (UUID actorId : movie.getCast()) {
            var person = state.getActor(actorId);
            if (person != null) {
                castNames.add(person.getFullName());
            }
        }
        return new MovieRow(movie.getUUID(), movie.getTitle(), movie.getReleaseYear(), movie.getDuration(),
                String.join(", ", genreLabels),
                movie.getDirectorName() == null ? "" : movie.getDirectorName(),
                String.join(", ", castNames),
                String.join(", ", movie.getKeywords()),
                movie.getSynopsis() == null ? "" : movie.getSynopsis(),
                movie.getRatingAverage(), movie.getRatingCount(), movie.getFormattedRating());
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    public String getWarning() {
        return warning;
    }

    // used by the unit tests, which run without CDI
    void setState(ApplicationState state) {
        this.state = state;
    }

    public List<MovieRow> getMovies() {
        return movies;
    }

    public MovieRow getSelectedMovie() {
        return selectedMovie;
    }

    public void setSelectedMovie(MovieRow selectedMovie) {
        this.selectedMovie = selectedMovie;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getActor() {
        return actor;
    }

    public void setActor(String actor) {
        this.actor = actor;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
}
