package ch.unil.doplab.cinerate;

import ch.unil.doplab.cinerate.domain.*;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

// Mouhamed Sarira
@ApplicationScoped
public class CineRateService {
    private static final String BASE_URL = "http://localhost:8080/CineRateService-1.0-SNAPSHOT/api";
    private WebTarget userTarget;
    private WebTarget serviceTarget;
    private WebTarget actorTarget;
    private WebTarget directorTarget;
    private WebTarget genreTarget;
    private WebTarget movieTarget;

    @PostConstruct
    public void init() {
        Client client = ClientBuilder.newClient();
        userTarget = client.target(BASE_URL).path("users");
        serviceTarget = client.target(BASE_URL).path("service");
        actorTarget = client.target(BASE_URL).path("actors");
        directorTarget = client.target(BASE_URL).path("directors");
        genreTarget = client.target(BASE_URL).path("genres");
        movieTarget = client.target(BASE_URL).path("movies");
    }

    public void resetService() {
        serviceTarget
                .path("reset")
                .request()
                .get(String.class);
    }

    public List<User> getAllUsers() {
        var users = userTarget
                .request(MediaType.APPLICATION_JSON)
                .get(new GenericType<List<User>>() {
                });
        return users;
    }

    public User getUser(String id) {
        var user = userTarget
                .path(id)
                .request()
                .get(User.class);
        return user;
    }

    public User addUser(User user) throws Exception {
        user.setUUID(null);
        var response = userTarget
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(user, MediaType.APPLICATION_JSON));
        if (response.getStatus() == Response.Status.CREATED.getStatusCode()) {
            return response.readEntity(User.class);
        } else {
            ExceptionDescription description = response.readEntity(ExceptionDescription.class);
            throw Utils.buildException(description);
        }
    }

    public boolean setUser(User user) throws Exception {
        var response = userTarget
                .path(user.getUUID().toString())
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.entity(user, MediaType.APPLICATION_JSON));
        if (response.getStatus() == Response.Status.OK.getStatusCode()) {
            return true;
        } else {
            ExceptionDescription description = response.readEntity(ExceptionDescription.class);
            throw Utils.buildException(description);
        }
    }

    public boolean deleteUser(String id) {
        var response = userTarget
                .path(id)
                .request(MediaType.APPLICATION_JSON)
                .delete();
        return response.getStatus() == Response.Status.OK.getStatusCode();
    }

    public User login(String username, String password) {
        var credentials = new User();
        credentials.setUsername(username);
        credentials.setPassword(password);
        var response = userTarget
                .path("login")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(credentials, MediaType.APPLICATION_JSON));
        if (response.getStatus() == Response.Status.OK.getStatusCode()) {
            return response.readEntity(User.class);
        }
        return null;
    }

    public List<Actor> getAllActors() {
        var actors = actorTarget
                .request(MediaType.APPLICATION_JSON)
                .get(new GenericType<List<Actor>>() {
                });
        return actors;
    }

    public List<Director> getAllDirectors() {
        var directors = directorTarget
                .request(MediaType.APPLICATION_JSON)
                .get(new GenericType<List<Director>>() {
                });
        return directors;
    }

    public List<String> getAllGenres() {
        var genres = genreTarget
                .request(MediaType.APPLICATION_JSON)
                .get(new GenericType<List<String>>() {
                });
        return genres;
    }

    public List<Movie> getAllMovies() {
        var movies = movieTarget
                .request(MediaType.APPLICATION_JSON)
                .get(new GenericType<List<Movie>>() {
                });
        return movies;
    }

    public List<Movie> searchMovies(String title, String actor, String director, String genre, String keyword) {
        var actors = getAllActors();
        var directors = getAllDirectors();
        var movies = new ArrayList<Movie>();
        for (Movie movie : getAllMovies()) {
            boolean found = true;
            if (!isEmpty(title) && !movie.getTitle().toLowerCase().contains(title.toLowerCase())) {
                found = false;
            }
            if (!isEmpty(actor) && !playsIn(actors, actor, movie)) {
                found = false;
            }
            if (!isEmpty(director) && !directs(directors, director, movie)) {
                found = false;
            }
            if (!isEmpty(genre) && !movie.hasGenre(Genre.fromLabel(genre))) {
                found = false;
            }
            if (!isEmpty(keyword) && !movie.hasKeyword(keyword)) {
                found = false;
            }
            if (found) {
                movies.add(movie);
            }
        }
        return movies;
    }

    public Movie getMovie(String id) {
        var movie = movieTarget
                .path(id)
                .request()
                .get(Movie.class);
        return movie;
    }

    public Movie addMovie(Movie movie) throws Exception {
        var response = movieTarget
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(movie, MediaType.APPLICATION_JSON));
        if (response.getStatus() == Response.Status.CREATED.getStatusCode()) {
            return response.readEntity(Movie.class);
        } else {
            ExceptionDescription description = response.readEntity(ExceptionDescription.class);
            throw Utils.buildException(description);
        }
    }

    public boolean setMovie(Movie movie) throws Exception {
        var response = movieTarget
                .path(movie.getUUID().toString())
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.entity(movie, MediaType.APPLICATION_JSON));
        if (response.getStatus() == Response.Status.OK.getStatusCode()) {
            return true;
        } else {
            ExceptionDescription description = response.readEntity(ExceptionDescription.class);
            throw Utils.buildException(description);
        }
    }

    public boolean deleteMovie(String id) {
        var response = movieTarget
                .path(id)
                .request(MediaType.APPLICATION_JSON)
                .delete();
        return response.getStatus() == Response.Status.OK.getStatusCode();
    }

    public List<Review> getReviews(String userId) {
        var reviews = userTarget
                .path(userId)
                .path("reviews")
                .request(MediaType.APPLICATION_JSON)
                .get(new GenericType<List<Review>>() {
                });
        return reviews;
    }

    public Review getReview(String userId, String movieId) {
        var review = userTarget
                .path(userId)
                .path("reviews")
                .path(movieId)
                .request()
                .get(Review.class);
        return review;
    }

    public Review addReview(String userId, String movieId, Review review) throws Exception {
        var response = userTarget
                .path(userId)
                .path("reviews")
                .path(movieId)
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(review, MediaType.APPLICATION_JSON));
        if (response.getStatus() == Response.Status.CREATED.getStatusCode()) {
            return response.readEntity(Review.class);
        } else {
            ExceptionDescription description = response.readEntity(ExceptionDescription.class);
            throw Utils.buildException(description);
        }
    }

    public boolean setReview(String userId, String movieId, Review review) throws Exception {
        var response = userTarget
                .path(userId)
                .path("reviews")
                .path(movieId)
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.entity(review, MediaType.APPLICATION_JSON));
        if (response.getStatus() == Response.Status.OK.getStatusCode()) {
            return true;
        } else {
            ExceptionDescription description = response.readEntity(ExceptionDescription.class);
            throw Utils.buildException(description);
        }
    }

    public boolean deleteReview(String userId, String movieId) {
        var response = userTarget
                .path(userId)
                .path("reviews")
                .path(movieId)
                .request(MediaType.APPLICATION_JSON)
                .delete();
        return response.getStatus() == Response.Status.OK.getStatusCode();
    }

    public List<UUID> getWatchlist(String userId) {
        var watchlist = userTarget
                .path(userId)
                .path("watchlist")
                .request(MediaType.APPLICATION_JSON)
                .get(new GenericType<List<UUID>>() {
                });
        return watchlist;
    }

    public List<UUID> getWatchedMovies(String userId) {
        var watched = userTarget
                .path(userId)
                .path("watchlist")
                .path("watched")
                .request(MediaType.APPLICATION_JSON)
                .get(new GenericType<List<UUID>>() {
                });
        return watched;
    }

    public boolean addToWatchlist(String userId, Movie movie) {
        var response = userTarget
                .path(userId)
                .path("watchlist")
                .path(movie.getUUID().toString())
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(movie, MediaType.APPLICATION_JSON));
        return response.getStatus() == Response.Status.OK.getStatusCode();
    }

    public boolean removeFromWatchlist(String userId, String movieId) {
        var response = userTarget
                .path(userId)
                .path("watchlist")
                .path(movieId)
                .request(MediaType.APPLICATION_JSON)
                .delete();
        return response.getStatus() == Response.Status.OK.getStatusCode();
    }

    public boolean markAsWatched(String userId, Movie movie) {
        var response = userTarget
                .path(userId)
                .path("watchlist")
                .path(movie.getUUID().toString())
                .path("watched")
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.entity(movie, MediaType.APPLICATION_JSON));
        return response.getStatus() == Response.Status.OK.getStatusCode();
    }

    public boolean markAsUnwatched(String userId, String movieId) {
        var response = userTarget
                .path(userId)
                .path("watchlist")
                .path(movieId)
                .path("watched")
                .request(MediaType.APPLICATION_JSON)
                .delete();
        return response.getStatus() == Response.Status.OK.getStatusCode();
    }

    private boolean playsIn(List<Actor> actors, String name, Movie movie) {
        for (Actor actor : actors) {
            if (hasName(actor, name) && movie.hasActor(actor.getUUID())) {
                return true;
            }
        }
        return false;
    }

    private boolean directs(List<Director> directors, String name, Movie movie) {
        for (Director director : directors) {
            if (hasName(director, name) && movie.isDirectedBy(director.getUUID())) {
                return true;
            }
        }
        return false;
    }

    private boolean hasName(Person person, String name) {
        var fullName = person.getFirstName() + " " + person.getLastName();
        return fullName.toLowerCase().contains(name.toLowerCase());
    }

    private boolean isEmpty(String text) {
        return text == null || text.isBlank();
    }
}
