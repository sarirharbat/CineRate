package ch.unil.doplab.cinerate.ui;

import ch.unil.doplab.cinerate.domain.Genre;
import ch.unil.doplab.cinerate.domain.Movie;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

// Klisman Kodra
// Mise en forme des films pour les pages (note, durée, genres, couleur de l'affiche)
@Named
@ApplicationScoped
public class MovieDisplay {
    private static final int POSTER_TONES = 6;
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("d MMM yyyy", Locale.ENGLISH);

    // Sans image, chaque film garde toujours la même couleur d'affiche
    public String tone(Movie movie) {
        return "tone-" + Math.floorMod(Objects.hashCode(movie.getTitle()), POSTER_TONES);
    }

    public boolean hasPoster(Movie movie) {
        return movie.getPosterUrl() != null && !movie.getPosterUrl().isBlank();
    }

    public boolean isRated(Movie movie) {
        return movie.getRatingCount() > 0;
    }

    public String rating(Movie movie) {
        if (!isRated(movie)) {
            return "New";
        }
        return String.format(Locale.ENGLISH, "%.1f", movie.getRatingAverage());
    }

    public String ratingCount(Movie movie) {
        long count = movie.getRatingCount();
        if (count == 0) {
            return "No ratings yet";
        }
        return count == 1 ? "1 rating" : count + " ratings";
    }

    public long ratingPercent(Movie movie) {
        return isRated(movie) ? Math.round(movie.getRatingAverage() * 100 / Movie.maxRating) : 0;
    }

    public List<String> genreLabels(Movie movie) {
        var labels = new ArrayList<String>();
        for (Genre genre : movie.getGenres()) {
            labels.add(genre.getLabel());
        }
        labels.sort(Comparator.naturalOrder());
        return labels;
    }

    public String genres(Movie movie) {
        return String.join(" · ", genreLabels(movie));
    }

    public String duration(int minutes) {
        if (minutes <= 0) {
            return "";
        }
        int hours = minutes / 60;
        int rest = minutes % 60;
        if (hours == 0) {
            return rest + " min";
        }
        return rest == 0 ? hours + "h" : hours + "h " + rest + "m";
    }

    public String date(LocalDateTime date) {
        return date == null ? "" : date.format(DATE_FORMAT);
    }

    public String initials(String name) {
        if (name == null || name.isBlank()) {
            return "?";
        }
        var initials = new StringBuilder();
        for (String part : name.trim().split("\\s+")) {
            initials.append(Character.toUpperCase(part.charAt(0)));
            if (initials.length() == 2) {
                break;
            }
        }
        return initials.toString();
    }
}
