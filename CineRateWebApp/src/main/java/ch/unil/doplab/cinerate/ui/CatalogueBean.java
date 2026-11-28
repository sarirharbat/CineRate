package ch.unil.doplab.cinerate.ui;

import ch.unil.doplab.cinerate.CineRateService;
import ch.unil.doplab.cinerate.domain.Actor;
import ch.unil.doplab.cinerate.domain.Director;
import ch.unil.doplab.cinerate.domain.Genre;
import ch.unil.doplab.cinerate.domain.Movie;
import jakarta.annotation.PostConstruct;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.WebApplicationException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.UUID;
import java.util.logging.Logger;

// Klisman Kodra
// Page Catalogue : liste des films, recherche libre, filtres et tri
@Named
@ViewScoped
public class CatalogueBean implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final Logger log = Logger.getLogger(CatalogueBean.class.getName());

    public static final String SORT_RATING = "rating";
    public static final String SORT_YEAR = "year";
    public static final String SORT_TITLE = "title";

    @Inject
    CineRateService theService;

    private List<Movie> allMovies = new ArrayList<>();
    private List<Movie> movies = new ArrayList<>();
    private List<Actor> actors = new ArrayList<>();
    private List<Director> directors = new ArrayList<>();
    private Map<UUID, String> actorNames = new HashMap<>();
    private boolean serviceAvailable = true;

    private String query;
    private String genre;
    private String director;
    private String actor;
    private String sort = SORT_RATING;

    @PostConstruct
    public void init() {
        // Le texte tapé dans la barre de recherche de la page d'accueil arrive en paramètre "q"
        query = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("q");
        try {
            allMovies = theService.getAllMovies();
            actors = theService.getAllActors();
            directors = theService.getAllDirectors();
            for (Actor a : actors) {
                actorNames.put(a.getUUID(), a.getFullName());
            }
            actors.sort(Comparator.comparing(Actor::getLastName));
            directors.sort(Comparator.comparing(Director::getLastName));
        } catch (WebApplicationException | ProcessingException e) {
            log.warning("Catalogue could not be loaded: " + e.getMessage());
            serviceAvailable = false;
        }
        search();
    }

    public void search() {
        if (!serviceAvailable) {
            movies = new ArrayList<>();
            return;
        }
        try {
            // Les filtres actor / director / genre passent par la recherche du service
            var found = theService.searchMovies(null, actor, director, genre, null);
            var result = new ArrayList<Movie>();
            for (Movie movie : found) {
                if (matchesQuery(movie)) {
                    result.add(movie);
                }
            }
            result.sort(comparator());
            movies = result;
        } catch (WebApplicationException | ProcessingException e) {
            log.warning("Search failed: " + e.getMessage());
            serviceAvailable = false;
            movies = new ArrayList<>();
        }
    }

    // Recherche libre : "dicaprio, dream" garde les films qui correspondent à chaque mot
    private boolean matchesQuery(Movie movie) {
        if (isBlank(query)) {
            return true;
        }
        var text = searchableText(movie);
        for (String term : query.toLowerCase().split(",")) {
            if (!term.isBlank() && !text.contains(term.trim())) {
                return false;
            }
        }
        return true;
    }

    private String searchableText(Movie movie) {
        var text = new StringBuilder(movie.getTitle());
        text.append(' ').append(movie.getReleaseYear());
        if (movie.getDirectorName() != null) {
            text.append(' ').append(movie.getDirectorName());
        }
        for (UUID id : movie.getCast()) {
            text.append(' ').append(actorNames.getOrDefault(id, ""));
        }
        for (Genre g : movie.getGenres()) {
            text.append(' ').append(g.getLabel());
        }
        for (String keyword : movie.getKeywords()) {
            text.append(' ').append(keyword);
        }
        return text.toString().toLowerCase();
    }

    private Comparator<Movie> comparator() {
        Comparator<Movie> byTitle = Comparator.comparing(Movie::getTitle, String.CASE_INSENSITIVE_ORDER);
        return switch (sort) {
            case SORT_YEAR -> Comparator.comparingInt(Movie::getReleaseYear).reversed().thenComparing(byTitle);
            case SORT_TITLE -> byTitle;
            // Les films sans note passent après les films notés
            default -> Comparator.comparing((Movie m) -> m.getRatingCount() == 0)
                    .thenComparing(Comparator.comparingDouble(Movie::getRatingAverage).reversed())
                    .thenComparing(byTitle);
        };
    }

    public void toggleGenre(String label) {
        genre = label.equals(genre) ? null : label;
        search();
    }

    public void showAllGenres() {
        genre = null;
        search();
    }

    public boolean isGenreSelected(String label) {
        return label.equals(genre);
    }

    public void sortBy(String order) {
        sort = order;
        search();
    }

    public boolean isSortedBy(String order) {
        return order.equals(sort);
    }

    public void clearFilters() {
        query = null;
        genre = null;
        director = null;
        actor = null;
        search();
    }

    public boolean isFiltered() {
        return !isBlank(query) || genre != null || !isBlank(director) || !isBlank(actor);
    }

    // Seulement les genres présents dans le catalogue, pas les 19 de l'enum
    public List<String> getGenres() {
        var labels = new TreeSet<String>();
        for (Movie movie : allMovies) {
            for (Genre g : movie.getGenres()) {
                labels.add(g.getLabel());
            }
        }
        return new ArrayList<>(labels);
    }

    // Le film le mieux noté, mis en avant en haut de la page
    public Movie getSpotlight() {
        Movie best = null;
        for (Movie movie : allMovies) {
            if (best == null || movie.getRatingAverage() > best.getRatingAverage()) {
                best = movie;
            }
        }
        return best;
    }

    public boolean isSpotlightVisible() {
        return !isFiltered() && getSpotlight() != null;
    }

    public String getResultLabel() {
        int count = movies.size();
        String label = count == 1 ? "1 movie" : count + " movies";
        return isFiltered() ? label + " found" : label;
    }

    public String castLine(Movie movie) {
        var names = new ArrayList<String>();
        for (UUID id : movie.getCast()) {
            if (actorNames.containsKey(id)) {
                names.add(actorNames.get(id));
            }
            if (names.size() == 2) {
                break;
            }
        }
        return String.join(", ", names);
    }

    private static boolean isBlank(String text) {
        return text == null || text.isBlank();
    }

    public List<Movie> getMovies() {
        return movies;
    }

    public int getTotalCount() {
        return allMovies.size();
    }

    public List<Actor> getActors() {
        return actors;
    }

    public List<Director> getDirectors() {
        return directors;
    }

    public boolean isServiceAvailable() {
        return serviceAvailable;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getGenre() {
        return genre;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getActor() {
        return actor;
    }

    public void setActor(String actor) {
        this.actor = actor;
    }

    public String getSort() {
        return sort;
    }
}
