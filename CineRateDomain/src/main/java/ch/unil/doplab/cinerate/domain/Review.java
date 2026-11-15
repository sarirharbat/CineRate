package ch.unil.doplab.cinerate.domain;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

// Elias Mounerat
public class Review {

    public static final int maxCommentLength = 1000;

    private UUID uuid;
    private UUID userID;
    private UUID movieID;
    private String username;
    private String movieTitle;
    private Rating rating;
    private String comment;
    private LocalDateTime date;

    public Review() {
        this(null, Rating.NO_RATING, null);
    }

    public Review(Rating rating, String comment) {
        this(null, rating, comment);
    }

    public Review(UUID uuid, Rating rating, String comment) {
        this.uuid = uuid;
        this.rating = rating;
        this.comment = comment;
        this.userID = null;
        this.movieID = null;
        this.username = null;
        this.movieTitle = null;
        this.date = null;
    }

    public UUID getUUID() {
        return uuid;
    }

    public void setUUID(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getUserID() {
        return userID;
    }

    public void setUserID(UUID userID) {
        this.userID = userID;
    }

    public UUID getMovieID() {
        return movieID;
    }

    public void setMovieID(UUID movieID) {
        this.movieID = movieID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public void setMovieTitle(String movieTitle) {
        this.movieTitle = movieTitle;
    }

    public void setUser(User user) {
        this.userID = user.getUUID();
        this.username = user.getUsername();
    }

    public void setMovie(Movie movie) {
        this.movieID = movie.getUUID();
        this.movieTitle = movie.getTitle();
    }

    public int getRating() {
        return rating == null ? Rating.NO_RATING.getValue() : rating.getValue();
    }

    public void setRating(int value) {
        this.rating = Rating.fromValue(value);
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public boolean isPosted() {
        return date != null;
    }

    private void checkContent(Rating rating, String comment) {
        if (rating == null || rating.getValue() < Movie.minRating || rating.getValue() > Movie.maxRating) {
            throw new IllegalArgumentException("Rating must be between " + Movie.minRating + " and " + Movie.maxRating);
        }
        if (comment != null && comment.length() > maxCommentLength) {
            throw new IllegalArgumentException("Comment must not be longer than " + maxCommentLength + " characters");
        }
    }

    private void checkAuthorAndMovie(User user, Movie movie) {
        if (!Objects.equals(userID, user.getUUID())) {
            throw new IllegalStateException("Review is not written by " + user.getUsername());
        }
        if (!Objects.equals(movieID, movie.getUUID())) {
            throw new IllegalStateException("Review is not about " + movie.getTitle());
        }
        if (user.getReview(movieID) == null || movie.getReview(userID) == null) {
            throw new IllegalStateException("Review of " + user.getUsername() + " about " + movie.getTitle() + " was not found");
        }
    }

    public void post(User user, Movie movie) {
        if (user == null || movie == null) {
            throw new IllegalArgumentException("User or movie cannot be null");
        }
        if (isPosted()) {
            throw new IllegalStateException("Review has already been posted");
        }
        checkContent(rating, comment);
        if (user.hasReviewed(movie.getUUID()) || movie.hasReviewFrom(user.getUUID())) {
            throw new IllegalStateException(user.getUsername() + " has already reviewed " + movie.getTitle());
        }
        setUser(user);
        setMovie(movie);
        this.date = LocalDateTime.now();
        user.putReview(this);
        movie.putReview(this);
        movie.rate(rating.getValue());
    }

    public void edit(User user, Movie movie, Rating newRating, String newComment) {
        checkAuthorAndMovie(user, movie);
        checkContent(newRating, newComment);
        Review stored = movie.getReview(userID);
        movie.unrate(stored.rating.getValue());
        movie.rate(newRating.getValue());
        stored.rating = newRating;
        stored.comment = newComment;
        this.rating = newRating;
        this.comment = newComment;
    }

    public void delete(User user, Movie movie) {
        checkAuthorAndMovie(user, movie);
        Review stored = movie.getReview(userID);
        user.removeReview(this);
        movie.removeReview(this);
        movie.unrate(stored.rating.getValue());
    }

    @Override
    public String toString() {
        return "Review{" +
                "id=" + uuid +
                ", userID=" + userID +
                ", username='" + username + '\'' +
                ", movieID=" + movieID +
                ", movieTitle='" + movieTitle + '\'' +
                ", rating=" + rating +
                ", comment='" + comment + '\'' +
                ", date=" + date +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Review review)) return false;
        return Objects.equals(uuid, review.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }
}

