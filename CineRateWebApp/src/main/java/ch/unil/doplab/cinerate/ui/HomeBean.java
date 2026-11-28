package ch.unil.doplab.cinerate.ui;

import ch.unil.doplab.cinerate.CineRateService;
import ch.unil.doplab.cinerate.domain.Movie;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.WebApplicationException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;

// Klisman Kodra
// Vrais films de la page d'accueil : les mieux notés et les plus critiqués
@Named
@RequestScoped
public class HomeBean {
    private static final Logger log = Logger.getLogger(HomeBean.class.getName());
    private static final int TOP_RATED_SIZE = 4;
    private static final int MOST_REVIEWED_SIZE = 3;

    @Inject
    CineRateService theService;

    private List<Movie> movies = new ArrayList<>();

    @PostConstruct
    public void init() {
        // La page d'accueil doit s'afficher même si le service ne répond pas
        try {
            movies = theService.getAllMovies();
        } catch (WebApplicationException | ProcessingException e) {
            log.warning("Movies for the home page could not be loaded: " + e.getMessage());
        }
    }

    public List<Movie> getTopRated() {
        var sorted = new ArrayList<>(movies);
        sorted.sort(Comparator.comparing((Movie m) -> m.getRatingCount() == 0)
                .thenComparing(Comparator.comparingDouble(Movie::getRatingAverage).reversed())
                .thenComparing(Movie::getTitle, String.CASE_INSENSITIVE_ORDER));
        return first(sorted, TOP_RATED_SIZE);
    }

    public List<Movie> getMostReviewed() {
        var sorted = new ArrayList<>(movies);
        sorted.sort(Comparator.comparingLong(Movie::getRatingCount).reversed()
                .thenComparing(Comparator.comparingDouble(Movie::getRatingAverage).reversed())
                .thenComparing(Movie::getTitle, String.CASE_INSENSITIVE_ORDER));
        return first(sorted, MOST_REVIEWED_SIZE);
    }

    private static List<Movie> first(List<Movie> list, int size) {
        return new ArrayList<>(list.subList(0, Math.min(size, list.size())));
    }
}
