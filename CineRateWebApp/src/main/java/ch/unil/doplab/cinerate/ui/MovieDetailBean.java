package ch.unil.doplab.cinerate.ui;

import ch.unil.doplab.cinerate.CineRateService;
import ch.unil.doplab.cinerate.domain.Actor;
import ch.unil.doplab.cinerate.domain.Genre;
import ch.unil.doplab.cinerate.domain.Movie;
import ch.unil.doplab.cinerate.domain.Review;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.WebApplicationException;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

// Klisman Kodra
// Page MovieDetail : fiche d'un film, casting, avis, watchlist et films similaires
@Named
@ViewScoped
public class MovieDetailBean implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final Logger log = Logger.getLogger(MovieDetailBean.class.getName());
    private static final int MAX_SIMILAR = 4;

    @Inject
    CineRateService theService;

    private Movie movie;
    private List<Actor> cast = new ArrayList<>();
    private List<Review> reviews = new ArrayList<>();
    private List<Movie> similar = new ArrayList<>();
    private boolean inWatchlist;

    @PostConstruct
    public void init() {
        // On arrive ici depuis le Catalogue avec MovieDetail.xhtml?id=...
        var id = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("id");
        if (id == null || id.isBlank()) {
            return;
        }
        try {
            movie = theService.getMovie(id);
        } catch (WebApplicationException | ProcessingException e) {
            log.warning("Movie " + id + " could not be loaded: " + e.getMessage());
            movie = null;
            return;
        }
        loadReviews();
        // Si une partie secondaire échoue, la fiche du film reste affichée
        try {
            loadCast();
            loadSimilar();
            loadWatchlist();
        } catch (WebApplicationException | ProcessingException e) {
            log.warning("Details of movie " + id + " are incomplete: " + e.getMessage());
        }
    }

    private void loadCast() {
        Map<UUID, Actor> actors = new HashMap<>();
        for (Actor actor : theService.getAllActors()) {
            actors.put(actor.getUUID(), actor);
        }
        cast = new ArrayList<>();
        for (UUID id : movie.getCast()) {
            if (actors.containsKey(id)) {
                cast.add(actors.get(id));
            }
        }
    }

    private void loadReviews() {
        reviews = new ArrayList<>(movie.getReviews());
        // Les avis les plus récents en premier
        reviews.sort(Comparator.comparing(Review::getDate,
                Comparator.nullsLast(Comparator.<LocalDateTime>reverseOrder())));
    }

    // Films qui ont au moins un genre en commun, les plus proches d'abord
    private void loadSimilar() {
        var candidates = new ArrayList<Movie>();
        for (Movie other : theService.getAllMovies()) {
            if (!other.equals(movie) && sharedGenres(other) > 0) {
                candidates.add(other);
            }
        }
        candidates.sort(Comparator.comparingInt(this::sharedGenres).reversed()
                .thenComparing(Comparator.comparingDouble(Movie::getRatingAverage).reversed()));
        similar = new ArrayList<>(candidates.subList(0, Math.min(MAX_SIMILAR, candidates.size())));
    }

    private int sharedGenres(Movie other) {
        int count = 0;
        for (Genre genre : other.getGenres()) {
            if (movie.hasGenre(genre)) {
                count++;
            }
        }
        return count;
    }

    private void loadWatchlist() {
        var userId = currentUserId();
        inWatchlist = userId != null && theService.getWatchlist(userId).contains(movie.getUUID());
    }

    public void toggleWatchlist() {
        var userId = currentUserId();
        if (movie == null || userId == null) {
            return;
        }
        try {
            boolean done;
            if (inWatchlist) {
                done = theService.removeFromWatchlist(userId, movie.getUUID().toString());
            } else {
                done = theService.addToWatchlist(userId, movie);
            }
            if (done) {
                inWatchlist = !inWatchlist;
                notify(FacesMessage.SEVERITY_INFO, inWatchlist
                        ? movie.getTitle() + " was added to your watchlist."
                        : movie.getTitle() + " was removed from your watchlist.");
            } else {
                notify(FacesMessage.SEVERITY_ERROR, "Your watchlist could not be updated.");
            }
        } catch (WebApplicationException | ProcessingException e) {
            log.warning("Watchlist update failed: " + e.getMessage());
            notify(FacesMessage.SEVERITY_ERROR, "Your watchlist could not be updated.");
        }
    }

    public boolean isOwnReview(Review review) {
        var userId = currentUserId();
        return userId != null && review.getUserID() != null && userId.equals(review.getUserID().toString());
    }

    private String currentUserId() {
        var session = LoginBean.getSession(false);
        if (session == null || session.getAttribute("uuid") == null) {
            return null;
        }
        return session.getAttribute("uuid").toString();
    }

    private void notify(FacesMessage.Severity severity, String text) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, text, null));
    }

    public Movie getMovie() {
        return movie;
    }

    public boolean isFound() {
        return movie != null;
    }

    public List<Actor> getCast() {
        return cast;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public List<Movie> getSimilar() {
        return similar;
    }

    public boolean isInWatchlist() {
        return inWatchlist;
    }
}
