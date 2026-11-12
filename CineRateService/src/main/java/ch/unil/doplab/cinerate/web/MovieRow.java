package ch.unil.doplab.cinerate.web;

import java.io.Serializable;
import java.util.UUID;

/**
 * One movie as shown in the catalogue page: only text and numbers, ready to display.
 * (A plain class with getters, because the EL of Jakarta EE 10 cannot read records.)
 */
public class MovieRow implements Serializable {

    private final UUID id;
    private final String title;
    private final int releaseYear;
    private final int duration;
    private final String genres;
    private final String director;
    private final String cast;
    private final String keywords;
    private final String synopsis;
    private final double ratingAverage;
    private final long ratingCount;
    private final String formattedRating;

    public MovieRow(UUID id, String title, int releaseYear, int duration, String genres, String director,
                    String cast, String keywords, String synopsis, double ratingAverage, long ratingCount,
                    String formattedRating) {
        this.id = id;
        this.title = title;
        this.releaseYear = releaseYear;
        this.duration = duration;
        this.genres = genres;
        this.director = director;
        this.cast = cast;
        this.keywords = keywords;
        this.synopsis = synopsis;
        this.ratingAverage = ratingAverage;
        this.ratingCount = ratingCount;
        this.formattedRating = formattedRating;
    }

    public UUID getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public int getReleaseYear() {
        return releaseYear;
    }

    public int getDuration() {
        return duration;
    }

    public String getGenres() {
        return genres;
    }

    public String getDirector() {
        return director;
    }

    public String getCast() {
        return cast;
    }

    public String getKeywords() {
        return keywords;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public double getRatingAverage() {
        return ratingAverage;
    }

    public long getRatingCount() {
        return ratingCount;
    }

    public String getFormattedRating() {
        return formattedRating;
    }
}
