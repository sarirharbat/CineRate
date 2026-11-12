package ch.unil.doplab.cinerate.domain;

import jakarta.json.bind.annotation.JsonbTypeDeserializer;

import java.util.List;

/**
 * Personne 3 : corps des requetes POST /movies et PUT /movies/{id}.
 *
 * Il ne contient que les champs modifiables par un client. Les valeurs calculees
 * par le service (UUID, nom du realisateur, moyenne et nombre de notes, avis)
 * ne peuvent pas etre ecrites : elles sont lues depuis le Movie stocke.
 *
 * Les identifiants et les genres sont des String pour que le service puisse
 * repondre 400 avec un message clair quand une valeur est invalide.
 */
public class MovieData {

    private String title;
    // lecture stricte : 2000.5 ou 99999999999 donnent 400 au lieu d'etre tronques
    @JsonbTypeDeserializer(WholeNumberDeserializer.class)
    private Integer releaseYear;
    @JsonbTypeDeserializer(WholeNumberDeserializer.class)
    private Integer duration;
    private String synopsis;
    private String posterUrl;
    private List<String> genres;
    private String directorID;
    private List<String> cast;
    private List<String> keywords;

    public MovieData() {
    }

    public MovieData(String title, Integer releaseYear) {
        this.title = title;
        this.releaseYear = releaseYear;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(Integer releaseYear) {
        this.releaseYear = releaseYear;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }

    public List<String> getGenres() {
        return genres;
    }

    public void setGenres(List<String> genres) {
        this.genres = genres;
    }

    public String getDirectorID() {
        return directorID;
    }

    public void setDirectorID(String directorID) {
        this.directorID = directorID;
    }

    public List<String> getCast() {
        return cast;
    }

    public void setCast(List<String> cast) {
        this.cast = cast;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }
}
